package com.avinab.trusssolver.data;

import android.graphics.Canvas;
import android.util.Log;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.analysis.ForceVector;
import com.avinab.trusssolver.analysis.StiffnessMatrix;
import com.avinab.trusssolver.data.supports.HingeSupport;
import com.avinab.trusssolver.data.supports.RollerSupport;
import com.avinab.trusssolver.data.supports.Support;
import com.avinab.trusssolver.math.Matrix;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.Drawable;
import com.avinab.trusssolver.widgets.ViewControl;

import java.util.ArrayList;


/**
 * Avinab
 * 11/11/2014.
 */
public class Truss implements Drawable
{
	public String Name = Global.TRUSS_DATA_CACHE;

	public boolean Solved = false;
	public boolean Modified = false;

	public ArrayList<Node> Nodes;
	public ArrayList<Member> Members;

	public ArrayList<Load> PointLoads;


	public ArrayList<HingeSupport> HingeSupports;
	public ArrayList<RollerSupport> RollerSupports;


	public int NEXT_NODE_ID = 1;
	public int NEXT_MEMBER_ID = 1;
	public int NEXT_SECTION_ID = 1;
	public ArrayList<String> messages;

	public Truss(String name)
	{
		Name = name;
		Nodes = new ArrayList<>();
		Members = new ArrayList<>();

		HingeSupports = new ArrayList<>();
		RollerSupports = new ArrayList<>();

		PointLoads = new ArrayList<>();
	}

	@Override
	public void Draw(Canvas canvas, ViewControl viewControl)
	{

		if (!Solved)
		{
			for (Member e : Members)
			{
				e.Draw(canvas, viewControl);
			}
		} else
		{
			double maxTF = Double.NEGATIVE_INFINITY, minTF = Double.POSITIVE_INFINITY;
			double maxCF = Double.NEGATIVE_INFINITY, minCF = Double.POSITIVE_INFINITY;
			// get max & min forces
			for (Member m : Members)
			{
				if (m.force > 0)
				{
					if (m.force > maxTF)
					{
						maxTF = m.force;
					}
					if (m.force < minTF)
					{
						minTF = m.force;
					}
				} else if (m.force < 0)
				{
					if (-m.force > maxCF)
					{
						maxCF = -m.force;
					}
					if (-m.force < minCF)
					{
						minCF = -m.force;
					}
				}
			}

			for (Member m : Members)
			{
				m.DrawSolvedMember(viewControl, canvas, maxTF, maxCF, minTF, minCF);
			}

		}

		for (HingeSupport s : HingeSupports)
		{
			s.Draw(canvas, viewControl);
			if (Solved) s.DrawReactions(canvas, viewControl);
		}

		for (RollerSupport s : RollerSupports)
		{
			s.Draw(canvas, viewControl);
			if (Solved) s.DrawReactions(canvas, viewControl);
		}


		for (Load load : PointLoads)
		{
			load.Draw(canvas, viewControl);
		}

		for (Node n : Nodes)
		{
			n.Draw(canvas, viewControl);
		}
	}

	@Override
	public Bounds getBounds(ViewControl viewControl)
	{
		Bounds ret = null;

		for (Node n : Nodes)
		{
			if (ret == null)
			{
				ret = n.getBounds(viewControl);
			} else
			{
				ret.add(n.getBounds(viewControl));
			}
		}

		if (ret == null) return new Bounds(-250, -250, 250, 250);
		for (Member e : Members)
		{
			ret.add(e.getBounds(viewControl));
		}


		for (HingeSupport s : HingeSupports)
		{
			ret.add(s.getBounds(viewControl));
		}

		for (RollerSupport s : RollerSupports)
		{
			ret.add(s.getBounds(viewControl));
		}


		for (Load load : PointLoads)
		{
			ret.add(load.getBounds(viewControl));
		}


		return ret;
	}

	public Vector2D getCenter()
	{
		int count = 0;
		Vector2D ret = new Vector2D(0, 0);
		for (Node n : Nodes)
		{
			ret = ret.Add(n.Location);
			count++;
		}

		return ret.multiply(1d / count);
	}

	/**
	 * Add a new node to the truss.
	 *
	 * @param location
	 */
	public void AddNode(Vector2D location)
	{
		Nodes.add(new Node(this, location));
		Solved = false;
		Modified = true;
	}

	public Node getNode(int id)
	{
		for (Node n : Nodes)
		{
			if (n.id == id)
			{
				return n;
			}
		}
		return null;
	}

	public void EditNode(int id, Vector2D location)
	{
		Node n = getNode(id);

		n.Location = location;
		Solved = false;
		Modified = true;
	}

	public void EditMember(int id, double E, double A)
	{
		Member mem = getMember(id);
		mem.Emod = E;
		mem.Area = A;
		Solved = false;
		Modified = true;
	}

	public void AddMember(int StartNode, int EndNode, double E, double A)
	{
		Solved = false;
		Modified = true;
		for (Member m : Members)
		{
			if ((m.FromNodeID == StartNode && m.ToNodeID == EndNode) || (m.ToNodeID == StartNode && m.FromNodeID == EndNode))
			{
				m.Emod = E;
				m.Area = A;
				return;
			}
		}
		Members.add(new Member(this, StartNode, EndNode, E, A));

	}

	public Member getMember(int id)
	{
		for (Member e : Members)
		{
			if (e.id == id)
			{
				return e;
			}
		}
		return null;
	}

	public void AddHingeSupport(Node n)
	{
		RemoveSupportFromNode(n);
		HingeSupports.add(new HingeSupport(n));
		Solved = false;
		Modified = true;
	}

	public void AddRollerSupport(Node n, boolean horz)
	{
		RemoveSupportFromNode(n);
		RollerSupports.add(new RollerSupport(n, horz));
		Solved = false;
		Modified = true;
	}

	public void UnselectAll()
	{
		for (Node n : Nodes)
		{
			n.Unselect();
		}

		for (Member e : Members)
		{
			e.Unselect();
		}


		for (Load dl : PointLoads)
		{
			dl.Unselect();
		}

		this.Modified = true;
	}

	public void RemoveSupportFromNode(Node n)
	{
		int i;

		i = getNodeHingeSupportIndex(n);
		if (i != -1)
		{
			HingeSupports.remove(i);
			return;
		}

		i = getNodeRollerSupportIndex(n);
		if (i != -1)
		{
			RollerSupports.remove(i);
		}

	}

	public Support GetSupportOfNode(Node n)
	{
		int i;

		i = getNodeHingeSupportIndex(n);
		if (i != -1)
		{
			return HingeSupports.get(i);
		}

		i = getNodeRollerSupportIndex(n);
		if (i != -1)
		{
			return RollerSupports.get(i);
		}
		return null;
	}

	private int getNodeHingeSupportIndex(Node n)
	{
		for (HingeSupport h : HingeSupports)
		{
			if (h.getNode().equals(n))
			{
				return HingeSupports.indexOf(h);
			}
		}
		return -1;
	}

	private int getNodeRollerSupportIndex(Node n)
	{
		for (RollerSupport h : RollerSupports)
		{
			if (h.getNode().equals(n))
			{
				return RollerSupports.indexOf(h);
			}
		}
		return -1;
	}

	public void addPointLoad(Node n, double mag, double ang)
	{
		PointLoads.add(new Load(this, n, mag, ang));
		Solved = false;
		Modified = true;
	}

	public void DeleteSelected()
	{
		ArrayList<Node> r_node = new ArrayList<>();
		ArrayList<Member> r_member = new ArrayList<>();
		ArrayList<Load> r_pl = new ArrayList<>();

		for (Node n : Nodes)
		{
			if (n.isSelected())
			{
				n.deleteDependencies();
				r_node.add(n);
			}

		}

		for (Member e : Members)
		{
			if (e.isSelected())
			{
				r_member.add(e);
			}
		}


		for (Load dl : PointLoads)
		{
			if (dl.isSelected())
			{
				r_pl.add(dl);
			}
		}


		this.Members.removeAll(r_member);
		this.Nodes.removeAll(r_node);
		this.PointLoads.removeAll(r_pl);


		Solved = false;
		Modified = true;
	}

	public Object getFirstSelectedItem()
	{
		for (Node n : Nodes)
		{
			if (n.isSelected()) return n;
		}

		for (Member e : Members)
		{
			if (e.isSelected()) return e;
		}

		for (Load pl : PointLoads)
		{
			if (pl.isSelected()) return pl;
		}


		return null;
	}

	/**
	 * Delete EVERYTHING from this truss
	 */
	public void Reset()
	{
		Name = Global.TRUSS_DATA_CACHE;
		Nodes = new ArrayList<Node>();
		Members = new ArrayList<Member>();

		HingeSupports = new ArrayList<HingeSupport>();
		RollerSupports = new ArrayList<RollerSupport>();

		PointLoads = new ArrayList<Load>();
		Solved = false;
		Modified = true;
		NEXT_MEMBER_ID = 1;
		NEXT_NODE_ID = 1;
		NEXT_SECTION_ID = 1;
	}


	/*
	Solver
	 */

	public boolean solve()
	{
		long time = System.nanoTime();

		resetAllData();
		if (!checkTruss()) return false;

		//Create Stiffness Matrix and Force Vector
		StiffnessMatrix stiffnessMatrix = new StiffnessMatrix(this);
		StiffnessMatrix RestrictedStiffnessMatrix = new StiffnessMatrix(stiffnessMatrix);
		RestrictedStiffnessMatrix.Restrain(this);


		Matrix displacementVector;

		try
		{
			ForceVector fVec = new ForceVector(this);


			//Solve for displacements
			displacementVector = RestrictedStiffnessMatrix.solve(fVec);
		} catch (Exception ex)
		{
			if (ex.getMessage().contains("singular"))
			{
				Log.i("SOLVER", "Check Stability");
			}
			return false;
		}

		//stiffnessMatrix.show();
		double displacements[][] = displacementVector.getData();

		for (int i = 0; i < Nodes.size(); i++)
		{
			Nodes.get(i).displacement = new Vector2D(displacements[i * 2][0], displacements[i * 2 + 1][0]);
			Log.i("Disp", i + ":" + displacements[i * 2][0] + "," + displacements[i * 2 + 1][0]);
		}


		//Solve form member forces
		for (Member m : Members)
		{
			double dx = m.getToNode().displacement.X - m.getFromNode().displacement.X;
			double dy = m.getToNode().displacement.Y - m.getFromNode().displacement.Y;

			m.force = m.Stiffness() * (dx * m.Cos() + dy * m.Sin());
		}

		//Find Reactions
		double Forces[][] = stiffnessMatrix.times(displacementVector).getData();
		for (HingeSupport hs : HingeSupports)
		{
			int index = Nodes.indexOf(hs.getNode());
			double Rx = Forces[2 * index][0];
			double Ry = Forces[2 * index + 1][0];
			hs.Reaction = new Vector2D(Rx, Ry).Subtract(hs.getNode().getTotalLoad());
		}

		for (RollerSupport rs : RollerSupports)
		{
			int index = Nodes.indexOf(rs.getNode());
			double Rx = Forces[2 * index][0];
			double Ry = Forces[2 * index + 1][0];
			rs.Reaction = new Vector2D(Rx, Ry).Subtract(rs.getNode().getTotalLoad());

		}

		Solved = true;
		Modified = true;
		long time2 = System.nanoTime();

		System.out.println("Solved in " + (time2 - time) + " ns");

		return true;
	}

	public void resetAllData()
	{
		for (HingeSupport hs : HingeSupports)
		{
			hs.Reaction = new Vector2D(0, 0);
		}
		for (RollerSupport rs : RollerSupports)
		{
			rs.Reaction = new Vector2D(0, 0);
		}


		for (Member m : Members)
		{
			m.force = 0;
			m.unknown = true;
		}
	}

	private boolean checkTruss()
	{

		messages = new ArrayList<String>();
		// check no. of nodes
		if (Nodes.size() < 3)
		{
			messages.add("Insufficient Nodes");
		}

		// check presence of both supports
		if ((HingeSupports.size() == 0) || ((HingeSupports.size() == 1) && (RollerSupports.size() == 0)))
		{
			messages.add("Insufficient Supports");
		}


		// check loads
		if (PointLoads.size() == 0)
		{
			messages.add("No loads present.");
		}

		return (messages.size() == 0);
	}
}
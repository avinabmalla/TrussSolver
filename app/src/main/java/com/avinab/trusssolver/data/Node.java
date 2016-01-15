package com.avinab.trusssolver.data;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.data.supports.HingeSupport;
import com.avinab.trusssolver.data.supports.RollerSupport;
import com.avinab.trusssolver.data.supports.Support;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.Drawable;
import com.avinab.trusssolver.widgets.ViewControl;

import java.util.ArrayList;


/**
 * Avinab
 * 11/11/2014.
 */
public class Node implements Drawable
{
	static Paint paint;
	static Paint selectedPaint;
	public final int id;
	public Truss Parent;
	public Vector2D Location;
	public String info = "";
	public Vector2D displacement;
	boolean Selected;

	public Node(Truss truss, Vector2D location)
	{
		//Set Parent
		Parent = truss;


		//Incrementally set id for each new node
		this.id = Parent.NEXT_NODE_ID;
		Parent.NEXT_NODE_ID++;

		//set location and type
		this.Location = location;

		initializePaints();
	}

	public Node(Truss truss, int id, double x, double y)
	{
		Parent = truss;
		this.id = id;
		this.Location = new Vector2D(x, y);
		if (id + 1 > truss.NEXT_NODE_ID)
		{
			truss.NEXT_NODE_ID = id + 1;
		}
		initializePaints();
	}

	private void initializePaints()
	{
		paint = new Paint();
		selectedPaint = new Paint();

		paint.setTextSize(25);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		paint.setAntiAlias(true);
		paint.setStrokeWidth(4f);

		paint.setColor(Color.rgb(180, 180, 255));

		selectedPaint.setTextSize(25);
		selectedPaint.setStyle(Paint.Style.FILL);
		selectedPaint.setTextAlign(Paint.Align.CENTER);
		selectedPaint.setStrokeWidth(4f);
		selectedPaint.setAntiAlias(true);
		selectedPaint.setColor(Color.GREEN);
		selectedPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

	}

	@Override
	public void Draw(Canvas canvas, ViewControl viewControl)
	{
		Vector2D p1 = viewControl.toDrawableCoord(Location);
		Paint pnt = new Paint(Selected ? selectedPaint : paint);


		pnt.setStyle(Paint.Style.FILL);
		canvas.drawCircle((float) p1.X, (float) p1.Y, 7f, pnt);
		canvas.drawText(id + "  " + info, (float) p1.X + 10, (float) p1.Y - 7, pnt);


	}

	public String getText()
	{
		String ret = "Node ID: " + id + "\r\nX: " + String.format("%.4G", Location.X) + "\r\nY: " + String.format("%.4G", Location.Y) + "\nSupport: ";
		Support s = Parent.GetSupportOfNode(this);
		if (s instanceof HingeSupport)
		{
			ret += "Hinged";
		} else if (s instanceof RollerSupport)
		{
			ret += "Roller";
		} else if (s == null)
		{
			ret += "None";
		}
		if (Parent.Solved)
		{
			ret += "\nDisplacements:\nX:" + String.format("%.4G", displacement.X * 1000) + "mm\nY:" + String.format("%.4G", displacement.Y * 1000) + "mm";
		}
		return ret;
	}

	@Override
	public Bounds getBounds(ViewControl viewControl)
	{
		return new Bounds(Location);
	}

	public void Select()
	{
		this.Selected = true;
		Parent.Modified = true;
	}

	public void Unselect()
	{
		this.Selected = false;
		Parent.Modified = true;
	}

	public boolean isSelected()
	{
		return Selected;
	}

	public void deleteDependencies()
	{
		ArrayList<Member> r_Members = new ArrayList<>();
		ArrayList<HingeSupport> r_h = new ArrayList<>();
		ArrayList<RollerSupport> r_r = new ArrayList<>();
		ArrayList<Load> r_l = new ArrayList<>();


		for (Member member : Parent.Members)
		{
			if (member.getFromNode().id == this.id || member.getToNode().id == this.id)
			{
				r_Members.add(member);
			}
		}

		Parent.Members.removeAll(r_Members);

		for (HingeSupport hs : Parent.HingeSupports)
		{
			if (hs.getNode().id == this.id)
			{
				r_h.add(hs);
			}
		}

		for (RollerSupport r : Parent.RollerSupports)
		{
			if (r.getNode().id == this.id)
			{
				r_r.add(r);
			}
		}

		for (Load l : Parent.PointLoads)
		{
			if (l.getNode().id == this.id)
			{
				r_l.add(l);
			}
		}


		Parent.HingeSupports.removeAll(r_h);
		Parent.RollerSupports.removeAll(r_r);
		Parent.PointLoads.removeAll(r_l);
	}


	public Vector2D getTotalLoad()
	{
		Vector2D ret = new Vector2D(0, 0);
		for (Load l : Global.CurrentTruss.PointLoads)
		{
			if (l.getNode() == this)
			{
				ret = ret.Add(l.force);
			}
		}
		return ret;
	}

}

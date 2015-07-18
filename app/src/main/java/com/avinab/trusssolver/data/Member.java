package com.avinab.trusssolver.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import com.avinab.trusssolver.analysis.MemberStiffnessMatrix;
import com.avinab.trusssolver.math.Matrix2x2;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.Drawable;
import com.avinab.trusssolver.widgets.TrussView;

import java.text.DecimalFormat;


/**
 * Avinab
 * 11/15/2014.
 */
public class Member implements Drawable
{

	static Paint paint;
	static Paint sPaint;
	public final int FromNodeID;
	public final int ToNodeID;
	public final int id;
	public Truss Parent;
	public double Emod = 200; //Young's Modulus, in GPa
	public double Area = 100; //Area, in sq. mm
	public boolean unknown = true;
	public double force = 0;
	private boolean Selected = false;

	public Member(Truss truss, int id, int from, int to, double E, double A)
	{
		Parent = truss;
		this.id = id;
		FromNodeID = from;
		ToNodeID = to;
		if (Parent.NEXT_MEMBER_ID < id + 1)
		{
			Parent.NEXT_MEMBER_ID = id + 1;
		}
		Emod = E;
		Area = A;
	}

	public Member(Truss truss, int from, int to, double E, double A)
	{
		this(truss, truss.getNode(from), truss.getNode(to), E, A);
	}


	public Member(Truss truss, Node from, Node to, double E, double A)
	{
		Parent = truss;

		if (from.id < to.id)
		{
			FromNodeID = from.id;
			ToNodeID = to.id;
		} else
		{
			FromNodeID = to.id;
			ToNodeID = from.id;
		}

		Emod = E;
		Area = A;
		//Incrementally set id for each new node
		this.id = Parent.NEXT_MEMBER_ID;
		Parent.NEXT_MEMBER_ID++;
	}


	public Node getFromNode()
	{
		return Parent.getNode(FromNodeID);
	}

	public Node getToNode()
	{
		return Parent.getNode(ToNodeID);
	}

	void setupPaint()
	{
		if (paint == null)
		{
			paint = new Paint();
			paint.setTextSize(20);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextAlign(Paint.Align.CENTER);
			paint.setStrokeWidth(4f);
			paint.setAntiAlias(true);
			paint.setColor(Color.argb(200, 210, 210, 210));

			sPaint = new Paint(paint);
			sPaint.setStrokeWidth(6f);
			sPaint.setColor(Color.GREEN);
		}
	}

	@Override
	public void Draw(Canvas canvas, TrussView trussView)
	{
		Vector2D p1 = trussView.toDrawableCoord(getFromNode().Location);
		Vector2D p2 = trussView.toDrawableCoord(getToNode().Location);

		setupPaint();
		if (Selected)
		{
			canvas.drawLine((float) p1.X, (float) p1.Y, (float) p2.X, (float) p2.Y, sPaint);
		} else
		{
			canvas.drawLine((float) p1.X, (float) p1.Y, (float) p2.X, (float) p2.Y, paint);
		}
	}

	@Override
	public Bounds getBounds(TrussView trussView)
	{
		Bounds ret = getFromNode().getBounds(trussView);
		ret.add(getToNode().getBounds(trussView));
		return ret;
	}


	public double DistanceFromPoint(double X, double Y, TrussView m)
	{
		Vector2D A = m.toDrawableCoord(getFromNode().Location);
		Vector2D B = m.toDrawableCoord(getToNode().Location);
		Vector2D C = new Vector2D(X, Y);

		Vector2D AB = B.Subtract(A);
		Vector2D AC = C.Subtract(A);

		Vector2D BA = A.Subtract(B);
		Vector2D BC = C.Subtract(B);

		double cosang1 = BA.Dot(BC) / (BA.getMagnitude() * BC.getMagnitude());
		if (cosang1 < 0) return BC.getMagnitude();

		double cosang2 = AB.Dot(AC) / (AB.getMagnitude() * AC.getMagnitude());
		if (cosang2 < 0) return AC.getMagnitude();

		return Math.abs(B.Subtract(A).Cross(C.Subtract(A))) / B.Subtract(A).getMagnitude();
	}

	/**
	 * upward/rightward normal
	 */
	public Vector2D Normal()
	{
		Vector2D direction = getFromUnitVector();
		direction = direction.transform(Matrix2x2.createRotationMatrix(Math.toRadians(90d)));
		if (direction.Y > 0)
		{
			return direction;
		} else if (direction.Y < 0)
		{
			return direction.multiply(-1d);
		} else if (direction.Y == 0)
		{
			if (direction.X > 0)
			{
				return direction;
			} else
			{
				return direction.multiply(-1d);
			}
		}
		return direction;

	}

	public Vector2D getFromUnitVector()
	{
		return this.getToNode().Location.Subtract(this.getFromNode().Location).unit();
	}

	public Vector2D getToUnitVector()
	{
		return this.getFromNode().Location.Subtract(this.getToNode().Location).unit();
	}


	public double getLength()
	{
		return this.getFromNode().Location.Subtract(this.getToNode().Location).getMagnitude();
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


	public double Sin()
	{
		return (this.getToNode().Location.Y - this.getFromNode().Location.Y) / this.getLength();
	}

	public double Cos()
	{
		return (this.getToNode().Location.X - this.getFromNode().Location.X) / this.getLength();
	}

	//K, in KN/m
	public double Stiffness()
	{
		return Emod * Area / getLength();
	}

	public MemberStiffnessMatrix getMemberStiffnessMatrix()
	{
		return new MemberStiffnessMatrix(this);
	}

	public String getName()
	{
		return this.getFromNode().id + "-" + this.getToNode().id;
	}


	public void DrawSolvedMember(TrussView view, Canvas canvas, double maxTF, double maxCF, double minTF, double minCF)
	{
		setupPaint();
		Paint memPaint = new Paint(paint);
		int r, g, b;
		String lbl = "";
		if (force > 0.0001)
		{
			double fraction;
			if (Math.abs(maxTF - minTF) < 0.0001 || Math.abs(maxTF - force) < 0.0001)
			{
				fraction = 1;
			} else
			{
				fraction = Math.abs((force - minTF) / (maxTF - minTF));
			}

			r = (int) (150 * (1 - fraction));
			g = (int) (150 * (1 - fraction));
			b = 255;
			lbl = "T";
		} else if (force < -0.0001)
		{
			double fraction;
			if (Math.abs(maxCF - minCF) < 0.0001 || Math.abs(maxCF + force) < 0.0001)
			{
				fraction = 1;
			} else
			{
				fraction = Math.abs((-force - minCF) / (maxCF - minCF));
			}
			b = (int) (150 * (1 - fraction));
			g = (int) (150 * (1 - fraction));
			r = 255;
			lbl = "C";

		} else
		{
			r = 255;
			g = 255;
			b = 255;
		}
		memPaint.setColor(Color.argb(255, r, g, b));
		float tSize = view.zoom * 0.1f * (float) getLength();
		if (tSize > 60) tSize = 60;
		memPaint.setTextSize(tSize);
		Vector2D p1 = view.toDrawableCoord(getFromNode().Location);
		Vector2D p2 = view.toDrawableCoord(getToNode().Location);
		Vector2D center = p1.Add(p2).multiply(0.5d);

		canvas.save();
		canvas.rotate((float) Math.toDegrees(-getInclinationAngle()), (float) center.X, (float) center.Y);
		DecimalFormat df = new DecimalFormat(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("pref_mem_force_precision", "0.000"));


		if (isSelected())
		{
			memPaint.setColor(sPaint.getColor());
			memPaint.setStrokeWidth(sPaint.getStrokeWidth());
		}
		canvas.drawText(df.format(Math.abs(force)) + " kN " + lbl, (float) center.X, (float) center.Y - 2, memPaint);
		canvas.restore();
		canvas.drawLine((float) p1.X, (float) p1.Y, (float) p2.X, (float) p2.Y, memPaint);
	}


	public double getInclinationAngle()
	{
		Vector2D pt1, pt2;
		if (getFromNode().Location.X > getToNode().Location.X)
		{
			pt1 = getToNode().Location;
			pt2 = getFromNode().Location;
		} else
		{
			pt1 = getFromNode().Location;
			pt2 = getToNode().Location;
		}

		return pt2.Subtract(pt1).inclination();
	}


	public String getText(Context ctx)
	{
		String ret;
		DecimalFormat df = new DecimalFormat(PreferenceManager.getDefaultSharedPreferences(ctx).getString("pref_mem_force_precision", "0.000") + "0");


		ret = "Member " + FromNodeID + "-" + ToNodeID + "\n";
		ret += "Length:" + String.format("%.4G", getLength()) + "m\n";

		ret += "E: " + String.format("%.4G", Emod) + "GPa\n";
		ret += "A: " + String.format("%.4G", Area) + "mm^2\n";
		if (Parent.Solved)
		{
			if (Math.abs(force) > 1E-7)
			{
				String sign = force > 0 ? "T" : "C";
				ret += "Axial Force: " + String.format("%.5G", Math.abs(force)) + "kN " + sign;
			} else
			{
				ret += "Axial Force: 0kN";
			}
		}
		return ret;
	}

}

package com.avinab.trusssolver.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.math.Matrix2x2;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.Drawable;
import com.avinab.trusssolver.widgets.TrussView;

import java.text.DecimalFormat;


public class Load implements Drawable
{
	public Vector2D force;
	public int loadNode;

	public boolean selected = false;
	public Truss Parent;
	Vector2D end = new Vector2D(-80, 0);
	Vector2D arrow1 = new Vector2D(-10, 5);
	Vector2D arrow2 = new Vector2D(-10, -5);
	Vector2D mid = new Vector2D(-10, 0);
	Paint loadPaint, sLoadPaint;
	private double magnitude, angle;


	public Load(Truss truss, Node vert, double mag, double ang)
	{
		Parent = truss;
		setData(mag, ang);
		loadNode = vert.id;
	}

	public Load(Node vert, Vector2D Force)
	{
		loadNode = vert.id;
		force = Force;
	}

	public Load(int nodeNo, Vector2D Force)
	{
		loadNode = nodeNo;
		force = Force;
	}

	public void setData(double mag, double ang)
	{
		magnitude = mag;
		angle = ang;

		double X = -mag * Math.cos(Math.toRadians(ang));
		double Y = -mag * Math.sin(Math.toRadians(ang));

		force = new Vector2D(X, Y);
	}

	public Node getNode()
	{
		return Global.CurrentTruss.getNode(loadNode);
	}

	private void setUpPaints()
	{
		if (loadPaint == null)
		{
			loadPaint = new Paint();
			loadPaint.setTextSize(18);
			loadPaint.setStyle(Paint.Style.FILL);
			loadPaint.setTextAlign(Paint.Align.CENTER);
			loadPaint.setStrokeWidth(4f);
			loadPaint.setAntiAlias(true);
			loadPaint.setTypeface(Typeface.MONOSPACE);
			loadPaint.setColor(Color.argb(220, 255, 165, 0));
		}

		if (sLoadPaint == null)
		{
			sLoadPaint = new Paint();
			sLoadPaint.setTextSize(18);
			sLoadPaint.setStyle(Paint.Style.FILL);
			sLoadPaint.setTextAlign(Paint.Align.CENTER);
			sLoadPaint.setAntiAlias(true);
			sLoadPaint.setStrokeWidth(4f);
			sLoadPaint.setColor(Color.GREEN);
			sLoadPaint.setTypeface(Typeface.MONOSPACE);

		}
	}

	public void Draw(Canvas canvas, TrussView trussview)
	{
		setUpPaints();
		Vector2D point;
		try
		{
			point = trussview.toDrawableCoord(this.getNode().Location);
		} catch (NullPointerException ex)
		{
			return;
		}
		arrowCoords(trussview);

		Path arrow = new Path();
		arrow.moveTo((float) point.X, (float) point.Y);
		arrow.lineTo((float) arrow1.X, (float) arrow1.Y);
		arrow.lineTo((float) arrow2.X, (float) arrow2.Y);
		arrow.close();
		double ang = Math.toDegrees(new Vector2D(force.X, force.Y).inclination()) - 180;
		if (ang < 0) ang += 360;

		DecimalFormat df = new DecimalFormat(PreferenceManager.getDefaultSharedPreferences(trussview.getContext()).getString("pref_load_precision", "0.000"));
		float ty = (float) end.Y;
		if (mid.Y < end.Y)
		{
			ty += 16;
		} else if (mid.Y >= end.Y)
		{
			ty -= 4;
		}

		Paint p = this.selected ? sLoadPaint : loadPaint;

		canvas.drawPath(arrow, p);
		canvas.drawLine((float) mid.X, (float) mid.Y, (float) end.X, (float) end.Y, p);
		canvas.drawText(df.format(this.force.getMagnitude()) + "/" + df.format(ang) + "°", (float) end.X, ty, p);

	}

	@Override
	public Bounds getBounds(TrussView trussView)
	{
		//TODO: inplement bounds
		return this.getNode().getBounds(trussView);
	}

	void arrowCoords(TrussView trussview)
	{
		Vector2D point = trussview.toDrawableCoord(this.getNode().Location);

		end = new Vector2D(-80, 0);
		arrow1 = new Vector2D(-15, 7);
		arrow2 = new Vector2D(-15, -7);
		mid = new Vector2D(-15, 0);

		double ang = new Vector2D(force.X, -force.Y).inclination();
		Matrix2x2 rotate = Matrix2x2.createRotationMatrix(ang);

		end = end.transform(rotate).Add(point);
		arrow1 = arrow1.transform(rotate).Add(point);
		arrow2 = arrow2.transform(rotate).Add(point);
		mid = mid.transform(rotate).Add(point);


	}

	public double DistanceFromPoint(double X, double Y, TrussView m)
	{
		arrowCoords(m);
		Vector2D A = end;
		Vector2D B = mid;
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

	public void Unselect()
	{
		this.selected = false;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public double getMagnitude()
	{
		return magnitude;
	}

	public double getAngle()
	{
		return angle;
	}

	public void Select()
	{
		selected = true;
	}


	public String getText(Context ctx)
	{
		DecimalFormat df = new DecimalFormat(PreferenceManager.getDefaultSharedPreferences(ctx).getString("pref_load_precision", "0.000") + "0");
		return "Force: " + df.format(magnitude) + "kN\n" + "Angle : " + df.format(angle) + "°";
	}

}

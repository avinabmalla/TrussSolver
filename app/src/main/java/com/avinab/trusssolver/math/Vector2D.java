package com.avinab.trusssolver.math;

import java.text.DecimalFormat;

public class Vector2D
{
	public double X;
	public double Y;

	public Vector2D()
	{
	}

	public Vector2D(double _x, double _y)
	{
		setXY(_x, _y);
	}

	public void CreateFromPolar(double magnitude, double angle)
	{
		setXY(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
	}

	public double getMagnitude()
	{
		return Math.sqrt(X * X + Y * Y);
	}


	public void setXY(double x, double y)
	{
		X = x;
		Y = y;
	}

	public Vector2D Add(Vector2D other)
	{
		double x = X + other.X;
		double y = Y + other.Y;
		return new Vector2D(x, y);
	}

	public Vector2D Subtract(Vector2D other)
	{
		double x = X - other.X;
		double y = Y - other.Y;
		return new Vector2D(x, y);
	}

	public double Dot(Vector2D other)
	{
		return X * other.X + Y * other.Y;
	}

	public double Cross(Vector2D other)
	{
		return X * other.Y - Y * other.X;
	}

	public double inclination()
	{
		double absAng = Math.atan(Math.abs(Y) / Math.abs(X));
		if (Y >= 0)
		{
			if (X >= 0)
			{
				return absAng;
			} else if (X < 0)
			{
				return Math.PI - absAng;
			}
		} else if (Y < 0)
		{
			if (X >= 0)
			{
				return 2 * Math.PI - absAng;
			} else if (X < 0)
			{
				return Math.PI + absAng;
			}
		}
		return absAng;
	}


	public Vector2D transform(Matrix2x2 mat)
	{
		double x = mat.element[0][0] * this.X + mat.element[0][1] * this.Y;
		double y = mat.element[1][0] * this.X + mat.element[1][1] * this.Y;
		return new Vector2D(x, y);
	}

	public Vector2D multiply(double d)
	{
		return new Vector2D(X * d, Y * d);
	}

	public Vector2D unit()
	{
		return this.multiply(1 / this.getMagnitude());

	}

	@Override
	public String toString()
	{
		DecimalFormat df = new DecimalFormat("#.0000");
		return "(" + df.format(X) + " , " + df.format(Y) + ")";
	}
}

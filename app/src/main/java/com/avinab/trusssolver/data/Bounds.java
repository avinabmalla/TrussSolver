package com.avinab.trusssolver.data;


import com.avinab.trusssolver.math.Vector2D;

/**
 * Avinab
 * 11/11/2014.
 */
public class Bounds
{
	public double minX = Float.POSITIVE_INFINITY;
	public double maxX = Float.NEGATIVE_INFINITY;
	public double minY = Float.POSITIVE_INFINITY;
	public double maxY = Float.NEGATIVE_INFINITY;


	public Bounds(Vector2D pnt)
	{
		minX = pnt.X;
		maxX = pnt.X;

		minY = pnt.Y;
		maxY = pnt.Y;
	}

	public Bounds(double min_x, double min_y, double max_x, double max_y)
	{
		minX = min_x;
		maxX = max_x;

		minY = min_y;
		maxY = max_y;
	}


	public void add(Vector2D pnt)
	{
		if (pnt.X > maxX)
		{
			maxX = pnt.X;
		}
		if (pnt.Y > maxY)
		{
			maxY = pnt.Y;
		}

		if (pnt.X < minX)
		{
			minX = pnt.X;
		}
		if (pnt.Y < minY)
		{
			minY = pnt.Y;
		}
	}

	public void add(Bounds bnds)
	{
		if (bnds.maxY > this.maxY) this.maxY = bnds.maxY;
		if (bnds.minY < this.minY) this.minY = bnds.minY;
		if (bnds.maxX > this.maxX) this.maxX = bnds.maxX;
		if (bnds.minX < this.minX) this.minX = bnds.minX;
	}

	public Vector2D getCenter()
	{
		return new Vector2D((minX + maxX) / 2, (minY + maxY) / 2);
	}
}

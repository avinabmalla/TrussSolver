package com.avinab.trusssolver.math;

public class Matrix2x2
{

	public double element[][] = new double[2][2];

	public Matrix2x2(double e11, double e12, double e21, double e22)
	{
		element = new double[2][2];
		element[0][0] = e11;
		element[0][1] = e12;
		element[1][0] = e21;
		element[1][1] = e22;

	}

	/**
	 * @param angle Angle in radians
	 * @return The transformation matrix
	 */
	public static Matrix2x2 createRotationMatrix(double angle)
	{
		double a = Math.cos(angle);
		double b = Math.sin(angle);

		return new Matrix2x2(a, -b, b, a);
	}

	public double determinant()
	{
		return element[0][0] * element[1][1] - element[0][1] * element[1][0];
	}

	public Matrix2x2 adj()
	{
		return new Matrix2x2(element[1][1], -element[0][1], -element[1][0], element[0][0]);
	}

	public Matrix2x2 multiplyByScalar(double scalar)
	{
		return new Matrix2x2(element[0][0] * scalar, element[0][1] * scalar, element[1][0] * scalar, element[1][1] * scalar);
	}

	public Matrix2x2 inverse()
	{
		return this.adj().multiplyByScalar(1 / this.determinant());
	}
}

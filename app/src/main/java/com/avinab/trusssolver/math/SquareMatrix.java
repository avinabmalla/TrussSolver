package com.avinab.trusssolver.math;

/**
 * Avinab
 * 10/16/2014.
 */
public class SquareMatrix extends Matrix
{

	public SquareMatrix(int n)
	{
		super(n, n);
	}

	public SquareMatrix(double[][] data)
	{
		super(data);
	}

	// copy constructor
	private SquareMatrix(SquareMatrix A)
	{
		this(A.data);
	}

	public static SquareMatrix Identity(int n)
	{
		SquareMatrix ret = new SquareMatrix(n);
		for (int i = 0; i < n; i++)
		{
			ret.data[i][i] = 1;
		}

		return ret;
	}

	public SquareMatrix Inverse()
	{
		SquareMatrix A = new SquareMatrix(this);
		SquareMatrix B = SquareMatrix.Identity(N);


		//Reduce to diagonal matrix
		for (int k = 0; k < N; k++)
		{
			for (int i = 0; i < N; i++)
			{
				if (i == k) continue;
				double ratio = A.data[i][k] / A.data[k][k];
				for (int j = 0; j < N; j++)
				{
					A.data[i][j] -= ratio * A.data[k][j];
					B.data[i][j] -= ratio * B.data[k][j];
				}
			}
		}


		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				B.data[i][j] /= A.data[i][i];
			}
		}
		return B;

	}

}

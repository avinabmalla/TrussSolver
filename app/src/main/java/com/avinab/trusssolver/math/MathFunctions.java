package com.avinab.trusssolver.math;

/**
 * Avinab
 * 11/4/2014.
 */
public class MathFunctions
{
	public static int nCr(int n, int r)
	{
		return factorial(n) / (factorial(n - r) * factorial(r));

	}

	public static int factorial(int n)
	{
		if (n < 0)
		{
			return -1;
		} else if (n == 0)
		{
			return 1;
		} else
		{
			int ret = 1;
			for (int i = n; i > 1; i--)
			{
				ret *= i;
			}
			return ret;
		}
	}
}

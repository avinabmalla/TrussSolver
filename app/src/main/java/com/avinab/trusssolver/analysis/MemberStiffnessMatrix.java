package com.avinab.trusssolver.analysis;


import com.avinab.trusssolver.data.Member;
import com.avinab.trusssolver.math.SquareMatrix;

/**
 * Avinab
 * 10/16/2014.
 */
public class MemberStiffnessMatrix extends SquareMatrix
{

	public Member member;

	public MemberStiffnessMatrix(Member _member)
	{
		super(4);

		member = _member;
		double k = member.Stiffness();
		double c = member.Cos();
		double s = member.Sin();

		this.data[0][0] = k * c * c;
		this.data[0][1] = k * c * s;
		this.data[1][0] = k * c * s;
		this.data[1][1] = k * s * s;

		this.data[2][0] = -k * c * c;
		this.data[2][1] = -k * c * s;
		this.data[3][0] = -k * c * s;
		this.data[3][1] = -k * s * s;

		this.data[0][2] = -k * c * c;
		this.data[0][3] = -k * c * s;
		this.data[1][2] = -k * c * s;
		this.data[1][3] = -k * s * s;

		this.data[2][2] = k * c * c;
		this.data[2][3] = k * c * s;
		this.data[3][2] = k * c * s;
		this.data[3][3] = k * s * s;
	}


}

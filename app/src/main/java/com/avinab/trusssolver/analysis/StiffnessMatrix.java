package com.avinab.trusssolver.analysis;


import com.avinab.trusssolver.data.Member;
import com.avinab.trusssolver.data.Truss;
import com.avinab.trusssolver.data.supports.HingeSupport;
import com.avinab.trusssolver.data.supports.RollerSupport;
import com.avinab.trusssolver.math.SquareMatrix;

/**
 * Avinab
 * 10/16/2014.
 */
public class StiffnessMatrix extends SquareMatrix
{
	// copy constructor
	public StiffnessMatrix(StiffnessMatrix A)
	{
		this(A.data);
	}

	private StiffnessMatrix(double data[][])
	{
		super(data);
	}


	public StiffnessMatrix(Truss truss)
	{
		super(truss.Nodes.size() * 2);

		for (Member m : truss.Members)
		{
			int from = truss.Nodes.indexOf(m.getFromNode());
			int to = truss.Nodes.indexOf(m.getToNode());

			double msm[][] = m.getMemberStiffnessMatrix().getData();


			for (int i = 0; i <= 1; i++)
			{
				for (int j = 0; j <= 1; j++)
				{
					this.data[from * 2 + i][from * 2 + j] += msm[i][j];
				}
			}

			for (int i = 0; i <= 1; i++)
			{
				for (int j = 0; j <= 1; j++)
				{
					this.data[to * 2 + i][to * 2 + j] += msm[2 + i][2 + j];
				}
			}

			for (int i = 0; i <= 1; i++)
			{
				for (int j = 0; j <= 1; j++)
				{
					this.data[from * 2 + i][to * 2 + j] += msm[i][2 + j];
				}
			}

			for (int i = 0; i <= 1; i++)
			{
				for (int j = 0; j <= 1; j++)
				{
					this.data[to * 2 + i][from * 2 + j] += msm[i + 2][j];
				}
			}

		}
	}

	public void Restrain(Truss truss)
	{
		//restrain
		for (HingeSupport hs : truss.HingeSupports)
		{
			int index = truss.Nodes.indexOf(hs.getNode());
			for (int i = 0; i < N; i++)
			{
				data[index * 2][i] = 0;
				data[index * 2 + 1][i] = 0;
				data[i][index * 2] = 0;
				data[i][index * 2 + 1] = 0;
			}
			data[index * 2][index * 2] = 1;
			data[index * 2 + 1][index * 2 + 1] = 1;
		}
		for (RollerSupport rs : truss.RollerSupports)
		{
			int index = truss.Nodes.indexOf(rs.getNode());
			if (rs.isHorizontal())
			{
				for (int i = 0; i < N; i++)
				{
					data[index * 2][i] = 0;
					data[i][index * 2] = 0;
				}
				data[index * 2][index * 2] = 1;
			} else
			{
				for (int i = 0; i < N; i++)
				{
					data[index * 2 + 1][i] = 0;
					data[i][index * 2 + 1] = 0;
				}
				data[index * 2 + 1][index * 2 + 1] = 1;
			}
		}


	}


}

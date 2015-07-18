package com.avinab.trusssolver.analysis;


import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.data.Load;
import com.avinab.trusssolver.data.Truss;
import com.avinab.trusssolver.data.supports.HingeSupport;
import com.avinab.trusssolver.data.supports.RollerSupport;
import com.avinab.trusssolver.math.Matrix;

/**
 * Avinab
 * 10/16/2014.
 */
public class ForceVector extends Matrix
{

	public ForceVector(Truss truss)
	{
		super(Global.CurrentTruss.Nodes.size() * 2, 1);

		for (Load l : truss.PointLoads)
		{
			int index = truss.Nodes.indexOf(l.getNode());
			data[index * 2][0] += l.force.X;
			data[index * 2 + 1][0] += l.force.Y;
		}

		//restrain
		for (HingeSupport hs : truss.HingeSupports)
		{
			int index = truss.Nodes.indexOf(hs.getNode());
			data[index * 2][0] = 0;
			data[index * 2 + 1][0] = 0;
		}
		for (RollerSupport rs : truss.RollerSupports)
		{
			int index = truss.Nodes.indexOf(rs.getNode());
			if (rs.isHorizontal())
			{
				data[index * 2][0] = 0;
			} else
			{
				data[index * 2 + 1][0] = 0;
			}
		}

	}
}

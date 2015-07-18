package com.avinab.trusssolver.analysis;

import com.avinab.trusssolver.Global;

/**
 * Created by Avinab on 7/13/2015.
 */
public class TrussAnalyser
{
	public void Analyze()
	{
		Thread thr = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Global.CurrentTruss.solve();
			}
		});
		thr.run();
	}


}

package com.avinab.trusssolver.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateTimer extends Service
{
	// constant
	public static final long NOTIFY_INTERVAL = 60;
	static final public String UPDATE = "com.avinab.trusssolver.ui.UPDATE";
	LocalBroadcastManager broadcaster;
	// run on another Thread to avoid crash
	private Handler mHandler = new Handler();
	// timer handling
	private Timer mTimer = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		broadcaster = LocalBroadcastManager.getInstance(this);
		// cancel if already existed
		if (mTimer != null)
		{
			mTimer.cancel();
		} else
		{
			// recreate new
			mTimer = new Timer();
		}
		// schedule task
		mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
	}

	class TimeDisplayTimerTask extends TimerTask
	{

		@Override
		public void run()
		{
			// run on another thread
			mHandler.post(new Runnable()
			{

				@Override
				public void run()
				{
					Intent intent = new Intent(UPDATE);
					broadcaster.sendBroadcast(intent);
				}
			});
		}

	}
}
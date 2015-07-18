package com.avinab.trusssolver.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.avinab.trusssolver.R;


public class AboutActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		this.setTitle("About");
	}

}

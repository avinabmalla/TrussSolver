package com.avinab.trusssolver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.avinab.trusssolver.Global;

import java.io.File;


/**
 * Avinab
 * 12/8/2014.
 */
public class OpenDialog extends DialogFragment
{
	Context ctx;
	AlertDialog dlg;
	ArrayAdapter<String> adapter;
	int selection = -1;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ctx = this.getActivity();


		final File dir = new File(ctx.getApplicationInfo().dataDir + "/databases");
		File ff[] = dir.listFiles();

		if (ff == null)
		{
			Toast.makeText(ctx, "No Files Found.", Toast.LENGTH_SHORT).show();
			return null;
		}
		if (ff.length == 0)
		{
			Toast.makeText(ctx, "No Files Found.", Toast.LENGTH_SHORT).show();
			return null;
		}

		adapter = new ArrayAdapter<>(ctx, android.R.layout.select_dialog_singlechoice);
		for (File f : ff)
		{
			String n = f.getName();
			if (!n.contains("-journal") && !f.isDirectory() && !n.equals(Global.TRUSS_DATA_CACHE))
			{
				adapter.add(f.getName());
			}
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("Open");
		builder.setPositiveButton("Open", null);
		builder.setNeutralButton("Delete", null);
		builder.setNegativeButton("Cancel", null);

		builder.setSingleChoiceItems(adapter, selection, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				selection = which;
			}
		});

		dlg = builder.create();

		dlg.setOnShowListener(new DialogInterface.OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialogInterface)
			{
				Button btnOpen = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
				Button btnDelete = dlg.getButton(AlertDialog.BUTTON_NEUTRAL);
				Button btnCancel = dlg.getButton(AlertDialog.BUTTON_NEGATIVE);

				btnCancel.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						dlg.dismiss();
					}
				});


				btnDelete.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (selection < 0) return;
						String file = adapter.getItem(selection);
						ctx.deleteDatabase(file);
						adapter.remove(file);
					}
				});

				btnOpen.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (selection < 0) return;
						String file = adapter.getItem(selection);
						Global.CurrentTruss = Global.trussIO.Open(file);
						if (ctx instanceof MainActivity)
						{
							MainActivity activity = (MainActivity) ctx;
							activity.trussView.ZoomExtents();
						}
						dlg.dismiss();
					}
				});

			}
		});
		return dlg;
	}
}

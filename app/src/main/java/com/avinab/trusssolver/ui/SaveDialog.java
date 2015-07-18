package com.avinab.trusssolver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.R;

import java.io.File;


/**
 * Avinab
 * 12/8/2014.
 */
public class SaveDialog extends DialogFragment
{
	Context ctx;
	AlertDialog dlg;
	private EditText txtSaveFileName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ctx = this.getActivity();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_save, null);
		this.txtSaveFileName = (EditText) layout.findViewById(R.id.txtSaveFileName);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("Save As");
		builder.setView(layout);
		builder.setPositiveButton("Save", null);
		builder.setNegativeButton("Cancel", null);

		dlg = builder.create();

		dlg.setOnShowListener(new DialogInterface.OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialogInterface)
			{
				Button btnSave = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
				Button btnCancel = dlg.getButton(AlertDialog.BUTTON_NEGATIVE);

				btnCancel.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						dlg.dismiss();
					}
				});

				btnSave.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							final String name = txtSaveFileName.getText().toString();
							if (name.equals(""))
							{
								Toast.makeText(ctx, "Enter a valid name.", Toast.LENGTH_SHORT).show();
							} else
							{
								final File f = new File(dlg.getContext().getApplicationInfo().dataDir + "/databases/", name);
								if (f.exists())
								{
									AlertDialog.Builder builder = new AlertDialog.Builder(dlg.getContext());
									builder.setMessage("File " + name + " exists. Replace?").setTitle("Warning").setPositiveButton("Yes", new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(DialogInterface dialog, int id)
										{
											Global.CurrentTruss.Name = name;
											Global.trussIO.Save(Global.CurrentTruss);
											Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();

										}
									}).setNegativeButton("No", new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(DialogInterface dialog, int id)
										{
											dlg.dismiss();
										}
									});

									AlertDialog dialog = builder.create();
									dialog.show();
									dlg.dismiss();
								} else
								{
									Global.CurrentTruss.Name = name;
									Global.trussIO.Save(Global.CurrentTruss);
									Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
									dlg.dismiss();

								}
							}
						} catch (Exception ex)
						{
							ex.printStackTrace();
							Toast.makeText(ctx, "Enter a valid name.", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});


		return dlg;
	}
}

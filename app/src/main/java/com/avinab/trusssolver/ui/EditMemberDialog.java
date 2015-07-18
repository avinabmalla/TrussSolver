package com.avinab.trusssolver.ui;

import android.app.Activity;
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
import android.widget.ImageButton;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.R;
import com.avinab.trusssolver.data.Member;

/**
 * Created by Avinab on 12/19/2014.
 */
public class EditMemberDialog extends DialogFragment
{
	Context ctx;
	AlertDialog dlg;
	Member mem;
	private android.widget.EditText txtMemberE;
	private android.widget.EditText txtMemberArea;
	private android.widget.ImageButton btnCalcA;
	private android.widget.ImageButton btnCalcE;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ctx = this.getActivity();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_member, null);
		this.btnCalcE = (ImageButton) layout.findViewById(R.id.btnCalcE);
		this.btnCalcA = (ImageButton) layout.findViewById(R.id.btnCalcA);
		this.txtMemberArea = (EditText) layout.findViewById(R.id.txtMemberArea);
		this.txtMemberE = (EditText) layout.findViewById(R.id.txtMemberE);


		btnCalcE.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CalculatorDialog dlg = new CalculatorDialog();
				dlg.setInputListener(new CalculatorDialog.CalculatorInputListener()
				{
					@Override
					public void InputProvided(double value)
					{
						txtMemberE.setText(value + "");
					}
				});
				dlg.show(((Activity) ctx).getFragmentManager(), "calc");
			}
		});

		btnCalcA.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CalculatorDialog dlg = new CalculatorDialog();
				dlg.setInputListener(new CalculatorDialog.CalculatorInputListener()
				{
					@Override
					public void InputProvided(double value)
					{
						txtMemberArea.setText(value + "");
					}
				});
				dlg.show(((Activity) ctx).getFragmentManager(), "calc");
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		if (mem == null)
		{
			return null;
		} else
		{
			builder.setTitle("Edit Member");
			builder.setPositiveButton("Save", null);
			builder.setNeutralButton("Apply to All Members", null);
			txtMemberE.setText(mem.Emod + "");
			txtMemberArea.setText(mem.Area + "");
		}
		builder.setView(layout);

		builder.setNegativeButton("Cancel", null);

		dlg = builder.create();

		dlg.setOnShowListener(new DialogInterface.OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialogInterface)
			{
				Button btnAdd = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
				Button btnAll = dlg.getButton(AlertDialog.BUTTON_NEUTRAL);
				Button btnCancel = dlg.getButton(AlertDialog.BUTTON_NEGATIVE);

				btnAdd.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{

						try
						{
							double e = Double.parseDouble(txtMemberE.getText().toString());
							double a = Double.parseDouble(txtMemberArea.getText().toString());
							Global.CurrentTruss.EditMember(mem.id, e, a);
							dlg.dismiss();
						} catch (Exception e)
						{
							e.printStackTrace();
						}

					}
				});

				btnAll.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						new AlertDialog.Builder(ctx)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setTitle("Assign Section")
								.setMessage("Assign section properties to all members?")
								.setPositiveButton("Yes", new DialogInterface.OnClickListener()
								{

									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										double e = Double.parseDouble(txtMemberE.getText().toString());
										double a = Double.parseDouble(txtMemberArea.getText().toString());
										for (Member m : Global.CurrentTruss.Members)
										{
											Global.CurrentTruss.EditMember(m.id, e, a);
										}

										dlg.dismiss();
									}

								})
								.setNegativeButton("No", null)
								.show();
					}
				});
				btnCancel.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						dlg.dismiss();
					}
				});
			}
		});


		return dlg;
	}

	public void setMember(Member m)
	{
		mem = m;
	}
}

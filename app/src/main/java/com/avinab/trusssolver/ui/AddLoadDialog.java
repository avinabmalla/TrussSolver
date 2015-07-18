package com.avinab.trusssolver.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.R;
import com.avinab.trusssolver.data.Load;
import com.avinab.trusssolver.data.Node;


/**
 * Avinab
 * 11/25/2014.
 */
public class AddLoadDialog extends DialogFragment
{
	Context ctx;
	AlertDialog dlg;
	Node node;
	Load pl = null;


	private EditText txtLoadMag;
	private EditText txtAngle;
	private android.widget.TextView textView4;
	private android.widget.TextView textView5;
	private android.widget.ToggleButton tbtnQuad1;
	private android.widget.ToggleButton tbtnQuad2;
	private android.widget.ToggleButton tbtnQuad3;
	private android.widget.ToggleButton tbtnQuad4;
	private android.widget.ImageButton btnCalcL;
	private android.widget.ImageButton btnCalcAng;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ctx = this.getActivity();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_add_load, null);
		this.btnCalcAng = (ImageButton) layout.findViewById(R.id.btnCalcAng);
		this.btnCalcL = (ImageButton) layout.findViewById(R.id.btnCalcL);
		this.tbtnQuad4 = (ToggleButton) layout.findViewById(R.id.tbtnQuad4);
		this.tbtnQuad3 = (ToggleButton) layout.findViewById(R.id.tbtnQuad3);
		this.tbtnQuad2 = (ToggleButton) layout.findViewById(R.id.tbtnQuad2);
		this.tbtnQuad1 = (ToggleButton) layout.findViewById(R.id.tbtnQuad1);
		this.textView5 = (TextView) layout.findViewById(R.id.textView5);
		this.textView4 = (TextView) layout.findViewById(R.id.textView4);
		this.txtAngle = (EditText) layout.findViewById(R.id.txtAngle);
		this.txtLoadMag = (EditText) layout.findViewById(R.id.txtLoadMag);


		btnCalcAng.setOnClickListener(new View.OnClickListener()
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
						txtAngle.setText(value + "");
					}
				});
				dlg.show(((Activity) ctx).getFragmentManager(), "calc");
			}
		});

		btnCalcL.setOnClickListener(new View.OnClickListener()
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
						txtLoadMag.setText(value + "");
					}
				});
				dlg.show(((Activity) ctx).getFragmentManager(), "calc");
			}
		});


		ImageSpan q1 = new ImageSpan(ctx, R.drawable.ic_quad_1);
		ImageSpan q2 = new ImageSpan(ctx, R.drawable.ic_quad_2);
		ImageSpan q3 = new ImageSpan(ctx, R.drawable.ic_quad_3);
		ImageSpan q4 = new ImageSpan(ctx, R.drawable.ic_quad_4);

		SpannableString content1 = new SpannableString("X");
		SpannableString content2 = new SpannableString("X");
		SpannableString content3 = new SpannableString("X");
		SpannableString content4 = new SpannableString("X");

		content1.setSpan(q1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tbtnQuad1.setText(content1);
		tbtnQuad1.setTextOn(content1);
		tbtnQuad1.setTextOff(content1);

		content2.setSpan(q2, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tbtnQuad2.setText(content2);
		tbtnQuad2.setTextOn(content2);
		tbtnQuad2.setTextOff(content2);


		content3.setSpan(q3, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tbtnQuad3.setText(content3);
		tbtnQuad3.setTextOn(content3);
		tbtnQuad3.setTextOff(content3);

		content4.setSpan(q4, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tbtnQuad4.setText(content4);
		tbtnQuad4.setTextOn(content4);
		tbtnQuad4.setTextOff(content4);


		tbtnQuad1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tbtnQuad1.setChecked(true);
				tbtnQuad2.setChecked(false);
				tbtnQuad3.setChecked(false);
				tbtnQuad4.setChecked(false);
			}
		});


		tbtnQuad2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tbtnQuad2.setChecked(true);
				tbtnQuad1.setChecked(false);
				tbtnQuad3.setChecked(false);
				tbtnQuad4.setChecked(false);
			}
		});


		tbtnQuad3.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tbtnQuad3.setChecked(true);
				tbtnQuad2.setChecked(false);
				tbtnQuad1.setChecked(false);
				tbtnQuad4.setChecked(false);
			}
		});


		tbtnQuad4.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tbtnQuad4.setChecked(true);
				tbtnQuad2.setChecked(false);
				tbtnQuad3.setChecked(false);
				tbtnQuad1.setChecked(false);
			}
		});


		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		if (pl == null)
		{
			builder.setTitle("Add Load");
			builder.setPositiveButton("Add", null);
		} else
		{
			builder.setTitle("Edit Load");
			builder.setPositiveButton("Edit", null);
			txtLoadMag.setText(pl.getMagnitude() + "");
			txtAngle.setText(pl.getAngle() + "");
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
				Button btnCancel = dlg.getButton(AlertDialog.BUTTON_NEGATIVE);

				btnAdd.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{

						try
						{
							double magn = Double.parseDouble(txtLoadMag.getText().toString());
							double ang = Double.parseDouble(txtAngle.getText().toString());

							if (tbtnQuad2.isChecked())
							{
								ang = 180 - ang;
							} else if (tbtnQuad3.isChecked())
							{
								ang = 180 + ang;
							} else if (tbtnQuad4.isChecked())
							{
								ang = 360 - ang;
							}


							if (pl == null)
							{
								Global.CurrentTruss.addPointLoad(node, magn, ang);
							} else
							{
								pl.setData(magn, ang);
								Global.CurrentTruss.Solved = false;
								Global.CurrentTruss.Modified = true;
							}
							dlg.dismiss();
						} catch (Exception e)
						{
							e.printStackTrace();
						}

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

	public void setPointLoad(Load load)
	{
		pl = load;
		setNode(load.getNode());
	}

	public void setNode(Node n)
	{
		node = n;
	}
}

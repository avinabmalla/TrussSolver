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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.R;
import com.avinab.trusssolver.data.Node;
import com.avinab.trusssolver.math.Vector2D;


/**
 * Avinab
 * 12/6/2014.
 */
public class AddNodeDialog extends DialogFragment
{
	Context ctx;
	AlertDialog dlg;
	Node node = null;
	private EditText txtX;
	private EditText txtY;
	private ImageButton btnCalculateX;
	private ImageButton btnCalculateY;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ctx = this.getActivity();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_add_node, null);
		this.btnCalculateX = (ImageButton) layout.findViewById(R.id.btnCalculateX);
		this.btnCalculateY = (ImageButton) layout.findViewById(R.id.btnCalculateY);

		this.txtY = (EditText) layout.findViewById(R.id.txtY);
		this.txtX = (EditText) layout.findViewById(R.id.txtX);


		btnCalculateX.setOnClickListener(new View.OnClickListener()
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
						txtX.setText(value + "");
					}
				});
				dlg.show(((Activity) ctx).getFragmentManager(), "calc");
			}
		});


		btnCalculateY.setOnClickListener(new View.OnClickListener()
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
						txtY.setText(value + "");
					}
				});
				dlg.show(((Activity) ctx).getFragmentManager(), "calc");
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		if (node == null)
		{
			builder.setTitle("Add Node");
			builder.setPositiveButton("Add", null);
		} else
		{
			builder.setTitle("Edit Node " + node.id);
			builder.setPositiveButton("Edit", null);
			txtX.setText(node.Location.X + "");
			txtY.setText(node.Location.Y + "");

			String items[] = getResources().getStringArray(R.array.node_types);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, items);


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
							double X = Double.parseDouble(txtX.getText().toString());
							double Y = Double.parseDouble(txtY.getText().toString());

							if (node == null)
							{
								Global.CurrentTruss.AddNode(new Vector2D(X, Y));
							} else
							{
								Global.CurrentTruss.EditNode(node.id, new Vector2D(X, Y));
							}
							dlg.dismiss();
						} catch (Exception e)
						{
							Toast.makeText(ctx, "Invalid Input", Toast.LENGTH_SHORT).show();
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


	public void setNode(Node n)
	{
		node = n;
	}
}

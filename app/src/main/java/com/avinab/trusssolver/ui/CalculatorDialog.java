package com.avinab.trusssolver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avinab.trusssolver.R;
import com.avinab.trusssolver.math.Expression;

import java.util.Stack;

/**
 * Created by Avinab on 7/15/2015.
 */
public class CalculatorDialog extends DialogFragment
{

	Context ctx;
	AlertDialog dlg;
	private android.widget.TextView txtVal;
	private android.widget.Button btnSqrt;
	private android.widget.Button btnBackSpace;
	private android.widget.Button btnBrkOpen;
	private android.widget.Button btnBrkClose;
	private android.widget.Button btnCls;
	private android.widget.Button btnPlus;
	private android.widget.Button btnPow;
	private android.widget.Button btn7;
	private android.widget.Button btn8;
	private android.widget.Button btn9;
	private android.widget.Button btnMinus;
	private android.widget.Button btnSin;
	private android.widget.Button btn4;
	private android.widget.Button btn5;
	private android.widget.Button btn6;
	private android.widget.Button btnMultiply;
	private android.widget.Button btnCos;
	private android.widget.Button btn1;
	private android.widget.Button btn2;
	private android.widget.Button btn3;
	private android.widget.Button btnDivide;
	private android.widget.Button btnTan;
	private android.widget.Button btn0;
	private android.widget.Button btnDot;
	private android.widget.Button btnEqual;
	private android.widget.Button btnOk;
	private android.widget.TableLayout tableLayout;
	private android.widget.TextView txtExpression;


	private Stack<String> ExpressionStack = new Stack<>();
	private Button btnPi;
	private Button btnAsin;
	private Button btnAcos;
	private Button btnAtan;
	private CalculatorInputListener listener;

	public void setInputListener(CalculatorInputListener l)
	{
		listener = l;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		ctx = this.getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_calculator, null);
		this.btnAtan = (Button) layout.findViewById(R.id.btnAtan);
		this.btnAcos = (Button) layout.findViewById(R.id.btnAcos);
		this.btnAsin = (Button) layout.findViewById(R.id.btnAsin);
		this.btnPi = (Button) layout.findViewById(R.id.btnPi);
		this.txtExpression = (TextView) layout.findViewById(R.id.txtExpression);
		this.tableLayout = (TableLayout) layout.findViewById(R.id.tableLayout);
		this.btnOk = (Button) layout.findViewById(R.id.btnOk);
		this.btnEqual = (Button) layout.findViewById(R.id.btnEqual);
		this.btnDot = (Button) layout.findViewById(R.id.btnDot);
		this.btn0 = (Button) layout.findViewById(R.id.btn0);
		this.btnTan = (Button) layout.findViewById(R.id.btnTan);
		this.btnDivide = (Button) layout.findViewById(R.id.btnDivide);
		this.btn3 = (Button) layout.findViewById(R.id.btn3);
		this.btn2 = (Button) layout.findViewById(R.id.btn2);
		this.btn1 = (Button) layout.findViewById(R.id.btn1);
		this.btnCos = (Button) layout.findViewById(R.id.btnCos);
		this.btnMultiply = (Button) layout.findViewById(R.id.btnMultiply);
		this.btn6 = (Button) layout.findViewById(R.id.btn6);
		this.btn5 = (Button) layout.findViewById(R.id.btn5);
		this.btn4 = (Button) layout.findViewById(R.id.btn4);
		this.btnSin = (Button) layout.findViewById(R.id.btnSin);
		this.btnMinus = (Button) layout.findViewById(R.id.btnMinus);
		this.btn9 = (Button) layout.findViewById(R.id.btn9);
		this.btn8 = (Button) layout.findViewById(R.id.btn8);
		this.btn7 = (Button) layout.findViewById(R.id.btn7);
		this.btnPow = (Button) layout.findViewById(R.id.btnPow);
		this.btnPlus = (Button) layout.findViewById(R.id.btnPlus);
		this.btnCls = (Button) layout.findViewById(R.id.btnCls);
		this.btnBrkClose = (Button) layout.findViewById(R.id.btnBrkClose);
		this.btnBrkOpen = (Button) layout.findViewById(R.id.btnBrkOpen);
		this.btnSqrt = (Button) layout.findViewById(R.id.btnSqrt);
		this.txtVal = (TextView) layout.findViewById(R.id.txtVal);
		this.btnBackSpace = (Button) layout.findViewById(R.id.btnBackSpace);

		btn0.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("0");
			}
		});
		btn1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("1");
			}
		});
		btn2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("2");
			}
		});
		btn3.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("3");
			}
		});
		btn4.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("4");
			}
		});
		btn5.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("5");
			}
		});
		btn6.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("6");
			}
		});
		btn7.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("7");
			}
		});
		btn8.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("8");
			}
		});
		btn9.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("9");
			}
		});
		btnPlus.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" + ");
			}
		});
		btnMinus.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" - ");
			}
		});
		btnMultiply.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" * ");
			}
		});
		btnDivide.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" / ");
			}
		});
		btnBrkOpen.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("(");
			}
		});
		btnBrkClose.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(")");
			}
		});
		btnPow.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression("^");
			}
		});
		btnDot.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(".");
			}
		});
		btnSqrt.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" SQRT(");
			}
		});
		btnSin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" SIN(");
			}
		});
		btnCos.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" COS(");
			}
		});
		btnTan.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" TAN(");
			}
		});
		btnAsin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" ASIN(");
			}
		});
		btnAcos.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" ACOS(");
			}
		});
		btnAtan.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" ATAN(");
			}
		});

		btnPi.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppendToExpression(" PI ");
			}
		});

		btnCls.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				txtExpression.setText("");
				txtVal.setText("0");
				ExpressionStack.clear();
			}
		});


		btnAsin.setText(Html.fromHtml("SIN<sup>-1</sup>"));
		btnAcos.setText(Html.fromHtml("COS<sup>-1</sup>"));
		btnAtan.setText(Html.fromHtml("TAN<sup>-1</sup>"));

		btnEqual.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				UpdateVal();
			}
		});

		btnBackSpace.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (ExpressionStack.size() <= 0) return;
				String s = ExpressionStack.pop();
				String oldExpr = txtExpression.getText().toString();
				String newExpr = oldExpr.substring(0, oldExpr.length() - s.length());
				txtExpression.setText(newExpr);
			}
		});


		btnOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String expr = txtExpression.getText().toString();
				if (expr.equals(""))
				{
					if (listener != null)
					{
						listener.InputProvided(0);
					}
					dlg.dismiss();
				} else
				{

					try
					{
						Expression expression = new Expression(expr);
						expression.setPrecision(8);
						double val = Double.parseDouble(expression.eval() + "");
						txtVal.setText(val + "");
						if (listener != null)
						{
							listener.InputProvided(val);
						}
						dlg.dismiss();
					} catch (Exception ex)
					{
						String s = ex.getMessage();
						if (s != null)
						{
							Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
						} else
						{
							Toast.makeText(ctx, "Invalid Input", Toast.LENGTH_SHORT).show();
						}
						txtVal.setText("0");
					}
				}
			}
		});
		builder.setView(layout);
		dlg = builder.create();
		return dlg;
	}

	private void UpdateVal()
	{
		String expr = txtExpression.getText().toString();
		if (expr.equals(""))
		{
			txtVal.setText("0");
		} else
		{
			try
			{
				Expression expression = new Expression(expr);
				expression.setPrecision(8);
				double val = Double.parseDouble(expression.eval() + "");
				txtVal.setText(val + "");
			} catch (Exception ex)
			{
				String s = ex.getMessage();
				if (s != null)
				{
					Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(ctx, "Invalid Input", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void AppendToExpression(String txt)
	{
		txtExpression.setText(txtExpression.getText().toString() + txt);
		ExpressionStack.push(txt);
	}

	public interface CalculatorInputListener
	{
		void InputProvided(double value);
	}


}

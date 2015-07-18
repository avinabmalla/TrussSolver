package com.avinab.trusssolver.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.avinab.trusssolver.data.Bounds;
import com.avinab.trusssolver.math.Vector2D;


/**
 * Created by Avinab on 7/15/2015.
 */
public class Grid implements Drawable
{

	public final int MIN_GRIDLINE_COUNT = 5;
	public final double[] gridSpacings = {50000, 25000, 10000, 5000, 2500, 1000, 500, 250, 100, 50, 25, 10, 5, 2, 1, 0.5, 0.25, 0.1, 0.05, 0.025, 0.01, 0.005, 0.0025, 0.0001, 0.00005};
	Paint paint = new Paint();

	public Grid()
	{
		paint.setColor(Color.argb(70, 255, 255, 255));
		paint.setStrokeWidth(1f);
		paint.setTextSize(20);
		paint.setAntiAlias(true);

	}

	@Override
	public void Draw(Canvas canvas, TrussView trussView)
	{
		int w = canvas.getWidth();
		int h = canvas.getHeight();

		Vector2D topleft = trussView.toRealCoord(new Vector2D(0, 0));
		Vector2D bottomright = trussView.toRealCoord(new Vector2D(w, h));


		double spacing = 1;//m
		double diff;

		//Get Spacing
		if (w > h)
		{
			diff = topleft.Y - bottomright.Y;
		} else
		{
			diff = bottomright.X - topleft.X;
		}

		for (double sp : gridSpacings)
		{
			if (diff / sp >= MIN_GRIDLINE_COUNT)
			{
				spacing = sp;
				break;
			}
		}

		if (diff / spacing > 25)
		{
			return;
		}
		//Vertical lines
		double startX = spacing * (Math.ceil(topleft.X / spacing));

		do
		{
			Vector2D absSt = new Vector2D(startX, topleft.Y);
			Vector2D absEn = new Vector2D(startX, bottomright.Y);
			Vector2D screenSt = trussView.toDrawableCoord(absSt);
			Vector2D screenEn = trussView.toDrawableCoord(absEn);
			canvas.drawLine((float) screenSt.X, (float) screenSt.Y, (float) screenEn.X, (float) screenEn.Y - 2, paint);
			canvas.drawText(String.format("%.2f", startX) + "", (float) screenEn.X + 2, (float) screenEn.Y, paint);
			startX += spacing;
		} while (startX < bottomright.X);


		//Horizontal lines
		double startY = spacing * (Math.ceil(bottomright.Y / spacing));
		do
		{
			Vector2D absSt = new Vector2D(topleft.X, startY);
			Vector2D absEn = new Vector2D(bottomright.X, startY);
			Vector2D screenSt = trussView.toDrawableCoord(absSt);
			Vector2D screenEn = trussView.toDrawableCoord(absEn);
			canvas.drawLine((float) screenSt.X, (float) screenSt.Y, (float) screenEn.X, (float) screenEn.Y, paint);
			canvas.drawText(String.format("%.2f", startY) + "", (float) screenSt.X + 2, (float) screenSt.Y - 2, paint);
			startY += spacing;
		} while (startY < topleft.Y);

	}

	@Override
	public Bounds getBounds(TrussView trussView)
	{
		return null;
	}
}

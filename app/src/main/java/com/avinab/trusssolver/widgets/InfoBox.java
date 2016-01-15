package com.avinab.trusssolver.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.avinab.trusssolver.data.Bounds;


/**
 * Created by Avinab on 2/17/2015.
 */
public class InfoBox implements Drawable
{
	public boolean isVisible = false;
	Paint pnt;
	Paint bg;
	String text = null;

	public InfoBox()
	{
		pnt = new Paint();
		bg = new Paint();

		bg.setColor(Color.argb(100, 50, 50, 50));
		bg.setStyle(Paint.Style.FILL);

		pnt.setColor(Color.WHITE);
		pnt.setTextSize(25);
		pnt.setStyle(Paint.Style.FILL);
		pnt.setTextAlign(Paint.Align.LEFT);
		pnt.setTypeface(Typeface.MONOSPACE);
		pnt.setAntiAlias(true);
	}

	public void setText(String s)
	{
		text = s;
	}

	@Override
	public void Draw(Canvas canvas, ViewControl frameView)
	{
		if (!isVisible || text == null) return;


		//count lines
		String[] lines = text.split("\r\n|\r|\n");
		int nlines = lines.length;

		int w = 0;
		for (String s : lines)
		{
			Rect textBounds = new Rect();
			pnt.getTextBounds(s, 0, s.length(), textBounds);
			if (w < textBounds.width()) w = textBounds.width() + 5;
		}

		int h = nlines * 32;
		Rect r = new Rect(0, 0, w, h);


		canvas.drawRect(r, bg);
		int y = 30;
		for (String s : lines)
		{
			canvas.drawText(s, 0, y, pnt);
			y += 30;
		}

	}


	@Override
	public Bounds getBounds(ViewControl viewControl)
	{
		return null;
	}
}

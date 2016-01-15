package com.avinab.trusssolver.data.supports;

import android.graphics.Canvas;
import android.graphics.Path;

import com.avinab.trusssolver.data.Node;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.ViewControl;


/**
 * Avinab
 * 11/16/2014.
 */
public class HingeSupport extends Support
{
	final int SIDE_LENGTH = 15;


	public HingeSupport(Node no)
	{
		super(no);
	}

	@Override
	public void Draw(Canvas canvas, ViewControl view)
	{
		Vector2D p = view.toDrawableCoord(this.getNode().Location);
		Vector2D p1;
		Vector2D p2;
		p1 = p.Add(new Vector2D(SIDE_LENGTH, SIDE_LENGTH * 2));
		p2 = p.Add(new Vector2D(-SIDE_LENGTH, SIDE_LENGTH * 2));
		Path pth = new Path();
		pth.moveTo((float) p.X, (float) p.Y);
		pth.lineTo((float) p1.X, (float) p1.Y);
		pth.lineTo((float) p2.X, (float) p2.Y);
		pth.close();

		canvas.drawPath(pth, SupportPaint);
	}


}

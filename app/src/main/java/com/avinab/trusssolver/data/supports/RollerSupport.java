package com.avinab.trusssolver.data.supports;

import android.graphics.Canvas;

import com.avinab.trusssolver.data.Node;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.ViewControl;


/**
 * Avinab
 * 11/16/2014.
 */
public class RollerSupport extends Support
{
	boolean horizontal;

	public RollerSupport(Node no, boolean horz)
	{
		super(no);
		horizontal = horz;
	}

	@Override
	public void Draw(Canvas canvas, ViewControl view)
	{
		Vector2D p = view.toDrawableCoord(this.getNode().Location);

		if (horizontal)
		{
			p = p.Add(new Vector2D(15, 0));
			canvas.drawCircle((float) p.X, (float) p.Y, 10f, SupportPaint);
			canvas.drawLine((float) p.X - 10, (float) p.Y - 15f, (float) p.X - 10, (float) p.Y + 15f, SupportPaint);
		} else
		{
			p = p.Add(new Vector2D(0, 15));
			canvas.drawCircle((float) p.X, (float) p.Y, 10f, SupportPaint);
			canvas.drawLine((float) p.X - 15, (float) p.Y + 10f, (float) p.X + 15, (float) p.Y + 10f, SupportPaint);
		}
	}

	public boolean isHorizontal()
	{
		return horizontal;
	}


}

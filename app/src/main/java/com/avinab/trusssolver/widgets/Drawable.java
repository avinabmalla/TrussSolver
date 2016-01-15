package com.avinab.trusssolver.widgets;


import android.graphics.Canvas;

import com.avinab.trusssolver.data.Bounds;


/**
 * Avinab
 * 11/11/2014.
 */
public interface Drawable
{
	void Draw(Canvas canvas, ViewControl viewControl);

	Bounds getBounds(ViewControl viewControl);
}

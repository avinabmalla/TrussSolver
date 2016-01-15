package com.avinab.trusssolver.widgets;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.avinab.trusssolver.Global;
import com.avinab.trusssolver.analysis.TrussAnalyser;
import com.avinab.trusssolver.data.Bounds;
import com.avinab.trusssolver.data.Load;
import com.avinab.trusssolver.data.Member;
import com.avinab.trusssolver.data.Node;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.ui.MainActivity;
import com.avinab.trusssolver.ui.UIState;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Avinab
 * 10/12/2014.
 */
public class ViewControl extends View
{
	public Vector2D origin = new Vector2D(0, 0); //Bottom Left
	public float zoom = 20;
	public InfoBox infoBox;
	public Grid grid;
	TrussItemSelectListener selectListener;
	GestureDetector mDetector = new GestureDetector(this.getContext(), new mListener());
	ScaleGestureDetector sDetector = new ScaleGestureDetector(this.getContext(), new scaleListener());
	boolean isScaling = false;
	Context ctx;


	public ViewControl(Context context)
	{
		super(context);
		ctx = context;
		infoBox = new InfoBox();
		grid = new Grid();
		mDetector.setOnDoubleTapListener(new doubleTapListener());

	}

	public ViewControl(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		infoBox = new InfoBox();
		ctx = context;
		grid = new Grid();
		mDetector.setOnDoubleTapListener(new doubleTapListener());

	}

	public ViewControl(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		infoBox = new InfoBox();
		ctx = context;
		grid = new Grid();
		mDetector.setOnDoubleTapListener(new doubleTapListener());

	}

	public void Update()
	{
		if (Global.CurrentTruss.Modified)
		{
			if (!Global.CurrentTruss.Solved)
			{
				TrussAnalyser an = new TrussAnalyser();
				an.Analyze();
			}
			Global.CurrentTruss.Modified = false;
			invalidate();
		}


	}

	private void centeredZoom(Vector2D pt, float z)
	{
		Vector2D currentCoord = toRealCoord(pt);
		zoom = z;
		if (zoom < 0.06125) zoom = 0.06125f;
		if (zoom > 2048) zoom = 2048;

		Vector2D btmleft = new Vector2D();
		btmleft.X = currentCoord.X - pt.X / zoom;
		btmleft.Y = currentCoord.Y - ((this.getHeight() - 1 - pt.Y) / zoom);
		this.origin = btmleft;
		invalidate();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		mDetector.onTouchEvent(event);
		sDetector.onTouchEvent(event);

		return true;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width;
		if (getLayoutParams().width == LayoutParams.WRAP_CONTENT)
		{
			width = 40;
		} else if ((getLayoutParams().width == LayoutParams.MATCH_PARENT))
		{
			width = MeasureSpec.getSize(widthMeasureSpec);
		} else
			width = getLayoutParams().width;

		int height;
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT)
		{
			height = 300;
		} else if ((getLayoutParams().height == LayoutParams.MATCH_PARENT))
		{
			height = MeasureSpec.getSize(heightMeasureSpec);
		} else
			height = getLayoutParams().height;

		setMeasuredDimension(width, height);
	}

	public Vector2D toDrawableCoord(Vector2D coord)
	{
		double h = (coord.X - origin.X) * zoom;
		double k = -(((coord.Y - origin.Y) * zoom) - this.getHeight() + 1);
		return new Vector2D(h, k);
	}

	public Vector2D toRealCoord(Vector2D pt)
	{
		double h = pt.X / zoom + origin.X;
		double k = (this.getHeight() - 1 - pt.Y) / zoom + origin.Y;
		return new Vector2D(h, k);
	}


	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.rgb(33, 33, 33));
		if (Global.CurrentTruss != null)
		{
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
			boolean showGrid = sharedPref.getBoolean("pref_grid", true);
			if (showGrid)
			{
				grid.Draw(canvas, this);
			}
			Global.CurrentTruss.Draw(canvas, this);
			infoBox.Draw(canvas, this);

		}
	}

	public void ZoomExtents()
	{
		Bounds b = Global.CurrentTruss.getBounds(this);


		float Xzoom, Yzoom;
		boolean cX = false, cY = false;

		if (b.maxX == b.minX)
		{
			Xzoom = 20;
			cX = true;
		} else
		{
			Xzoom = (getWidth() - 120) / ((float) b.maxX - (float) b.minX);
		}


		if (b.maxY == b.minY)
		{
			Yzoom = 20;
			cY = true;
		} else
		{
			Yzoom = (getHeight() - 120) / ((float) b.maxY - (float) b.minY);
		}
		zoom = Xzoom < Yzoom ? Xzoom : Yzoom;
		zoom *= 0.9d;

		double oX, oY;
		if (cX)
		{
			oX = b.minX - (getWidth() / 2) / zoom;
		} else
		{
			oX = b.minX;
		}
		if (cY)
		{
			oY = b.minY - (getHeight() / 2) / zoom;
		} else
		{
			oY = b.minY;
		}
		origin = new Vector2D(oX, oY);


		Vector2D ScreenCenter = toRealCoord(new Vector2D(getWidth() / 2, getHeight() / 2));
		Vector2D center = b.getCenter();
		Vector2D translation = ScreenCenter.Subtract(center);

		origin = origin.Subtract(translation);

		try
		{
			this.invalidate();
		} catch (Exception ignored)
		{

		}

	}

	public void setSelectListener(TrussItemSelectListener listener)
	{
		selectListener = listener;
	}

	private Node CheckNodeTouch(Vector2D touchPoint)
	{
		for (Node n : Global.CurrentTruss.Nodes)
		{
			Vector2D p = toDrawableCoord(n.Location);

			if (touchPoint.X > p.X - 30 && touchPoint.X < p.X + 30)
			{
				if (touchPoint.Y > p.Y - 30 && touchPoint.Y < p.Y + 30)
				{
					return n;
				}
			}
		}
		return null;
	}

	private Member CheckMemberTouch(Vector2D touchPoint)
	{
		double closestDistance = Double.POSITIVE_INFINITY;
		int closestIndex = -1;

		for (Member m : Global.CurrentTruss.Members)
		{
			double dist = m.DistanceFromPoint(touchPoint.X, touchPoint.Y, ViewControl.this);
			if (dist < closestDistance)
			{
				closestDistance = dist;
				closestIndex = Global.CurrentTruss.Members.indexOf(m);
			}

		}
		if (closestDistance < 30)
		{
			return Global.CurrentTruss.Members.get(closestIndex);
		}
		return null;
	}


	private Load CheckPointLoadTouch(Vector2D touchPoint)
	{
		double closestDistance = Double.POSITIVE_INFINITY;
		int closestIndex = -1;

		for (Load pl : Global.CurrentTruss.PointLoads)
		{
			double dist = pl.DistanceFromPoint(touchPoint.X, touchPoint.Y, ViewControl.this);
			if (dist < closestDistance)
			{
				closestDistance = dist;
				closestIndex = Global.CurrentTruss.PointLoads.indexOf(pl);
			}

		}
		if (closestDistance < 30)
		{
			return Global.CurrentTruss.PointLoads.get(closestIndex);
		}
		return null;
	}


	class mListener implements GestureDetector.OnGestureListener
	{


		@Override
		public boolean onDown(MotionEvent e)
		{
			isScaling = false;
			return true;
		}

		@Override
		public void onShowPress(MotionEvent motionEvent)
		{

		}

		@Override
		public boolean onSingleTapUp(MotionEvent event)
		{
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2)
		{
			if (!isScaling)
			{
				double X = origin.X + v / zoom;
				double Y = origin.Y - v2 / zoom;

				Bounds bnds = Global.CurrentTruss.getBounds(ViewControl.this);

				if (bnds.maxX < X)
				{
					X = bnds.maxX;
				}

				if (bnds.maxY < Y)
				{
					Y = bnds.maxY;
				}

				if ((bnds.minX - X) * zoom > ViewControl.this.getWidth())
				{
					X = bnds.minX - (ViewControl.this.getWidth() - 1) / zoom;
				}

				if ((bnds.minY - Y) * zoom > ViewControl.this.getHeight())
				{
					Y = bnds.minY - (ViewControl.this.getHeight() - 1) / zoom;
				}


				origin.setXY(X, Y);
				invalidate();
			} else
			{
				isScaling = true;
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event)
		{
			if (selectListener == null) return;
			boolean sel = false;


			Vector2D touchPoint = new Vector2D(event.getX(), event.getY());
			Node n = CheckNodeTouch(touchPoint);
			if (n != null)
			{
				if (selectListener.onNodeLongPressed(n)) return;
			}

			Member m = CheckMemberTouch(touchPoint);
			if (m != null)
			{
				if (selectListener.onMemberPressed(m)) return;
			}

			selectListener.onLongTouchWithoutSelection();
		}

		@Override
		public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2)
		{
			return false;
		}
	}

	class scaleListener implements ScaleGestureDetector.OnScaleGestureListener
	{

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{

			Vector2D focus = new Vector2D(detector.getFocusX(), detector.getFocusY());
			centeredZoom(focus, detector.getScaleFactor() * zoom);

			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector)
		{
			isScaling = true;
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector scaleGestureDetector)
		{
			isScaling = false;
		}
	}

	class doubleTapListener implements GestureDetector.OnDoubleTapListener
	{

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e)
		{
			if (selectListener == null) return true;
			boolean sel = false;


			Vector2D touchPoint = new Vector2D(e.getX(), e.getY());

			Node n = CheckNodeTouch(touchPoint);
			if (n != null)
			{
				if (selectListener.onNodePressed(n)) return true;
			}


			if (!(MainActivity.uiState == UIState.ADD_HINGE_SUPPORT || MainActivity.uiState == UIState.ADD_ROLLER_SUPPORT || MainActivity.uiState == UIState.ADD_MEMBER || MainActivity.uiState == UIState.ADD_POINT_LOAD))
			{
				Load pl = CheckPointLoadTouch(touchPoint);
				if (pl != null)
				{
					if (selectListener.onPointLoadPressed(pl)) return true;
				}
			}


			Member m = CheckMemberTouch(touchPoint);
			if (m != null)
			{
				if (selectListener.onMemberPressed(m)) return true;
			}


			selectListener.onTouchWithoutSelection();
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			//Smooth zoom animation: zoom 1.5X in 200ms
			final float initial = zoom;
			float f = 1.5f * zoom;
			if (f > 3000) f = 2999;
			final float finalzoom = f;
			final float increment = (finalzoom - initial) / 10;

			final Vector2D ctr = new Vector2D(e.getX(), e.getY());

			final Timer rTimer = new Timer();
			TimerTask rTask = new TimerTask()
			{
				@Override
				public void run()
				{
					((Activity) ctx).runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							float v = zoom + increment;
							if (v > finalzoom)
							{
								rTimer.cancel();
							} else
							{
								centeredZoom(ctr, zoom + increment);
							}
						}
					});
				}
			};
			rTimer.schedule(rTask, 0, 20);


			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e)
		{
			return false;
		}
	}


}

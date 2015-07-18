package com.avinab.trusssolver.data.supports;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import com.avinab.trusssolver.data.Bounds;
import com.avinab.trusssolver.data.Node;
import com.avinab.trusssolver.data.Truss;
import com.avinab.trusssolver.math.Vector2D;
import com.avinab.trusssolver.widgets.Drawable;
import com.avinab.trusssolver.widgets.TrussView;

import java.text.DecimalFormat;


/**
 * Avinab
 * 11/16/2014.
 */
public abstract class Support implements Drawable
{
	protected static Paint ReactionPaint;
	protected static Paint SupportPaint;
	final int ARROW_ALPHA = 200;
	final int TEXT_ALPHA = 255;
	public Vector2D Reaction;
	public int supportNode;
	public Truss Parent;


	public Support(Node n)
	{
		Parent = n.Parent;
		supportNode = n.id;

		if (SupportPaint == null)
		{
			SupportPaint = new Paint();
			SupportPaint.setTextSize(20);
			SupportPaint.setStyle(Paint.Style.STROKE);
			SupportPaint.setTextAlign(Paint.Align.CENTER);
			SupportPaint.setStrokeWidth(4f);
			SupportPaint.setAntiAlias(true);
			SupportPaint.setColor(Color.argb(190, 250, 200, 45));
		}
		if (ReactionPaint == null)
		{
			ReactionPaint = new Paint();
			ReactionPaint.setTextSize(20);
			ReactionPaint.setStyle(Paint.Style.FILL);
			ReactionPaint.setTextAlign(Paint.Align.CENTER);
			ReactionPaint.setStrokeWidth(5f);
			ReactionPaint.setAntiAlias(true);
			ReactionPaint.setColor(Color.CYAN);
		}
	}

	public Node getNode()
	{
		return Parent.getNode(supportNode);
	}

	public void DrawReactions(Canvas canvas, TrussView trussView)
	{

		DecimalFormat df = new DecimalFormat(PreferenceManager.getDefaultSharedPreferences(trussView.getContext()).getString("pref_reaction_precision", "0.000"));
		Paint pnt = ReactionPaint;


		if (Math.abs(Reaction.X) > 0.00001)
		{
			//Draw X reaction
			int dirX = Reaction.X < 0 ? +1 : -1;
			Vector2D p = trussView.toDrawableCoord(this.getNode().Location);
			p.X += 10 * dirX;

			Vector2D px = new Vector2D(p.X + 60 * dirX, p.Y);

			ReactionPaint.setAlpha(ARROW_ALPHA);

			canvas.drawLine((float) p.X, (float) p.Y, (float) px.X, (float) px.Y, pnt);
			canvas.drawLine((float) p.X, (float) p.Y, (float) p.X + 10 * dirX, (float) p.Y + 10, pnt);
			canvas.drawLine((float) p.X, (float) p.Y, (float) p.X + 10 * dirX, (float) p.Y - 10, pnt);
			ReactionPaint.setAlpha(TEXT_ALPHA);
			canvas.drawText(df.format(Math.abs(Reaction.X)), (float) px.X, (float) px.Y - 4, pnt);

		}

		if (Math.abs(Reaction.Y) > 0.00001)
		{
			//Draw Y reaction
			Vector2D p = trussView.toDrawableCoord(this.getNode().Location);
			int dirY = Reaction.Y > 0 ? +1 : -1;
			p.Y += 10 * dirY;

			Vector2D py = new Vector2D(p.X, p.Y + 60 * dirY);

			ReactionPaint.setAlpha(ARROW_ALPHA);
			canvas.drawLine((float) p.X, (float) p.Y, (float) py.X, (float) py.Y, pnt);
			canvas.drawLine((float) p.X, (float) p.Y, (float) p.X + 10, (float) p.Y + 10 * dirY, pnt);
			canvas.drawLine((float) p.X, (float) p.Y, (float) p.X - 10, (float) p.Y + 10 * dirY, pnt);
			float yy = (float) py.Y;
			if (dirY > 0)
			{
				yy -= pnt.ascent();
			} else
			{
				yy -= pnt.descent();
			}

			ReactionPaint.setAlpha(TEXT_ALPHA);
			canvas.drawText(df.format(Math.abs(Reaction.Y)), (float) py.X, yy, pnt);

		}
	}

	public abstract void Draw(Canvas canvas, TrussView view);

	@Override
	public Bounds getBounds(TrussView trussView)
	{
		return this.getNode().getBounds(trussView);
	}
}

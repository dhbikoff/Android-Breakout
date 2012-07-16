package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Block extends ShapeDrawable {
	
	private Paint paint;
	
	public Block(Rect rect, int color) {
		super(new RectShape());
		this.setBounds(rect);
		paint = new Paint();
		paint.setColor(color);
	}
	
	public void drawBlock(Canvas canvas) {
		canvas.drawRect(this.getBounds(), paint);
	}
	
	public int getColor() {
		return paint.getColor();
	}
}

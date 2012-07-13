package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Block extends ShapeDrawable {
	
	public Block(Rect rect) {
		super(new RectShape());
		this.setBounds(rect);
	}
	
	public void drawBlock(Canvas canvas, int color) {
		Paint paint = new Paint();
		paint.setColor(color);
		canvas.drawRect(this.getBounds(), paint);
	}

}

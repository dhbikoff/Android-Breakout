package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/**
 * Represents a single game block object. Extends a ShapeDrawable to include a
 * Color value and a method for exporting the coordinates and color in order to
 * save its state.
 * */
public class Block extends ShapeDrawable {

	private Paint paint;
	private int blockColor;

	/**
	 * Constructor. Uses the superclass to construct the Rect and adds a color
	 * value.
	 * 
	 * @param rect
	 *            Android Rect object
	 * @param color
	 *            number representing a Color value
	 * */
	public Block(Rect rect, int color) {
		super(new RectShape());
		this.setBounds(rect);
		paint = new Paint();
		paint.setColor(color);
		blockColor = color;
	}

	/**
	 * Draws the block to a screen canvas.
	 * 
	 * @param canvas
	 *            graphic canvas
	 * */
	public void drawBlock(Canvas canvas) {
		canvas.drawRect(this.getBounds(), paint);
	}

	/**
	 * Returns the integer representing the block's color.
	 * 
	 * @return color value
	 * */
	public int getColor() {
		return paint.getColor();
	}

	/***
	 * Returns an integer array containing the color and coordinates of the
	 * block. Used to save a block's state to a data file. The first four values
	 * represent the block's coordinates. The last value is the block's color.
	 * 
	 * @return integer array containing the block's coordinates and color values.
	 * */
	public int[] toIntArray() {
		int[] arr = { this.getBounds().left, this.getBounds().top,
				this.getBounds().right, this.getBounds().bottom, blockColor };
		return arr;
	}
}

package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/**
 * Represents the game paddle. Extends a ShapeDrawable to include a color value and
 * methods for setting the paddle's location on screen.
 * */
public class Paddle extends ShapeDrawable {

	// paddle dimensions
	private int left;
	private int right;
	private int top;
	private int bottom;
	private int paddle_width;
	private int paddle_height;
	private int paddle_offset; // bottom screen offset
	private int paddle_move_offset; // move paddle to touch event speed

	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;

	/**
	 * Constructor
	 * */
	public Paddle() {
		super(new RectShape());
		this.getPaint().setColor(Color.WHITE);
	}

	/**
	 * Sets the paddle's dimensions and coordinates based on screen width and
	 * height.
	 * 
	 * @param width
	 *            screen width
	 * @param height
	 *            screen height
	 * */
	public void initCoords(int width, int height) {
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;

		paddle_width = SCREEN_WIDTH / 10;
		paddle_height = SCREEN_WIDTH / 72;
		paddle_offset = SCREEN_HEIGHT / 6;

		left = (SCREEN_WIDTH / 2) - paddle_width;
		right = (SCREEN_WIDTH / 2) + paddle_width;
		top = (SCREEN_HEIGHT - paddle_offset) - paddle_height;
		bottom = (SCREEN_HEIGHT - paddle_offset) + paddle_height;

		paddle_move_offset = SCREEN_WIDTH / 15;
	}

	/**
	 * Draws the paddle to a canvas.
	 * 
	 * @param canvas
	 *            graphics canvas
	 * */
	public void drawPaddle(Canvas canvas) {
		this.setBounds(left, top, right, bottom);
		this.draw(canvas);
	}

	/**
	 * Sets the paddle's coordinates. The paddle may move across the screen
	 * along the x axis only.
	 * 
	 * @param x x axis value of touch event. Used to calculate the center of the paddle.
	 * */
	public void movePaddle(int x) {
		if (x >= left && x <= right) {
			left = x - paddle_width;
			right = x + paddle_width;
		} else if (x > right) {
			left += paddle_move_offset;
			right += paddle_move_offset;
		} else if (x < left) {
			left -= paddle_move_offset;
			right -= paddle_move_offset;
		}

		// keep paddle from going off screen left
		if (left < 0) {
			left = 0;
			right = paddle_width * 2;
		}

		// keep paddle from going off screen right
		if (right > SCREEN_WIDTH) {
			right = SCREEN_WIDTH;
			left = SCREEN_WIDTH - (paddle_width * 2);
		}
	}
}

package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Paddle extends ShapeDrawable {
	
	// paddle dimensions
	private int left;
	private int right;
	private int top;
	private int bottom;
	private int paddle_width = 75;
	private int paddle_height = 10;
	private int paddle_offset = 92; // bottom screen offset
	
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	
	public Paddle() {
		super(new RectShape());
		this.getPaint().setColor(Color.WHITE);
	}
	
	public void initCoords(int width, int height) {
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
		
		paddle_width = SCREEN_WIDTH / 10;
		paddle_height = SCREEN_WIDTH / 72;
		paddle_offset = SCREEN_HEIGHT / 13;

		left = (SCREEN_WIDTH / 2) - paddle_width;
		right = (SCREEN_WIDTH / 2) + paddle_width;
		top = (SCREEN_HEIGHT - paddle_offset) - paddle_height;
		bottom = (SCREEN_HEIGHT - paddle_offset) + paddle_height;

	}
	
	public void drawPaddle(Canvas canvas) {
		this.setBounds(left, top, right, bottom);
		this.draw(canvas);
	}

	public void movePaddle(int x) {
		left = x - paddle_width;
		right = x + paddle_width;
	
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

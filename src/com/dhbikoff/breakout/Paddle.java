package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Paddle extends ShapeDrawable {
	
	private int left;
	private int right;
	private int top;
	private int bottom;
	private int paddle_width = 75;
	private int paddle_height = 10;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private int paddle_offset = 92;
	
	public Paddle() {
		super(new RectShape());
		this.getPaint().setColor(Color.WHITE);
	}
	
	public void initCoords(int width, int height) {
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;

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
		
		if (left < 0) {
			left = 0;
			right = paddle_width * 2;
		}
		
		if (right > SCREEN_WIDTH) {
			right = SCREEN_WIDTH;
			left = SCREEN_WIDTH - (paddle_width * 2);
		}	
	}	
}

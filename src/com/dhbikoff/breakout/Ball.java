package com.dhbikoff.breakout;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class Ball extends ShapeDrawable {

	private int left;
	private int right;
	private int top;
	private int bottom;
	private int velocityX = 20;
	private int velocityY = 20;
	private int radius = 10;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private boolean collision = false;
	private Rect mPaddle;
	private Rect ballRect;

	public Ball() {
		super(new OvalShape());
		this.getPaint().setColor(Color.CYAN);
	}

	public void initCoords(int width, int height) {
		Random rnd = new Random();
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
		left = (SCREEN_WIDTH / 2) - radius;
		right = (SCREEN_WIDTH / 2) + radius;
		top = (SCREEN_HEIGHT / 2) - radius;
		bottom = (SCREEN_HEIGHT / 2) + radius;

		int startingXDirection = rnd.nextInt(2);
		if (startingXDirection > 0) {
			velocityX = -velocityX;
		}
	}

	public void drawBall(Canvas canvas) {
		this.setBounds(left, top, right, bottom);
		this.draw(canvas);
		this.setVelocity();
	}

	public void setVelocity() {

		if (velocityY > 0 && collision) {
			velocityY = -velocityY;
			if (velocityX > 0 && ballRect.centerX() < mPaddle.centerX()) {
				velocityX = -velocityX;
			} else if (velocityX < 0 && ballRect.centerX() > mPaddle.centerX()) {
				velocityX = -velocityX;
			}

		}

		if (this.getBounds().right >= SCREEN_WIDTH) {
			velocityX = -velocityX;
		} else if (this.getBounds().left <= 0) {
			velocityX = -velocityX;
		}

		if (this.getBounds().top <= 0) {
			velocityY = -velocityY;
		} else if (this.getBounds().top > SCREEN_HEIGHT) {
			try {
				Thread.sleep(1000);
				initCoords(SCREEN_WIDTH, SCREEN_HEIGHT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		left += velocityX;
		right += velocityX;
		top += velocityY;
		bottom += velocityY;
	}

	public void checkCollision(Paddle paddle) {
		mPaddle = paddle.getBounds();
		ballRect = this.getBounds();
		
		if (ballRect.left >= mPaddle.left - 40 && ballRect.right <= mPaddle.right + 40
				&& ballRect.bottom == mPaddle.top - 20) {
			collision = true;
		} else {
			collision = false;
		}
	}
}

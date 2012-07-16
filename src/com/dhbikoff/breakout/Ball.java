package com.dhbikoff.breakout;

import java.util.ArrayList;
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
	private boolean paddleCollision = false;
	private boolean blockCollision = false;
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
	}

	public void setVelocity() {

		if (blockCollision) {
			velocityY = -velocityY;
			blockCollision = false;
		}

		if (paddleCollision && velocityY > 0) {
			int paddleSplit = (mPaddle.right - mPaddle.left) / 4;
			int ballCenter = ballRect.centerX();
			if (ballCenter < mPaddle.left + paddleSplit) {
				velocityX = -30;
			} else if (ballCenter < mPaddle.left + (paddleSplit * 2)) {
				velocityX = -20;
			} else if (ballCenter < mPaddle.centerX() + paddleSplit) {
				velocityX = 20;
			} else {
				velocityX = 30;
			}
			velocityY = -velocityY;
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

	public void checkPaddleCollision(Paddle paddle) {
		mPaddle = paddle.getBounds();
		ballRect = this.getBounds();
		if (ballRect.left >= mPaddle.left - 40
				&& ballRect.right <= mPaddle.right + 40
				&& ballRect.bottom == mPaddle.top - 20) {
			paddleCollision = true;
		} else {
			paddleCollision = false;
		}
	}

	public int checkBlocksCollision(ArrayList<Block> blocks) {
		int points = 0;
		int blockListLength = blocks.size();
		ballRect = this.getBounds();
		int ballLeft = ballRect.left + velocityX;
		int ballRight = ballRect.right + velocityY;
		int ballTop = ballRect.top + velocityY;
		int ballBottom = ballRect.bottom + velocityY;

		for (int i = blockListLength - 1; i >= 0; i--) {
			Rect blockRect = blocks.get(i).getBounds();
			int color = blocks.get(i).getColor();
			if (ballLeft >= blockRect.left && ballLeft <= blockRect.right
					&& ballTop == blockRect.bottom && ballTop >= blockRect.top) {
				blockCollision = true;
				blocks.remove(i);
			} else if (ballRight <= blockRect.right
					&& ballRight >= blockRect.left
					&& ballTop <= blockRect.bottom && ballTop >= blockRect.top) {
				blockCollision = true;
				blocks.remove(i);
			} else if (ballLeft >= blockRect.left
					&& ballLeft <= blockRect.right
					&& ballBottom <= blockRect.bottom
					&& ballBottom >= blockRect.top) {
				blockCollision = true;
				blocks.remove(i);
			} else if (ballRight <= blockRect.right
					&& ballRight >= blockRect.left
					&& ballBottom <= blockRect.bottom
					&& ballBottom >= blockRect.top) {
				blockCollision = true;
				blocks.remove(i);
			}

			if (blockCollision) {
				if (color == Color.LTGRAY)
					points += 100;
				else if (color == Color.MAGENTA)
					points += 200;
				else if (color == Color.GREEN)
					points += 300;
				else if (color == Color.YELLOW)
					points += 400;
				else if (color == Color.RED)
					points += 500;
				return points;
			}
		}
		return points;
	}
}

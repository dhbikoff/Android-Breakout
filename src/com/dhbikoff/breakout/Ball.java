package com.dhbikoff.breakout;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public class Ball extends ShapeDrawable {

	// ball dimensions
	private int left;
	private int right;
	private int top;
	private int bottom;
	private int radius;

	// ball speed
	private int velocityX;
	private int velocityY;

	private final int resetBallTimer = 1000; // when ball hits screen bottom
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private boolean paddleCollision;
	private boolean blockCollision;
	private Rect mPaddle;
	private Rect ballRect;

	public Ball() {
		super(new OvalShape());
		this.getPaint().setColor(Color.CYAN);
	}

	public void initCoords(int width, int height) {
		Random rnd = new Random(); // starting x velocity direction
		
		paddleCollision = false;
		blockCollision = false;
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
		
		radius = SCREEN_WIDTH / 72;
		velocityX = velocityY = radius * 2; // starting velocity

		// ball coordinates
		left = (SCREEN_WIDTH / 2) - radius;
		right = (SCREEN_WIDTH / 2) + radius;
		top = (SCREEN_HEIGHT / 2) - radius;
		bottom = (SCREEN_HEIGHT / 2) + radius;

		int startingXDirection = rnd.nextInt(2); // random beginning direction
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
				velocityX = -(radius * 3);
			} else if (ballCenter < mPaddle.left + (paddleSplit * 2)) {
				velocityX = -(radius * 2);
			} else if (ballCenter < mPaddle.centerX() + paddleSplit) {
				velocityX = radius * 2;
			} else {
				velocityX = radius * 3;
			}
			velocityY = -velocityY;
		}

		if (this.getBounds().right >= SCREEN_WIDTH) {
			velocityX = -velocityX;
		} else if (this.getBounds().left <= 0) {
			this.setBounds(0, top, radius * 2, bottom);
			velocityX = -velocityX;
		}

		if (this.getBounds().top <= 0) {
			velocityY = -velocityY;
		} else if (this.getBounds().top > SCREEN_HEIGHT) {
			try {
				Thread.sleep(resetBallTimer);
				initCoords(SCREEN_WIDTH, SCREEN_HEIGHT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// move ball
		left += velocityX;
		right += velocityX;
		top += velocityY;
		bottom += velocityY;
	}

	public boolean checkPaddleCollision(Paddle paddle) {
		mPaddle = paddle.getBounds();
		ballRect = this.getBounds();

		if (ballRect.left >= mPaddle.left - (radius * 2)
				&& ballRect.right <= mPaddle.right + (radius * 2)
				&& ballRect.bottom >= mPaddle.top - (radius * 2)
				&& ballRect.top < mPaddle.bottom) {
			paddleCollision = true;
		} else
			paddleCollision = false;

		return paddleCollision;
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

			if (ballLeft >= blockRect.left - (radius * 2)
					&& ballLeft <= blockRect.right + (radius * 2)
					&& (ballTop == blockRect.bottom || ballTop == blockRect.top)) {
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
				return points += getPoints(color);
			}
		}
		return points;
	}

	private int getPoints(int color) {
		if (color == Color.LTGRAY)
			return 100;
		else if (color == Color.MAGENTA)
			return 200;
		else if (color == Color.GREEN)
			return 300;
		else if (color == Color.YELLOW)
			return 400;
		else if (color == Color.RED)
			return 500;
		else {
			return 0;
		}
	}

	public int getVelocityY() {
		return velocityY;
	}
}

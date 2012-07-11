package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

	private SurfaceHolder holder;
	private Thread gameThread = null;
	private boolean running = false;
	private Canvas canvas;
	private ShapeDrawable circle;
	private int circleLeft;
	private int circleRight;
	private int circleTop;
	private int circleBottom;
	private int circleVelocityX = 20;
	private int circleVelocityY = 20;
	private int CIRCLE_RADIUS = 10;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private boolean checkSize = true;

	public GameView(Context context) {
		super(context);
		holder = getHolder();
		circle = new ShapeDrawable(new OvalShape());
		circle.getPaint().setColor(Color.CYAN);
	}
	
	private void initCircleCoords() {
		SCREEN_WIDTH = canvas.getWidth();
		SCREEN_HEIGHT = canvas.getHeight();
		circleLeft = (SCREEN_WIDTH/2) - CIRCLE_RADIUS;
		circleRight = (SCREEN_WIDTH/2) + CIRCLE_RADIUS;
		circleTop = (SCREEN_HEIGHT/2) - CIRCLE_RADIUS;
		circleBottom = (SCREEN_HEIGHT/2) + CIRCLE_RADIUS;
	}

	public void run() {

		while (running) {
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (holder.getSurface().isValid()) {
				canvas = holder.lockCanvas();
				if (checkSize) {
					initCircleCoords();
					checkSize = false;
				}
					
				canvas.drawColor(Color.BLACK);
				circle.setBounds(circleLeft, circleTop, circleRight,
						circleBottom);
				circle.draw(canvas);
				updateVelocity();
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private void updateVelocity() {
		if (circle.getBounds().right > canvas.getWidth()) {
			circleVelocityX = -circleVelocityX;
		} else if (circle.getBounds().left < 0) {
			circleVelocityX = Math.abs(circleVelocityX);
		}
		
		if (circle.getBounds().top < 0) {
			circleVelocityY = Math.abs(circleVelocityY);
		} else if (circle.getBounds().bottom >= canvas.getHeight() - 5) {
			circleVelocityY = -circleVelocityY;
		}

		circleLeft += circleVelocityX;
		circleRight += circleVelocityX;
		circleTop += circleVelocityY;
		circleBottom += circleVelocityY;
	}

	public void pause() {
		running = false;
		while (true) {
			try {
				gameThread.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

		}
		gameThread = null;
	}

	public void resume() {
		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}
}

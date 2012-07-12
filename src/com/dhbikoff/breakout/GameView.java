package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

	private boolean touched = false;
	private float eventX;
	private SurfaceHolder holder;
	private Thread gameThread = null;
	private boolean running = false;
	private Canvas canvas;
	private boolean checkSize = true;
	private Ball ball;
	private Paddle paddle;

	public GameView(Context context) {
		super(context);
		holder = getHolder();
		ball = new Ball();
		paddle = new Paddle();
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
				canvas.drawColor(Color.BLACK);

				if (checkSize) {
					ball.initCoords(canvas.getWidth(), canvas.getHeight());
					paddle.initCoords(canvas.getWidth(), canvas.getHeight());
					checkSize = false;
				}

				if (touched) {
					paddle.movePaddle((int) eventX);
					touched = false;
				}
				
				paddle.drawPaddle(canvas);
				ball.drawBall(canvas);
				ball.checkCollision(paddle);
				holder.unlockCanvasAndPost(canvas);
			}
		}
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			eventX = event.getX();
			touched = true;
		}
		return true;
	}
}

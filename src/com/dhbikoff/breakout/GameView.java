package com.dhbikoff.breakout;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
	private boolean newGame = true;
	private int waitCount = 0;
	private Ball ball;
	private Paddle paddle;
	private ArrayList<Block> blocksList;
	private int points = 0;
	private Paint scorePaint;
	private String score = "SCORE = ";

	public GameView(Context context) {
		super(context);
		holder = getHolder();
		ball = new Ball();
		paddle = new Paddle();
		blocksList = new ArrayList<Block>();
		
		scorePaint = new Paint();
		scorePaint.setColor(Color.WHITE);
		scorePaint.setTextSize(25);
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
				
				if (blocksList.size() == 0) {
					checkSize = true;
					newGame = true;
					points = 0;
				}
					

				if (checkSize) {
					ball.initCoords(canvas.getWidth(), canvas.getHeight());
					paddle.initCoords(canvas.getWidth(), canvas.getHeight());
					initBlocks(canvas);
					checkSize = false;
					
				}

				drawBlocks(canvas);

				if (touched) {
					paddle.movePaddle((int) eventX);
					touched = false;
				}

				paddle.drawPaddle(canvas);
				ball.drawBall(canvas);
				
				if (newGame) {
					waitCount = 0;
					newGame = false;
				}
				
				waitCount++;
				
				if (waitCount > 66) {
					ball.setVelocity();
					ball.checkPaddleCollision(paddle);
					points += ball.checkBlocksCollision(blocksList);
				}
				
				String printScore = score + points;
				canvas.drawText(printScore, 0, 25, scorePaint);

				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private void initBlocks(Canvas canvas) {
		int blockHeight = 20;
		int spacing = 5;
		int topOffset = 120;
		int blockWidth = (canvas.getWidth() / 10) - spacing;

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				int y_coordinate = (i * (blockHeight + spacing)) + topOffset;
				int x_coordinate = j * (blockWidth + spacing);

				Rect r = new Rect();
				r.set(x_coordinate, y_coordinate, x_coordinate + blockWidth,
						y_coordinate + blockHeight);

				int color;

				if (i < 2)
					color = Color.RED;
				else if (i < 4)
					color = Color.YELLOW;
				else if (i < 6)
					color = Color.GREEN;
				else if (i < 8)
					color = Color.MAGENTA;
				else
					color = Color.LTGRAY;

				Block block = new Block(r, color);

				blocksList.add(block);
			}
		}

	}

	private void drawBlocks(Canvas canvas) {
		for (int i = 0; i < blocksList.size(); i++) {
			blocksList.get(i).drawBlock(canvas);
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

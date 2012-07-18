package com.dhbikoff.breakout;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

	private final int frameRate = 33;
	private final int startTimer = 0;
	private boolean touched = false; // touch event
	private float eventX; // x coordinate for touch event
	private SurfaceHolder holder;
	private Thread gameThread = null;
	private boolean running = false; // thread state
	private Canvas canvas;
	private boolean checkSize = true; // need initial game setup
	private boolean newGame = true;
	private int waitCount = 0; // count to start ball animation
	private Ball ball;
	private Paddle paddle;
	private ArrayList<Block> blocksList;
	private String getReady = "GET READY...";
	private Paint getReadyPaint;
	private int points = 0;
	private Paint scorePaint;
	private String score = "SCORE = ";
	private SoundPool soundPool;
	private int paddleSoundId;
	private int blockSoundId;

	public GameView(Context context) {
		super(context);
		holder = getHolder();
		ball = new Ball();
		paddle = new Paddle();
		blocksList = new ArrayList<Block>();

		scorePaint = new Paint();
		scorePaint.setColor(Color.WHITE);
		scorePaint.setTextSize(25);

		getReadyPaint = new Paint();
		getReadyPaint.setColor(Color.WHITE);
		getReadyPaint.setTextSize(45);

		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		paddleSoundId = soundPool.load(this.getContext(), R.raw.paddle, 0);
		blockSoundId = soundPool.load(this.getContext(), R.raw.block, 0);
	}

	public void run() {

		while (running) {
			try {
				Thread.sleep(frameRate); // frame rate
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (holder.getSurface().isValid()) {
				canvas = holder.lockCanvas();
				canvas.drawColor(Color.BLACK);

				// no blocks, must begin/restart game
				if (blocksList.size() == 0) {
					checkSize = true;
					newGame = true;
				}

				if (checkSize) {
					ball.initCoords(canvas.getWidth(), canvas.getHeight());
					paddle.initCoords(canvas.getWidth(), canvas.getHeight());
					initBlocks(canvas);
					checkSize = false;
				}

				drawBlocks(canvas);

				// touch listener
				if (touched) {
					paddle.movePaddle((int) eventX);
					touched = false;
				}

				paddle.drawPaddle(canvas);
				ball.drawBall(canvas);

				// pause screen on new game
				if (newGame) {
					waitCount = 0;
					newGame = false;
				}

				waitCount++;

				// run game if not waiting
				if (waitCount > startTimer) {
					ball.setVelocity();
					
					// paddle collision
					if (ball.checkPaddleCollision(paddle)
							&& ball.getVelocityY() > 0) {
						soundPool.play(paddleSoundId, 1, 1, 1, 0, 1);
					}
					
					// block collision
					int oldPoints = points;
					points += ball.checkBlocksCollision(blocksList);
					if (oldPoints != points) {
						soundPool.play(blockSoundId, 1, 1, 1, 0, 1);
					}
					
				} else {
					// prompt user to begin
					canvas.drawText(getReady, canvas.getWidth() / 2 - 100,
							(canvas.getHeight() / 2) - 45, getReadyPaint);
				}

				String printScore = score + points;
				canvas.drawText(printScore, 0, 25, scorePaint);
				Log.d("height", canvas.getHeight() + "");
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private void initBlocks(Canvas canvas) {
		int blockHeight = canvas.getWidth()/36;
		int spacing = canvas.getWidth()/144;
		int topOffset = canvas.getHeight()/10;
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

package com.dhbikoff.breakout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
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

	private int playerTurns = 3;
	private Paint turnsPaint;
	private String playerTurnsText = "TURNS = ";
	private boolean soundToggle; // sound on/off
	private int startNewGame; // new game or continue
	private ObjectOutputStream oos;
	private final String FILE_PATH = "data/data/com.dhbikoff.breakout/data.dat";
	private final int frameRate = 33;
	private final int startTimer = 66;
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

	public GameView(Context context, int launchNewGame, boolean sound) {
		super(context);
		startNewGame = launchNewGame; // new game or continue
		soundToggle = sound;
		holder = getHolder();
		ball = new Ball(this.getContext(), soundToggle);
		paddle = new Paddle();
		blocksList = new ArrayList<Block>();

		scorePaint = new Paint();
		scorePaint.setColor(Color.WHITE);
		scorePaint.setTextSize(25);
		
		turnsPaint = new Paint();
		turnsPaint.setTextAlign(Paint.Align.RIGHT);
		turnsPaint.setColor(Color.WHITE);
		turnsPaint.setTextSize(25);

		getReadyPaint = new Paint();
		getReadyPaint.setTextAlign(Paint.Align.CENTER);
		getReadyPaint.setColor(Color.WHITE);
		getReadyPaint.setTextSize(45);
	}

	// game engine thread
	public void run() {
		while (running) {
			try {
				Thread.sleep(frameRate); // draw screen frame rate
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

				// initialize objects (ball/paddle)
				if (checkSize) {
					initObjects(canvas);
					checkSize = false;
				}

				// touch listener for paddle
				if (touched) {
					paddle.movePaddle((int) eventX);
				}

				drawToCanvas(canvas); // draw all objects on screen
				
				// pause screen on new game
				if (newGame) {
					waitCount = 0;
					newGame = false;
				}
				waitCount++;

				engine(canvas, waitCount);
				// draw player score
				String printScore = score + points;
				canvas.drawText(printScore, 0, 25, scorePaint);
				
				String turns = playerTurnsText + playerTurns;
				canvas.drawText(turns, canvas.getWidth(), 25, turnsPaint);

				holder.unlockCanvasAndPost(canvas); // release canvas
			}
		}
	}
	
	private void drawToCanvas(Canvas canvas) {
		drawBlocks(canvas);
		paddle.drawPaddle(canvas);
		ball.drawBall(canvas);
	}

	// run game if not waiting
	private void engine(Canvas canvas, int waitCt) {
		if (waitCount > startTimer) {
			playerTurns -= ball.setVelocity();
			// paddle collision
			ball.checkPaddleCollision(paddle);
			// block collision and points tally
			points += ball.checkBlocksCollision(blocksList);
		} else {
			// alert user that the game will begin
			canvas.drawText(getReady, canvas.getWidth() / 2,
					(canvas.getHeight() / 2) - (ball.getBounds().height()), getReadyPaint);
		}
	}

	private void initObjects(Canvas canvas) {
		ball.initCoords(canvas.getWidth(), canvas.getHeight());
		paddle.initCoords(canvas.getWidth(), canvas.getHeight());
		if (startNewGame == 0) {
			restoreGameData();
		} else {
			initBlocks(canvas);
		}
	}

	private void restoreBlocks(ArrayList<int[]> arr) {
		for (int i = 0; i < arr.size(); i++) {
			Rect r = new Rect();
			int[] blockNums = arr.get(i);
			r.set(blockNums[0], blockNums[1], blockNums[2], blockNums[3]);
			Block b = new Block(r, blockNums[4]);
			blocksList.add(b);
		}
	}

	private void restoreGameData() {
		try {
			FileInputStream fis = new FileInputStream(
					FILE_PATH);
			ObjectInputStream ois = new ObjectInputStream(fis);
			points = ois.readInt(); // restore player points
			@SuppressWarnings("unchecked")
			ArrayList<int[]> arr = (ArrayList<int[]>) ois.readObject();
			restoreBlocks(arr);
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		startNewGame = 1; // only restore once
	}

	private void initBlocks(Canvas canvas) {
		int blockHeight = canvas.getWidth() / 36;
		int spacing = canvas.getWidth() / 144;
		int topOffset = canvas.getHeight() / 10;
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

	private void saveGameData() {
		ArrayList<int[]> arr = new ArrayList<int[]>();

		for (int i = 0; i < blocksList.size(); i++) {
			arr.add(blocksList.get(i).toIntArray());
		}

		try {
			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			oos = new ObjectOutputStream(fos);
			oos.writeInt(points);
			oos.writeObject(arr);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		saveGameData();
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
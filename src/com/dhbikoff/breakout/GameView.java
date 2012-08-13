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

/**
 * Creates and draws the graphics for the game. Runs a Thread for animation and
 * game physics. Saves and restores game data when paused or restored.
 * 
 */
public class GameView extends SurfaceView implements Runnable {

	private boolean showGameOverBanner = false;
	private int levelCompleted = 0;
	private int playerTurns;
	private int PLAYER_TURNS_NUM = 3;
	private Paint turnsPaint;
	private String playerTurnsText = "TURNS = ";
	private boolean soundToggle;
	private int startNewGame;
	private ObjectOutputStream oos;
	private final String FILE_PATH = "data/data/com.dhbikoff.breakout/data.dat";
	private final int frameRate = 33;
	private final int startTimer = 66;
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
	private String getReady = "GET READY...";
	private Paint getReadyPaint;
	private int points = 0;
	private Paint scorePaint;
	private String score = "SCORE = ";

	/**
	 * Constructor. Sets sound state and new game signal depending on the
	 * incoming intent from the Breakout class. Instantiates the ball, blocks,
	 * and paddle. Sets up the Paint parameters for drawing text to the screen.
	 * */
	public GameView(Context context, int launchNewGame, boolean sound) {
		super(context);
		startNewGame = launchNewGame; // new game or continue
		playerTurns = PLAYER_TURNS_NUM;
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

	/**
	 * Runs the game thread. Sets the frame rate for drawing graphics. Acquires
	 * a canvas for drawing. If no blocks exist, initialize game objects. Moves
	 * the paddle according to touch events. Checks for collisions as the ball's
	 * moves. Keeps track of player turns and ends the game when turns run out.
	 * Awards the player a turn when a level is completed. Draws text to the
	 * screen showing player score and turns left. Draws text to announce when
	 * the game begins or ends.
	 * 
	 * */
	public void run() {
		while (running) {
			try {
				Thread.sleep(frameRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (holder.getSurface().isValid()) {
				canvas = holder.lockCanvas();
				canvas.drawColor(Color.BLACK);

				if (blocksList.size() == 0) {
					checkSize = true;
					newGame = true;
					levelCompleted++;
				}

				if (checkSize) {
					initObjects(canvas);
					checkSize = false;
					// extra turn for finished level
					if (levelCompleted > 1) {
						playerTurns++;
					}
				}

				if (touched) {
					paddle.movePaddle((int) eventX);
				}

				drawToCanvas(canvas);

				// pause screen on new game
				if (newGame) {
					waitCount = 0;
					newGame = false;
				}
				waitCount++;

				engine(canvas, waitCount);
				String printScore = score + points;
				canvas.drawText(printScore, 0, 25, scorePaint);
				String turns = playerTurnsText + playerTurns;
				canvas.drawText(turns, canvas.getWidth(), 25, turnsPaint);
				holder.unlockCanvasAndPost(canvas); // release canvas
			}
		}
	}

	/**
	 * Draws graphics to the screen.
	 * 
	 * @param canvas
	 *            graphics canvas
	 * */
	private void drawToCanvas(Canvas canvas) {
		drawBlocks(canvas);
		paddle.drawPaddle(canvas);
		ball.drawBall(canvas);
	}

	/**
	 * Pauses the animation until the wait counter is satisfied. Sets the
	 * velocity and coordinates of the ball. Checks for collisions. Checks if
	 * the game is over. Draws text to alert the user if the ball restarts or
	 * the game is over.
	 * 
	 * @param canvas
	 *            graphics canvas
	 * @param waitCt
	 *            number of frames to pause the game
	 * */
	private void engine(Canvas canvas, int waitCt) {
		if (waitCount > startTimer) {
			showGameOverBanner = false;
			playerTurns -= ball.setVelocity();
			if (playerTurns < 0) {
				showGameOverBanner = true;
				gameOver(canvas);
				return;
			}
			// paddle collision
			ball.checkPaddleCollision(paddle);
			// block collision and points tally
			points += ball.checkBlocksCollision(blocksList);
		}

		else {
			if (showGameOverBanner) {
				getReadyPaint.setColor(Color.RED);
				canvas.drawText("GAME OVER!!!", canvas.getWidth() / 2,
						(canvas.getHeight() / 2) - (ball.getBounds().height())
								- 50, getReadyPaint);
			}
			getReadyPaint.setColor(Color.WHITE);
			canvas.drawText(getReady, canvas.getWidth() / 2,
					(canvas.getHeight() / 2) - (ball.getBounds().height()),
					getReadyPaint);

		}
	}

	/**
	 * Resets variables to signal a new game. Deletes the remaining blocks list.
	 * When the run function sees the blocks list is empty, it will initialize a
	 * new game board.
	 * 
	 * @param canvas
	 *            graphics canvas
	 * */
	private void gameOver(Canvas canvas) {
		levelCompleted = 0;
		points = 0;
		playerTurns = PLAYER_TURNS_NUM;
		blocksList.clear();
	}

	/**
	 * Instantiates graphical objects. Restores game state if an existing game
	 * is continued.
	 * 
	 * @param canvas
	 *            graphical canvas
	 * */
	private void initObjects(Canvas canvas) {
		touched = false; // reset paddle location
		ball.initCoords(canvas.getWidth(), canvas.getHeight());
		paddle.initCoords(canvas.getWidth(), canvas.getHeight());
		if (startNewGame == 0) {
			restoreGameData();
		} else {
			initBlocks(canvas);
		}
	}

	/**
	 * Restores a saved ArrayList of blocks. Reads through an ArrayList of integer
	 * Arrays. Passes the values to construct a block and adds the block to an
	 * ArrayList.
	 * 
	 * @param arr
	 *            ArrayList of integer arrays containing the coordinates and
	 *            color of the saved blocks.
	 * */
	private void restoreBlocks(ArrayList<int[]> arr) {
		for (int i = 0; i < arr.size(); i++) {
			Rect r = new Rect();
			int[] blockNums = arr.get(i);
			r.set(blockNums[0], blockNums[1], blockNums[2], blockNums[3]);
			Block b = new Block(r, blockNums[4]);
			blocksList.add(b);
		}
	}

	/**
	 * Opens a saved game file and reads in data to restore saved game state.
	 * */
	private void restoreGameData() {
		try {
			FileInputStream fis = new FileInputStream(FILE_PATH);
			ObjectInputStream ois = new ObjectInputStream(fis);
			points = ois.readInt(); // restore player points
			playerTurns = ois.readInt(); // restore player turns
			@SuppressWarnings("unchecked")
			ArrayList<int[]> arr = (ArrayList<int[]>) ois.readObject();
			restoreBlocks(arr); // restore blocks
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

	/**
	 * Initializes blocks. Measures the width and height of the canvas for the
	 * dimensions and coordinates of the blocks. Sets the color depending on the
	 * block's row. Adds the block to an ArrayList.
	 * 
	 * @param canvas
	 *            graphics canvas
	 * */
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

	/**
	 * Draws blocks to screen
	 * 
	 * @param canvas
	 *            graphical canvas
	 * */
	private void drawBlocks(Canvas canvas) {
		for (int i = 0; i < blocksList.size(); i++) {
			blocksList.get(i).drawBlock(canvas);
		}
	}

	/**
	 * Saves game state. Reads block color and coordinates into an ArrayList.
	 * Saves blocks, player points, and player turns into a data file.
	 * */
	private void saveGameData() {
		ArrayList<int[]> arr = new ArrayList<int[]>();

		for (int i = 0; i < blocksList.size(); i++) {
			arr.add(blocksList.get(i).toIntArray());
		}

		try {
			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			oos = new ObjectOutputStream(fos);
			oos.writeInt(points);
			oos.writeInt(playerTurns);
			oos.writeObject(arr);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves game data and destroys Thread.
	 * */
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

	/**
	 * Resumes the game. Starts a new game Thread. 
	 * */
	public void resume() {
		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	/**
	 * Overridden Touch event listener. Reads screen touches to move the paddle.
	 * 
	 * @return true if there has been a touch event
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			eventX = event.getX();
			touched = true;
		}
		return touched;
	}
}

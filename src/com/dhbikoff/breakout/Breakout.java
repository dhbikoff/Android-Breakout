package com.dhbikoff.breakout;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Activity for the running game. Holds the game's graphics thread. Saves and
 * restores game data when paused or resumed.
 * 
 */
public class Breakout extends Activity {

	private boolean sound;
	private GameView gameView;

	/**
	 * Activity constructor. Acquires media volume control. Hides the titlebar
	 * and requests a fullscreen window. Receives an intent from Splash. Reads
	 * intent values for sound state and new/continue game. Passes the new game
	 * value to the game's thread which signals whether to start a new game or
	 * continue and existing game.
	 * 
	 * @param savedInstanceState
	 *            saved data from a previous run of this Activity
	 * 
	 *            {@inheritDoc}
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		Intent intent = getIntent();
		int newGame = intent.getIntExtra("NEW_GAME", 1);
		sound = intent.getBooleanExtra("SOUND_ON_OFF", true);

		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// initialize graphics and game thread
		gameView = new GameView(this, newGame, sound);
		setContentView(gameView);
	}

	/**
	 * Called when the system pauses this Activity. Saves game data and stops
	 * the game's thread from running.
	 * 
	 * {@inheritDoc}
	 * */
	@Override
	protected void onPause() {
		super.onPause();
		gameView.pause();
	}

	/**
	 * Called when the system resumes this Activity. Restores game data and runs
	 * game thread.
	 * 
	 * {@inheritDoc}
	 * */
	@Override
	protected void onResume() {
		super.onResume();
		gameView.resume();
	}
}

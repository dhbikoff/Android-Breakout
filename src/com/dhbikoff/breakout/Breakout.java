package com.dhbikoff.breakout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Breakout extends Activity {

	private GameView gameView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int newGame = intent.getIntExtra("NEW_GAME", 1);
		
		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		gameView = new GameView(this, newGame);
		setContentView(gameView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		gameView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		gameView.resume();
	}
}

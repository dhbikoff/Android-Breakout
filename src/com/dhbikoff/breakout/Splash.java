package com.dhbikoff.breakout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Splash extends Activity {

	private final String NEW_GAME = "NEW_GAME";
	private int newGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}
	
	public void newGame(View view) {
		newGame = 1;
		Intent intent = new Intent(this, Breakout.class);
		intent.putExtra(NEW_GAME, newGame);
		startActivity(intent);
	}
	
	public void contGame(View view) {
		newGame = 0;
		Intent intent = new Intent(this, Breakout.class);
		intent.putExtra(NEW_GAME, newGame);
		startActivity(intent);
	}
}
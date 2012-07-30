package com.dhbikoff.breakout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Splash extends Activity {

	private final String FILE_PATH = "data/data/com.dhbikoff.breakout/data.dat";
	private final String NEW_GAME = "NEW_GAME";
	private int newGame;
	private String scoreStr = "High Score = ";
	private int highScore;
	private static final String HIGH_SCORE_PREF = "HIGH_SCORE_PREF";
	private boolean sound;
	private final String SOUND_ON_OFF = "SOUND_ON_OFF";
	private static final String SOUND_PREFS = "SOUND_PREFS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}

	private int fetchHighScore() {
		int points = 0;

		FileInputStream fis;
		try {
			fis = new FileInputStream(FILE_PATH);
			ObjectInputStream ois = new ObjectInputStream(fis);
			points = ois.readInt(); // restore player points
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return points;
	}

	private void showHighScore() {
		int points = fetchHighScore();

		if (points > highScore) {
			highScore = points;
		}

		TextView hiScore = (TextView) findViewById(R.id.hiScoreView);
		hiScore.setText(scoreStr + highScore);
	}

	// new game button
	public void newGame(View view) {
		newGame = 1;
		Intent intent = new Intent(this, Breakout.class);
		intent.putExtra(NEW_GAME, newGame);
		intent.putExtra(SOUND_ON_OFF, sound);
		startActivity(intent);
	}

	// continue saved game button
	public void contGame(View view) {
		newGame = 0;
		Intent intent = new Intent(this, Breakout.class);
		intent.putExtra(NEW_GAME, newGame);
		intent.putExtra(SOUND_ON_OFF, sound);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Restore preferences
		SharedPreferences soundSettings = getSharedPreferences(SOUND_PREFS, 0);
		sound = soundSettings.getBoolean("soundOn", true);
		SharedPreferences scoreSettings = getSharedPreferences(HIGH_SCORE_PREF,
				0);
		highScore = scoreSettings.getInt("highScore", 0);

		ToggleButton soundButton = (ToggleButton) findViewById(R.id.soundToggleButton);
		soundButton.setChecked(sound);

		showHighScore();
	}

	public void soundToggle(View v) {
		sound = ((ToggleButton) v).isChecked();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// save sound settings and high score
		SharedPreferences soundSettings = getSharedPreferences(SOUND_PREFS, 0);
		SharedPreferences highScoreSave = getSharedPreferences(HIGH_SCORE_PREF,
				0);
		SharedPreferences.Editor soundEditor = soundSettings.edit();
		SharedPreferences.Editor scoreEditor = highScoreSave.edit();
		scoreEditor.putInt("highScore", highScore);
		soundEditor.putBoolean("soundOn", sound);

		// Commit the edits!
		soundEditor.commit();
		scoreEditor.commit();
	}
}
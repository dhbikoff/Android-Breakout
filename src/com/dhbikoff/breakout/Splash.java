package com.dhbikoff.breakout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}
	
	public void launch(View view) {
		Intent intent = new Intent(this, Breakout.class);
		startActivity(intent);
	}
}

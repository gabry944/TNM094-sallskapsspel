package com.google.sprint1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GameEndedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_ended);
		
		//Necessary to run on UI thread to be able to edit the TextView
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView displayPoints = (TextView) findViewById(R.id.bluePlayer);
				displayPoints.setText("Blå spelare: " + Player.getScore());
			}
		});
	
	}
}

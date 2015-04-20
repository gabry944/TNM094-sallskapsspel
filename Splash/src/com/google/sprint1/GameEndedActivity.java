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
			TextView displayPoints;

			@Override
			public void run() {
				displayPoints = (TextView) findViewById(R.id.bluePlayer);
				displayPoints.setText("Blå spelare: " + GameState.getState().pointsBluePlayer + "p");
				
				displayPoints = (TextView) findViewById(R.id.greenPlayer);
				displayPoints.setText("Grön spelare: " + GameState.getState().pointsGreenPlayer + "p");
			}
		});
	
	}
}

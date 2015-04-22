package com.google.sprint1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
			public void run() 
			{
				displayPoints = (TextView) findViewById(R.id.bluePlayer);
				displayPoints.setText(getResources().getString(R.string.blue_player) + ": " + GameState.getState().players.get(0).score + "p");
				if(GameState.getState().nrOfPlayers >= 2)
				{
					displayPoints = (TextView) findViewById(R.id.greenPlayer);
					displayPoints.setText(getResources().getString(R.string.green_player) + ": " + GameState.getState().players.get(1).score + "p");
					displayPoints.setVisibility(0);//set visible
					if(GameState.getState().nrOfPlayers >= 3)
					{
						displayPoints = (TextView) findViewById(R.id.redPlayer);
						displayPoints.setText(getResources().getString(R.string.red_player) + ": " + GameState.getState().players.get(2).score + "p");
						displayPoints.setVisibility(0);// set visible
						if(GameState.getState().nrOfPlayers >= 4)
						{
							displayPoints = (TextView) findViewById(R.id.yellowPlayer);
							displayPoints.setText(getResources().getString(R.string.yellow_player) + ": " + GameState.getState().players.get(3).score + "p");
							displayPoints.setVisibility(0);//set visible
						}
					}
				}
			}
		});
	
	}
	
	/** Called when the user clicks the Return button */
	public void onPlayAgain(View v) {
		GameState.getState().gameStartTime = System.currentTimeMillis();
		for (int i = 0; i < GameState.getState().nrOfPlayers; i++ )
		{
			GameState.getState().players.get(i).score = 0;
		}
		finish();
	}
	/** Called when the user clicks the finish button */
	public void onFinishGame(View v) {
		//TODO nätverket ska kopplas från också!
		Intent mainMenu = new Intent(this, MainActivity.class);
		startActivity(mainMenu);
	}
}

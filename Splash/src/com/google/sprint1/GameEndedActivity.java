package com.google.sprint1;

import com.google.sprint1.NetworkService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameEndedActivity extends Activity {
	
	// Variables for Service handling
		private NetworkService mService;
		boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_game_ended);
		
		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
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
	
	@Override
	protected void onDestroy() {
		// Unbind from service
		if (mBound) {
			unbindService(mServiceConnection);
			mBound = false;
		}
		super.onDestroy();
		
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
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
}

package com.google.sprint1;

import java.io.IOException;
import java.util.ArrayList;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity to handle the screen between network and the gamescreen
 * 
 * where players should connect to each other before entering gamemode.
 * 
 * 
 */

public class LobbyActivity extends Activity {

	private AssetsExtracter startGame; // a variable used to start the
										// AssetExtraxter class
	private ListView playerListView;
		
	// Function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_lobby);
				
		/* Start game */
		startGame = new AssetsExtracter();
		
		NetworkState.getState().getMobileConnection().initPlayerAdapter(this);
		
		playerListView = (ListView) findViewById(R.id.playerListView);
		
		playerListView.setAdapter(NetworkState.getState().getMobileConnection().getPlayerAdapter());

	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		// In order to start the game we need to extract our assets to the
		// metaio SDK
		startGame.execute(0); // Starts the assetsExtracter class
	}

	/** Called when the user clicks the Lobby button (huvudmeny) */
	public void Lobby(View view) {
		Intent intentmenu = new Intent(this, NetworkActivity.class);
		startActivity(intentmenu);
	}

	/** Called when user minimize the window or clicks home button */
	@Override
	protected void onPause() {
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		super.onPause();
	}


	/**
	 * This task extracts all the assets to an external or internal location to
	 * make them accessible to Metaio SDK.
	 */
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {
		/** Extract all assets to make them accessible to Metaio SDK */
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				// Extract all assets except Menu. Overwrite existing files for
				// debug build only.
				final String[] ignoreList = { "Menu", "webkit", "sounds",
						"images", "webkitsec" };
				AssetsManager.extractAllAssets(getApplicationContext(), "",
						ignoreList, BuildConfig.DEBUG);
				// AssetsManager.extractAllAssets(getApplicationContext(),
				// BuildConfig.DEBUG);
			} catch (IOException e) {
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}

		/** when extraction is done, we load the game activity */
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent intent = new Intent(getApplicationContext(),
						GameActivity.class);
				startActivity(intent);
			}
			finish();
		}

	}
	
}

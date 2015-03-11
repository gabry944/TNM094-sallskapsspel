package com.google.sprint1;

import java.io.IOException;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

// Activity to handle the screen between mainmenu and the gamescreen, 
// where players should connect to each other before entering gamemode. 
public class NetworkActivity extends Activity {

	AssetsExtracter mTask;

	// function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
		mTask = new AssetsExtracter();
	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		mTask.execute(0); // Starts the assetsExtracter function
	}

	/** Called when the user clicks the mainMenu button (huvudmeny) */
	public void mainMenu(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}

	// This task extracts all the assets to an external or internal location
	// to make them accessible to Metaio SDK

	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				// Extract all assets except Menu. Overwrite existing files for
				// debug build only.
				// final String[] ignoreList = {"Menu", "webkit", "sounds",
				// "images", "webkitsec"};
				// AssetsManager.extractAllAssets(getApplicationContext(), "",
				// ignoreList, BuildConfig.DEBUG);
				AssetsManager.extractAllAssets(getApplicationContext(),
						BuildConfig.DEBUG);
			} catch (IOException e) {
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}

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

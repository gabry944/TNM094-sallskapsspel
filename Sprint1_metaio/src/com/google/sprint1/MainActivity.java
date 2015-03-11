package com.google.sprint1;

import java.io.IOException;

import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.sprint1.R;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

public class MainActivity extends Activity {

	AssetsExtracter mTask; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTask = new AssetsExtracter();
		
	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view)
	{
		mTask.execute(0); // Startar den assynkrona tasken assetsExtracter		
	}

	/** Called when the user clicks the settings button (spelinställningar) */
	public void startSettings(View view)
	{
		Intent intentSettings = new Intent(this, SettingsActivity.class);
		startActivity(intentSettings);
	}
	
	
	
	/**
	 * This task extracts all the assets to an external or internal location
	 * to make them accessible to Metaio SDK
	 */
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
	{		
		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try 
			{
				//Extract all assets except Menu. Overwrite existing files for debug build only.
				//final String[] ignoreList = {"Menu", "webkit", "sounds", "images", "webkitsec"};
				//AssetsManager.extractAllAssets(getApplicationContext(), "", ignoreList, BuildConfig.DEBUG);
				AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
			} 
			catch (IOException e) 
			{
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				Intent intent = new Intent(getApplicationContext(), GameActivity.class);
				startActivity(intent);
			}
			finish();
		}
		
	}
}

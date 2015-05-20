package com.google.sprint1;

import java.io.IOException;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.sprint1.R;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

/** 
 * MainActivity handle the main menu and Its buttons
 */
public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity";
	
	private ProgressDialog progressDialog;
	
	private AssetsExtracter startGame; // a variable used to start the
										// AssetExtraxter class
	
	private Button testGameBtn;
	
	/**Called at opening, drawing main layout */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_main);
		
		startGame = new AssetsExtracter();
		
		testGameBtn = (Button) findViewById(R.id.testgame);
		
		//Set progressDialog properties
		progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		progressDialog.setTitle("Loading resources...");
		progressDialog.setCancelable(false);
		
	/*	DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    double mWidthPixels = dm.widthPixels;
	    double mHeightPixels = dm.heightPixels;        
	    double x = Math.pow(mWidthPixels/dm.xdpi,2);
	    double y = Math.pow(mHeightPixels/dm.ydpi,2);
	    double screenInches = Math.sqrt(x+y);
	    Log.d(TAG,"Screen inches : " + screenInches);*/
				
	}

	
	/**Called when the user clicks the settings button ( Starta spel ) */


	public void startNetwork(View view) {
		Intent intentNetwork = new Intent(this, NetworkActivity.class);
		startActivity(intentNetwork);
	}
	
	/** Called when the user clicks the Test game button */
	public void testGame(View view){
		//Start progressDialog
		progressDialog.show();
		//Set button properties
		testGameBtn.setClickable(false);
		// In order to start the game we need to extract our assets to the
		// metaio SDK
		startGame.execute(0); // Starts the assetsExtracter class
	}

	/** Called when the user clicks the settings button (spelinställningar) */
	public void startSettings(View view) {
		
		SoundEffect.playSound(getBaseContext());
		
		Intent intentSettings = new Intent(this, SettingsActivity.class);
		startActivity(intentSettings);

	}
	
	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	public void onDestroy(){
		if(progressDialog.isShowing())
			progressDialog.cancel();
		
		super.onDestroy();
	}
	
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> 
	{
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

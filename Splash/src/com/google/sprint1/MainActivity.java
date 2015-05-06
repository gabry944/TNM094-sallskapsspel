package com.google.sprint1;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.sprint1.R;

/** 
 * MainActivity handle the main menu and Its buttons
 */
public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity";
	
	/**Called at opening, drawing main layout */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_main);
		
		
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

	/** Called when the user clicks the settings button (spelinställningar) */
	public void startSettings(View view) {
		Intent intentSettings = new Intent(this, SettingsActivity.class);
		startActivity(intentSettings);

	}
	
	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
}

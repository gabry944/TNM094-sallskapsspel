package com.google.sprint1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
/**
 * SettingsActivity handle the settings menu and some settings for the game 
 *
 */
public class SettingsActivity extends Activity {

	/**Called at opening, drawing settings layout */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_settings);
	}
	
	/**
	 *  Called when the user clicks the back arrow button 
	 */
	public void backArrow(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

}

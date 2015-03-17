package com.google.sprint1;

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

	/**Called at opening, drawing main layout */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
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

}

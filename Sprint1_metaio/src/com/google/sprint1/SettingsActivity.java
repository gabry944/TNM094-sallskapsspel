package com.google.sprint1;

import android.app.Activity;
import android.os.Bundle;
/**
 * SettingsActivity handle the settings menu and some settings for the game 
 *
 */
public class SettingsActivity extends Activity {

	/**Called at opening, drawing settings layout */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

}

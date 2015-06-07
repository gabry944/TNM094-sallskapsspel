package com.google.sprint1;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.sprint1.R;

/** 
 * MainActivity handle the main menu and Its buttons
 */
public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity";
	
	private ProgressDialog progressDialog;
	
	/**Called at opening, drawing main layout */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_main);
		
		//Set progressDialog properties
		progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		progressDialog.setTitle("Loading resources...");
		progressDialog.setCancelable(false);
				
	}

	
	/**Called when the user clicks the settings button ( Starta spel ) */


	public void startNetwork(View view) {
		Intent intentNetwork = new Intent(this, NetworkActivity.class);
		startActivity(intentNetwork);
	}
	
	/** Called when the user clicks the  view marker button */
	public void showMarker(View view){
		Intent intentNetwork = new Intent(this, MarkerActivity.class);
		startActivity(intentNetwork);
	}

	/** Called when the user clicks the settings button (spelinställningar) */
	public void startSettings(View view) {
		
		Intent intentSettings = new Intent(this, SettingsActivity.class);
		startActivity(intentSettings);

	}
	
	public void showInfo(View view)
	{			
		//Variables for toast
		Context context = getApplicationContext();
		CharSequence text = "Download marker from metaio's website! \n \n Game created by Daniel Holst, Michael Sjöström, Gabriella Rydenfors, Pontus Orraryd and Jens Jacobsson";
		int duration = 2 * Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		
		toast.show();
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
	
}

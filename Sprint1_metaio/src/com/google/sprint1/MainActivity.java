package com.google.sprint1;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.sprint1.R;
import com.google.sprint1.NetworkService.LocalBinder;

/** 
 * MainActivity handle the main menu and Its buttons
 */
public class MainActivity extends Activity {
	
	NetworkService mService;
	boolean mBound = false;
	
	public static final String TAG = "MainActivity";

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
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.d(TAG, "Vid bindService");

		// Bind to NetworkService
		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		Log.d(TAG, "1");

	}
	
	@Override
	protected void onStop() {
		super.onStop();

		// Unbind from service
		if (mBound) {
			unbindService(mServiceConnection);
			mBound = false;
		}
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			Log.d(TAG, "3");
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

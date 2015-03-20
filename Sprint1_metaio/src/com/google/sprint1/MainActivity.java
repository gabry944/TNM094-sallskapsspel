package com.google.sprint1;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.ComponentName;

import com.google.sprint1.R;

/** 
 * MainActivity handle the main menu and Its buttons
 */
public class MainActivity extends Activity
	implements
		ServiceConnection

	{
	
	private boolean mIsBound = false;
	
	private MusicService mServ;
	
	/**Called at opening, drawing main layout */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		Intent music = new Intent(this, MusicService.class);
		startService(music);
		
		doBindService();
		
		//mServ.start();
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
	
	// interface connection with the service activity
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			mServ = ((MusicService.ServiceBinder) binder).getService();
		}
		
		public void onServiceDisconnected(ComponentName name)
		{
			mServ = null;
		}
		
		// local methods used in connection/disconnection activity with service.
		
		public void doBindService()
		{
			// activity connects to the service.
	 		Intent intent = new Intent(this, MusicService.class);
			bindService(intent, this, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
		
		public void doUnbindService()
		{
			// disconnects the service activity.
			if(mIsBound)
			{
				unbindService(this);
	      		mIsBound = false;
			}
		}
		// when closing the current activity, the service will automatically shut down(disconnected).
		@Override
		public void onDestroy()
		{
			super.onDestroy();
			
			doUnbindService();
		}	
	

}

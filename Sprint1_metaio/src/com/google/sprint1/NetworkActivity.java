package com.google.sprint1;

import java.io.IOException;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Activity to handle the screen between mainmenu and the gamescreen
 * 
 * Where players register, discover and connect to each other 
 * before starting the game session.
 * 
 * 
 */

public class NetworkActivity extends Activity {

	private AssetsExtracter startGame; // a variable used to start the
										// AssetExtraxter class
	private Handler mUpdateHandler;
	NsdHelper mNsdHelper;
	MobileConnection mConnection;
	
	public static final String TAG = "NetworkActivity";
	

	/** Function to set up layout of activity */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_network);

		/* Start game */
		startGame = new AssetsExtracter();
		
		mUpdateHandler = new Handler();
		mConnection = new MobileConnection(mUpdateHandler);
		mNsdHelper = new NsdHelper(this);
		
		mNsdHelper.initializeNsd();
		
		//Register to network
		if(mConnection.getLocalPort() > -1) {
            mNsdHelper.registerService(mConnection.getLocalPort());
        } else {
            Log.d(TAG, "ServerSocket isn't bound.");
        }
		
		mNsdHelper.discoverServices();
		
		Log.d(TAG, "REGISTER DONE JENS!!");

	}
	
	/** Called when the user clicks the Discover button */
	
	public void clickDiscover(View v){
		mNsdHelper.discoverServices();
	}
	
	/** Called then the user clicks the Connect button*/
	
	public void clickConnect(View v) {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        if (service != null) {
            Log.d(TAG, "Connecting.");
            mConnection.connectToServer(service.getHost(),
                    service.getPort());
        } else {
            Log.d(TAG, "No service to connect to!");
        }
    }

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		// in order to start the game we need to extract our assets to the
		// metaio SDK
		startGame.execute(0); // Starts the assetsExtracter class
	}

	/** Called when the user clicks the mainMenu button (huvudmeny) */
	public void mainMenu(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}
	
	/** Unregister application when it closes down */
	
	 @Override
	    protected void onPause() {
	        if (mNsdHelper != null) {
	            mNsdHelper.stopDiscovery();
	        }
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        if (mNsdHelper != null) {
	            mNsdHelper.discoverServices();
	        }
	    
	    }
	    @Override
	    protected void onStop()
	    {
	    	super.onStop();
	    }
	    
	    @Override
	    protected void onDestroy() {

	    	Log.d(TAG, "tearing down");
	        mNsdHelper.tearDown();
	        mConnection.tearDown();
	        super.onDestroy();
	    }

	/**
	 * This task extracts all the assets to an external or internal location to
	 * make them accessible to Metaio SDK.
	 */

	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {
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
				// AssetsManager.extractAllAssets(getApplicationContext(),
				// BuildConfig.DEBUG);
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

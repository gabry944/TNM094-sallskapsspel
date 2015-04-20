package com.google.sprint1;

import java.io.IOException;
import java.util.ArrayList;

import com.google.sprint1.NetworkService.LocalBinder;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity to handle the screen between mainmenu and the gamescreen
 * 
 * where players should connect to each other before entering gamemode.
 * 
 * 
 */

public class NetworkActivity extends Activity {

	private AssetsExtracter startGame; // a variable used to start the
										// AssetExtraxter class
	Handler mNSDHandler;
	
	// Variables for Network Service handling
	public NetworkService mService;
	public NsdHelper mNsdHelper;
	private boolean mBound = false;

	public static final String TAG = "NetworkActivity";

	ArrayAdapter<NsdServiceInfo> listAdapter;
	ArrayAdapter<Player> playerListAdapter;
	ArrayList<NsdServiceInfo> arraylist;
	
	ListView serviceListView;
	
	// Function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_network);
		
		// Bind to NetworkService. The service will not destroy
		// until there is no activity bound to it
		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		serviceListView = (ListView) findViewById(R.id.serviceListView);

		/* Start game */
		startGame = new AssetsExtracter();
		
		arraylist = new ArrayList<NsdServiceInfo>();

		listAdapter = new ArrayAdapter<NsdServiceInfo>(this,
				android.R.layout.simple_list_item_1,
				arraylist);

		mNSDHandler = new Handler() {
			@Override
			// Called whenever a message is sent to the handler.
			// Currently assumes that the message contains a NsdServiceInfo
			// object.
			public void handleMessage(Message msg) {
				NsdServiceInfo service;
				// If message is of type 1, meaning "delete list".
				// TODO: Should probably be an enum
				if (msg.what == 1) {
					listAdapter.clear();
				}
				// If key is "found", add to the adapter
				else if ((service = (NsdServiceInfo) msg.getData().get("found")) != null) {
					//listAdapter.add(service);
					arraylist.add(service);
				}
				
				// If key is "lost", remove from adapter
				else if ((service = (NsdServiceInfo) msg.getData().get("lost")) != null) {
					
					for(int i = 0; i < arraylist.size(); i++){
						if(arraylist.get(i).getServiceName().equals(service.getServiceName()))
							arraylist.remove(i);
					}
					
				}
				
				// Notify adapter that the list is updated.
				listAdapter.notifyDataSetChanged();

			}
		};

		 
		
		serviceListView.setAdapter(listAdapter);
		serviceListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					// When clicking on a service, an AlertDialog window pops up
					// to allow you to connect to said service.
					public void onItemClick(AdapterView parent, View view,
							final int pos, long id) {

						// Instantiate an AlertDialog.Builder with its
						// constructor
						AlertDialog.Builder builder = new AlertDialog.Builder(
								NetworkActivity.this);

						builder.setMessage(
								"Connect to "
										+ listAdapter.getItem(pos)
												.getServiceName() + "?")
								.setTitle("Connect")
								.setPositiveButton(R.string.BTN_OK,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												NsdServiceInfo service = listAdapter
														.getItem(pos);
												service = mNsdHelper
														.resolveService(service);
												if (service != null) {
													Log.d(TAG,
															"Connecting to: "
																	+ service
																			.getServiceName());
													mService.mConnection.connectToPeer(
															service.getHost(),
															service.getPort());
												} else {
													Log.d(TAG,
															"No service to connect to!");
												}

											}
										})
								.setNegativeButton(R.string.BTN_CANCEL,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
											}
										});
						// 3. Get the AlertDialog from create()
						AlertDialog dialog = builder.create();
						// Show the AlertDialog
						dialog.show();
					}

				});
		
		mNsdHelper = new NsdHelper(this, mNSDHandler);
		mNsdHelper.initializeNsd();

	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		// In order to start the game we need to extract our assets to the
		// metaio SDK
		startGame.execute(0); // Starts the assetsExtracter class
	}

	/** Called when the user clicks the mainMenu button (huvudmeny) */
	public void mainMenu(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}

	/** Called when the user clicks the Send Data button */
	public void sendData(View view) {
		TestClass test = new TestClass(5, "hej");
		//mService.mConnection.sendData(test);
	}

	/** Called when user minimize the window or clicks home button */
	@Override
	protected void onPause() {
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		// If mNsdHelper is other than null it will be teared down.
		// This is done to unregister from the network and stop the
		// service discovery.
		

		if (mNsdHelper != null) {
			mNsdHelper.stopDiscovery();
			mNsdHelper.unregisterService();
			mNsdHelper = null;
        }

		
		super.onPause();
	}

	/**
	 * Called when when a new instance of NetworkActivity is started, for
	 * example when starting the game for the first time or when entering from
	 * another activity
	 */
	@Override
	protected void onStart() {
		super.onStart();

		

	}

	/**
	 * Called when after onStart() when a new instance of NetworkActivity is
	 * started and when ever the user enters the activity from a paused state
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mNsdHelper == null){
			mNsdHelper = new NsdHelper(this, mNSDHandler);
			mNsdHelper.initializeNsd();
		}
		
		if (mNsdHelper != null) {
			mNsdHelper.discoverServices();
			mNsdHelper.registerService(MobileConnection.SERVER_PORT);
            
        }

	}

	/**
	 * Called when user exits the Activity or pausing and then destroy the app
	 * by brute force
	 */
	protected void onDestroy() {

		// Check if mNsdHelper is not null(will throw NullPointerException
		// otherwise). Unregister from network and stops the discovery.

		if (mNsdHelper != null) {
			mNsdHelper.stopDiscovery();
			mNsdHelper.unregisterService();
	

        }
		
//		if(mService != null)
//			unbindService(mServiceConnection);
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

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
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

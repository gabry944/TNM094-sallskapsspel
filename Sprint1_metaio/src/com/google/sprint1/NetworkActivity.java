package com.google.sprint1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.sprint1.NetworkService.LocalBinder;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

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

//	private Handler mUpdateHandler;
	private Handler mNSDHandler;
	//NsdHelper mNsdHelper;
	//MobileConnection mConnection;

	// Variables for Service handling
	NetworkService mService;
	boolean mBound = false;

	ArrayAdapter<NsdServiceInfo> listAdapter;

	public static final String TAG = "NetworkActivity";

	// function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_network);

		/* Start game */
		startGame = new AssetsExtracter();

//		mUpdateHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//
//			}
//		};

		Log.d(TAG, "Precis vid init av mNSDHandler");
		
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
					listAdapter.add(service);
				}
				// If key is "lost", remove from adapter
				else if ((service = (NsdServiceInfo) msg.getData().get("lost")) != null) {
					listAdapter.remove(service);
				}
				// Notify adapter that the list is updated.
				listAdapter.notifyDataSetChanged();

			}
		};
		
		Log.d(TAG, "Precis efter init av mNSDHandler");


		//mConnection = new MobileConnection(mUpdateHandler);
		//mNsdHelper = new NsdHelper(this, mNSDHandler);
		
		//mNsdHelper.initializeNsd();

		ListView listView = (ListView) findViewById(R.id.serviceView);

		listAdapter = new ArrayAdapter<NsdServiceInfo>(this,
				android.R.layout.simple_list_item_1,
				new ArrayList<NsdServiceInfo>());

		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			// When clicking on a service, an AlertDialog window pops up
			// to allow you to connect to said service.
			public void onItemClick(AdapterView parent, View view,
					final int pos, long id) {

				// Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NetworkActivity.this);

				// set vari
				builder.setMessage(
						"Connect to "
								+ listAdapter.getItem(pos).getServiceName()
								+ "?")
						.setTitle("Connect")
						.setPositiveButton(R.string.BTN_OK,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										NsdServiceInfo service = listAdapter
												.getItem(pos);
										service = mService.mNsdHelper
												.resolveService(service);
										if (service != null) {
											Log.d(TAG, "Connecting to: "
													+ service.getServiceName());
											mService.mConnection.connectToServer(
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
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								});
				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				// Show the AlertDialog
				dialog.show();
			}

		});

	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		// in order to start the game we need to extract our assets to the
		// metaio SDK
		// startService(new Intent(this, NetworkService.class));
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
		mService.mConnection.sendData(test);

	}

	@Override
	protected void onPause() {

//		if (mService.mNsdHelper != null) {
//
//			Log.d(TAG, "Pausad");
//			mService.mNsdHelper.tearDown();
//			mService.mNsdHelper = null;
//		}

		super.onPause();
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
	protected void onResume() {
		super.onResume();

//		mNsdHelper = new NsdHelper(this, mNSDHandler);
//		mNsdHelper.initializeNsd();
//		if (mNsdHelper != null) {
//			Log.d(TAG, "Resumed");
//
//			mNsdHelper.registerService(mConnection.getLocalPort());
//			mNsdHelper.discoverServices();
//
//		}
		//mService.mNSDHandler = this.mNSDHandler;
		Log.d(TAG, "2");


	}

	protected void onDestroy() {

//		Log.d(TAG, "Destroyed");
//		if (mNsdHelper != null) {
//			mNsdHelper.tearDown();
//			mNsdHelper = null;
//		}
//		mConnection.tearDown();
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
			
			Log.d(TAG, "3");

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

}

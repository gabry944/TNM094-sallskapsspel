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

	Handler mNSDHandler;
	
	// Variables for Network Service handling
	public NetworkService mService;
	public NsdHelper mNsdHelper;
	private boolean mBound = false;
	
	//Variable that indicates if user is host
	private boolean isHost = false;

	public static final String TAG = "NetworkActivity";

	private ArrayAdapter<String> listAdapter;
	private ArrayList<NsdServiceInfo> serviceList;
	private ArrayList<String> serviceNameList;
	
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
		
		//ArrayList to store all services
		serviceList = new ArrayList<NsdServiceInfo>();
		//ArrayList to store all names of services
		serviceNameList = new ArrayList<String>();

		//The listAdapter only holds the name of the services and not the total
		//NsdServiceInfo items.
		listAdapter = new ArrayAdapter<String>(this,
				R.layout.custom_list_for_services,
				serviceNameList);

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
				// If key is "found", add NsdServiceInfo to serviceList and service name to serviceNameList.
				else if ((service = (NsdServiceInfo) msg.getData().get("found")) != null) {
					
					serviceList.add(service);
					serviceNameList.add(service.getServiceName());
			
				}
				
				// If key is "lost", remove from serviceList and serviceNameList
				else if ((service = (NsdServiceInfo) msg.getData().get("lost")) != null) {
					
					for(int i = 0; i < serviceList.size(); i++){
						if(serviceList.get(i).getServiceName().equals(service.getServiceName()))
							serviceList.remove(i);
					}
					for(int i = 0; i < serviceNameList.size(); i++){
						if(serviceNameList.get(i).equals(service.getServiceName()))
							serviceNameList.remove(i);
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
										+ listAdapter.getItem(pos) + "?")
								.setTitle("Connect")
								.setPositiveButton(R.string.BTN_OK,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												NsdServiceInfo service = null;
												
												//If a service name is clicked and the OK button is pressed,
												//a loop will compare all names in the listAdapter with the names from 
												//the serviceList. If they are the same, the correct service to connect
												//to is found
												for(int i = 0; i < serviceList.size(); i++){
													if(listAdapter.getItem(pos).equals(serviceList.get(i).getServiceName())){
														service = serviceList.get(i);
													}
												}
												service = mNsdHelper
														.resolveService(service);
												if (service != null) {
													Log.d(TAG,
															"Connecting to: "
																	+ service
																			.getServiceName());
													mService.mConnection.connectToPeer(
															service.getHost());
													//TODO : Go to Lobby
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
		Intent intentlobby = new Intent(this, LobbyActivity.class);
		startActivity(intentlobby);	
	}

	/** Called when the user clicks the mainMenu button (huvudmeny) */
	public void mainMenu(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}
	
	/** Called when the user clicks the Host Game button */
	public void hostGame(View view){
		
		//If user is not already host and the registration state is false,
		//register/host a game
		if(!isHost && !mNsdHelper.getRegistrationState())
			mNsdHelper.registerService(MobileConnection.SERVER_PORT);
		
		//if(mNsdHelper.getRegistrationState())
			isHost = true;
		//TODO : Go to Lobby, stay registered!
	}

	/** Called when user minimize the window or clicks home button */
	@Override
	protected void onPause() {
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		//Stops service discovery if mNsdHelper is still initialized.
		if (mNsdHelper != null) {
			mNsdHelper.stopDiscovery();
		}
		
		//Unregister if the registration state is true.
		if(mNsdHelper.getRegistrationState()){
			mNsdHelper.unregisterService();
        }
		
		mNsdHelper = null;

		super.onPause();
	}

	/**
	 * Called when after onStart() when a new instance of NetworkActivity is
	 * started and when ever the user enters the activity from a paused state
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		//If mNsdHelper is null(which happens if activity return after call to 
		//onPause() it will create a new NsdHelper and initialize it.
		if(mNsdHelper == null){
			mNsdHelper = new NsdHelper(this, mNSDHandler);
			mNsdHelper.initializeNsd();
		}
		
		//Starts service discovery when when starting activity for the first
		//or when returing from a paused state.
		if (mNsdHelper != null) {
			mNsdHelper.discoverServices();            
        }
		
		//Checks if the user is a host and register a service accordingly.
		if(isHost)
			mNsdHelper.registerService(MobileConnection.SERVER_PORT);

	}

	/**
	 * Called when user exits the Activity or pausing and then destroy the app
	 * by brute force
	 */
	protected void onDestroy() {

		// Check if mNsdHelper is not null(will throw NullPointerException
		// otherwise) and stops service discovery.
		if (mNsdHelper != null) {
			mNsdHelper.stopDiscovery();
        }
		
		//Checks state of mNsdHelper, isHost and registration state to prevent 
		//crash.
		if(mNsdHelper != null && isHost && mNsdHelper.getRegistrationState()){
			mNsdHelper.unregisterService();
		}
		
		//Unbinds from network service.
		if(mBound){
			unbindService(mServiceConnection);
			mBound = false;
		}
		
		super.onDestroy();
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

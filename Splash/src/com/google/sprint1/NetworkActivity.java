package com.google.sprint1;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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

	Handler mNSDHandler;
		
	//Variable that indicates if user is host
	private boolean isHost = false;
	
	public static final String TAG = "NetworkActivity";
	
	private ListView serviceListView;
	
	// Function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_network);
		
		NetworkState.getState().init(this);

		// TODO  E/AndroidRuntime(17132): java.lang.RuntimeException: Unable to start activity ComponentInfo{com.google.sprint1/com.google.sprint1.NetworkActivity}: java.lang.ClassCastException: android.widget.TextView cannot be cast to android.widget.ListView
		serviceListView = (ListView) findViewById(R.id.serviceListView);

		serviceListView.setAdapter(NetworkState.getState().getAdapter());
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
										+ NetworkState.getState().getAdapter().getItem(pos) + "?")
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
												for(int i = 0; i < NetworkState.getState().getServiceList().size(); i++){
													if(NetworkState.getState().getAdapter().getItem(pos).equals(NetworkState.getState().getServiceList().get(i).getServiceName())){
														service = NetworkState.getState().getServiceList().get(i);
													}
												}
												service = NetworkState.getState().getNsdHelper()
														.resolveService(service);
												if (service != null) {
													Log.d(TAG,
															"Connecting to: "
																	+ service
																			.getServiceName());
													NetworkState.getState().getMobileConnection().connectToPeer(
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
		
	}

	/** 
	 * Called when the user clicks the start Game button (starta spel)
	 */
	public void startGame(View view) {
		// In order to start the game we need to extract our assets to the
		// metaio SDK
		Intent intentlobby = new Intent(this, LobbyActivity.class);
		startActivity(intentlobby);	
	}

	/**
	 *  Called when the user clicks the back arrow button 
	 */
	public void backArrow(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}
	
	/** 
	 * Called when the user clicks the Host Game button 
	 */
	public void hostGame(View view){
		Context context = getApplicationContext();
		CharSequence text = "Game created successfully!";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		
		//If user is not already host and the registration state is false,
		//register/host a game
		if(!isHost && !NetworkState.getState().getNsdHelper().getRegistrationState())
			NetworkState.getState().getNsdHelper().registerService(MobileConnection.SERVER_PORT);
		
			isHost = true;
			
			toast.show();
			
			Intent intentlobby = new Intent(this, LobbyActivity.class);
			startActivity(intentlobby);	

	}

	/** 
	 * Called when user minimize the window or clicks home button 
	 */
	@Override
	protected void onPause() {	
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		//Stops service discovery if mNsdHelper is still initialized.
		if (NetworkState.getState().getNsdHelper() != null) {
			NetworkState.getState().getNsdHelper().stopDiscovery();
		}
		
		//Unregister if the registration state is true.
		if(NetworkState.getState().getNsdHelper().getRegistrationState()){
			NetworkState.getState().getNsdHelper().unregisterService();
        }
		
		NetworkState.getState().mNsdHelper = null;

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
			if(NetworkState.getState().getNsdHelper() == null)
				NetworkState.getState().initNsdHelper();
			
			//Starts service discovery when when starting activity for the first
			//or when returing from a paused state.
			if (NetworkState.getState().getNsdHelper() != null) 
				NetworkState.getState().getNsdHelper().discoverServices(); 
			
			//Checks if the user is a host and register a service accordingly.
			if(isHost)
				NetworkState.getState().getNsdHelper().registerService(MobileConnection.SERVER_PORT);


	}

	/**
	 * Called when user exits the Activity or pausing and then destroy the app
	 * by brute force
	 */
	protected void onDestroy() {

		// Check if mNsdHelper is not null(will throw NullPointerException
		// otherwise) and stops service discovery.
		if (NetworkState.getState().getNsdHelper() != null) {
			NetworkState.getState().getNsdHelper().stopDiscovery();
        }
		
		//Checks state of mNsdHelper, isHost and registration state to prevent 
		//crash.
		if(NetworkState.getState().getNsdHelper() != null && isHost 
				&& NetworkState.getState().getNsdHelper().getRegistrationState()){
			NetworkState.getState().getNsdHelper().unregisterService();
		}
		
		super.onDestroy();
	}

}

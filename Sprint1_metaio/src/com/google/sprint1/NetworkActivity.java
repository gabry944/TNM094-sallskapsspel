package com.google.sprint1;

import java.io.IOException;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/** Activity to handle the screen between mainmenu and the gamescreen
 *  
* where players should connect to each other before entering gamemode.
* 
*  
*  */

public class NetworkActivity extends Activity implements PeerListListener {

	AssetsExtracter mTask;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;

	// function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
		mTask = new AssetsExtracter();
		
		/* Wifi P2P Initialization */
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		mTask.execute(0); // Starts the assetsExtracter function
	}

	/** Called when the user clicks the mainMenu button (huvudmeny) */
	public void mainMenu(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}

	/** This task extracts all the assets to an external or internal location
	* to make them accessible to Metaio SDK. */

	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				// Extract all assets except Menu. Overwrite existing files for
				// debug build only.
				// final String[] ignoreList = {"Menu", "webkit", "sounds",
				// "images", "webkitsec"};
				// AssetsManager.extractAllAssets(getApplicationContext(), "",
				// ignoreList, BuildConfig.DEBUG);
				AssetsManager.extractAllAssets(getApplicationContext(),
						BuildConfig.DEBUG);
			} catch (IOException e) {
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}

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
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
	}

	/* onPeersAvailble is called when the BroadcastReciever finds peers */
	public void onPeersAvailable(WifiP2pDeviceList peers) {

		DialogFragment alert = ChoosePeerDialogFragment.newInstance(peers);
		alert.show(getFragmentManager(), "Peers");

	}

	public void checkForPeers(View view) {
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Toast toast = Toast.makeText(NetworkActivity.this,
						"discoverPeers Success", Toast.LENGTH_LONG);
				toast.show();
				Log.i("WIFI", "discoverPeers success");
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast toast = Toast.makeText(NetworkActivity.this,
						"discoverPeers Failed", Toast.LENGTH_LONG);
				toast.show();
				Log.i("WIFI", "discoverPeers failed");
			}
		});

	}

}

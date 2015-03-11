package com.google.sprint1;

import java.io.IOException;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
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

import com.google.sprint1.R;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;


public class MainActivity extends Activity implements PeerListListener {
	// test

	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

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

	public void startNetwork(View view)
	{
		Intent intentNetwork = new Intent(this, NetworkActivity.class);
		startActivity(intentNetwork);

	}

	/** Called when the user clicks the settings button (spelinställningar) */
	public void startSettings(View view) {
		Intent intentSettings = new Intent(this, SettingsActivity.class);
		startActivity(intentSettings);
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

		
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent intent = new Intent(getApplicationContext(),
						GameActivity.class);
				startActivity(intent);
			}
			finish();
		}

	

	/* onPeersAvailble is called when the BroadcastReciever finds peers */
	public void onPeersAvailable(WifiP2pDeviceList peers) {

		// Log.i("WIFI", peers.toString());

		DialogFragment alert = ChoosePeerDialogFragment.newInstance(peers);
		alert.show(getFragmentManager(), "Peers");

	}

	public void checkForPeers(View view) {
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Toast toast = Toast.makeText(MainActivity.this,
						"discoverPeers Success", Toast.LENGTH_LONG);
				toast.show();
				Log.i("WIFI", "discoverPeers success");
			}

			@Override
			public void onFailure(int reasonCode) {
				Toast toast = Toast.makeText(MainActivity.this,
						"discoverPeers Failed", Toast.LENGTH_LONG);
				toast.show();
				Log.i("WIFI", "discoverPeers failed");
			}
			});

		}
		
	}
	


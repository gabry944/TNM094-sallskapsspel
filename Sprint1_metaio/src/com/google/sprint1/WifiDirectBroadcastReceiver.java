package com.google.sprint1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private NetworkActivity mActivity;
	private PeerListListener mPeerListListener;
	
	
	public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, NetworkActivity networkActivity){
		this.mManager = manager;
		this.mChannel = channel;
		this.mActivity = networkActivity;
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	            // Wifi P2P is enabled
	        } else {
	            // Wi-Fi P2P is not enabled
	        }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	if (mManager != null)
        	{
        		mManager.requestPeers(mChannel, (PeerListListener) mActivity);
        	}
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
	}
}
		



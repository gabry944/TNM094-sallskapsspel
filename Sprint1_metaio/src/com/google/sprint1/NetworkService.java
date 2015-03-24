package com.google.sprint1;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class NetworkService extends Service {
	public static final String TAG = "NetworkService";

	private Handler mUpdateHandler;
    Handler mNSDHandler = null;
	NsdHelper mNsdHelper;
	MobileConnection mConnection;

	// ArrayAdapter<NsdServiceInfo> listAdapter;

	public int k;

	private final IBinder mBinder = new LocalBinder();
	private final Random mGenerator = new Random();


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My service created", Toast.LENGTH_LONG).show();

		mUpdateHandler = new Handler();
		mConnection = new MobileConnection(mUpdateHandler);
		

		Log.d(TAG, "Vid mNsdHelper");
		mNsdHelper = new NsdHelper(this, mNSDHandler);
		
		mUpdateHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

			}
		};

		// mNSDHandler = new Handler() {
		// @Override
		// // Called whenever a message is sent to the handler.
		// // Currently assumes that the message contains a NsdServiceInfo
		// // object.
		// public void handleMessage(Message msg) {
		// NsdServiceInfo service;
		// // If message is of type 1, meaning "delete list".
		// // TODO: Should probably be an enum
		// if (msg.what == 1) {
		// listAdapter.clear();
		// }
		// // If key is "found", add to the adapter
		// else if ((service = (NsdServiceInfo) msg.getData().get("found")) !=
		// null) {
		// listAdapter.add(service);
		// }
		// // If key is "lost", remove from adapter
		// else if ((service = (NsdServiceInfo) msg.getData().get("lost")) !=
		// null) {
		// listAdapter.remove(service);
		// }
		// // Notify adapter that the list is updated.
		// listAdapter.notifyDataSetChanged();
		//
		// }
		// };

		mNsdHelper.initializeNsd();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My service destroyed", Toast.LENGTH_LONG).show();
		Log.d(TAG, "DestroyedInService");

//		if (mNsdHelper != null) {
//			mNsdHelper.tearDown();
//			mNsdHelper = null;
//		}
//		mConnection.tearDown();

		super.onDestroy();
	}

	public void onStartCommand() {
		Toast.makeText(this, "My service started", Toast.LENGTH_LONG).show();

		// if (mNsdHelper != null) {
		/*
		 * Log.d(TAG, "Resumed");
		 * 
		 * mNsdHelper.registerService(mConnection.getLocalPort());
		 * mNsdHelper.discoverServices();
		 */

		// }

	}

	public class LocalBinder extends Binder {
		NetworkService getService() {
			return NetworkService.this;
		}
	}

	public int getRandomNumber() {
		return mGenerator.nextInt(100);
	}

}

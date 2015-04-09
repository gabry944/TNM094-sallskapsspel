package com.google.sprint1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class NetworkService extends Service {
	public static final String TAG = "NetworkService";

	private Handler mUpdateHandler;
	
	MobileConnection mConnection;

	private final IBinder mBinder = new LocalBinder();
	
	/**Called when a class tries to bind to the service*/
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public void onCreate() {

		// Creates new handler and a MobileConnection
		mUpdateHandler = new Handler();
		mConnection = new MobileConnection(mUpdateHandler);

	}
	
	/**Called when the user exits GameActivity and tear down the MobileConnection*/
	@Override
	public void onDestroy() {

		// Tearing MobileConnection down when user exits app
		mConnection.tearDown();

		super.onDestroy();
	}

	public class LocalBinder extends Binder {
		NetworkService getService() {
			return NetworkService.this;
		}
	}

}

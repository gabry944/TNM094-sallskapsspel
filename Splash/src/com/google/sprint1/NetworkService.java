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
	NsdHelper mNsdHelper;
	MobileConnection mConnection;

	private final IBinder mBinder = new LocalBinder();
	private final Random mGenerator = new Random();
	
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
	
	/**Initializing the NsdHelper to be active as long as the service is active*/
	public void initNsdHelper(Handler handler) {
		// Initialize new mNsdHandler with "handler"
		mNsdHelper = new NsdHelper(this, handler);
		mNsdHelper.initializeNsd();

	}

}

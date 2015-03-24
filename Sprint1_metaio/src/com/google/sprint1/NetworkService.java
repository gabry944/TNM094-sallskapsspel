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
		
//		mUpdateHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//
//			}
//		};
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My service destroyed", Toast.LENGTH_LONG).show();

		mConnection.tearDown();

		super.onDestroy();
	}


	public class LocalBinder extends Binder {
		NetworkService getService() {
			return NetworkService.this;
		}
	}

	public int getRandomNumber() {
		return mGenerator.nextInt(100);
	}
	
	public void initNsdHelper(Handler handler){
		Log.d(TAG, "Vid mNsdHelper");
		mNsdHelper = new NsdHelper(this, handler);
		
		mNsdHelper.initializeNsd();

	}

}

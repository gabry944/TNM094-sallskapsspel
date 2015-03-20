package com.google.sprint1;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NetworkService extends Service {
	
	/*private Handler mUpdateHandler;
	NsdHelper mNsdHelper;
	MobileConnection mConnection;*/
	
	public int k;
	
	private final IBinder mBinder = new LocalBinder();
	private final Random mGenerator = new Random();
	
	public static final String TAG = "NetworkService";
	
	@Override
	public IBinder onBind(Intent inten) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My service created", Toast.LENGTH_LONG).show();
		
		/*mUpdateHandler = new Handler();
		mConnection = new MobileConnection(mUpdateHandler);
		mNsdHelper = new NsdHelper(this);

		mNsdHelper.initializeNsd();*/
		
		k=0;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My service destroyed", Toast.LENGTH_LONG).show();
		Log.d(TAG, "DestroyedInService");
		/*if (mNsdHelper != null) {
			mNsdHelper.tearDown();
			mNsdHelper = null;
		}
		mConnection.tearDown();*/
		super.onDestroy();
	}
	
	public void onStartCommand(){
		Toast.makeText(this, "My service started", Toast.LENGTH_LONG).show();
		
		//if (mNsdHelper != null) {
			/*Log.d(TAG, "Resumed");

			mNsdHelper.registerService(mConnection.getLocalPort());
			mNsdHelper.discoverServices();*/

		//}
		
	}
	
	public class LocalBinder extends Binder {
		NetworkService getService() {
			return NetworkService.this;
		}
	}
	
	public int getRandomNumber(){
		return mGenerator.nextInt(100);
	}
	

}

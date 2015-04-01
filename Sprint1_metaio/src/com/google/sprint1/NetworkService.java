package com.google.sprint1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class NetworkService extends Service {

	@Override
	public IBinder onBind(Intent inten) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "My service created", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My service destroyed", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startid){
		Toast.makeText(this, "My service started", Toast.LENGTH_LONG).show();
	}

}

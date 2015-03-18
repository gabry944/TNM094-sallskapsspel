package com.google.sprint1;

import java.net.Socket;

import android.os.Handler;

public class MobileConnection {
	
	private Handler mUpdateHandler;

    private static final String TAG = "ChatConnection";

    private Socket mSocket;
    private int mPort = -1;
	
	public MobileConnection(Handler handler){
		mUpdateHandler = handler;
	}
	
	public int getLocalPort(){
		return mPort;
	}
	
	public void setLocalPort(int port) {
        mPort = port;
    }
}

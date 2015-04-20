package com.google.sprint1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class Peer {
	
	private static final String TAG = "Peer";
	
	private Socket mSocket;
	private InetAddress mIP;
	private OutputStream mOutStream;
	private InputStream mInStream;
	
	public Peer(Socket socket)
	{
		try {
			mSocket = socket;
			mSocket.setTcpNoDelay(true);
			mIP = socket.getInetAddress();
			mOutStream = socket.getOutputStream();
			mInStream = socket.getInputStream();
			
		} catch (IOException e) {
			Log.d(TAG, "Error initializing Peer");
			e.printStackTrace();
		}
	}
	
	public Socket getSocket()
	{
		return mSocket;
	}
	
	public OutputStream getOutputStream(){
		return mOutStream;
	}
	
	public InputStream getInputStream(){
		return mInStream;
	}

	public InetAddress getAdress(){
		return mIP;
	}
}

package com.google.sprint1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class Peer {
	
	private static final String TAG = "Peer";
	
	private Socket mSocket;
	private InetAddress mIP;
	private ObjectOutputStream mOutStream;
	private ObjectInputStream mInStream;
	
	public Peer(Socket socket)
	{
		try {
			mSocket = socket;
			mSocket.setTcpNoDelay(true);
			mIP = socket.getInetAddress();
			mOutStream = new ObjectOutputStream(socket.getOutputStream());
			mInStream = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			Log.d(TAG, "Error initializing Peer");
			e.printStackTrace();
		}
	}
	
	public Socket getSocket()
	{
		return mSocket;
	}
	
	public ObjectOutputStream getOutputStream(){
		return mOutStream;
	}
	
	public ObjectInputStream getInputStream(){
		return mInStream;
	}

	public InetAddress getAdress(){
		return mIP;
	}
}

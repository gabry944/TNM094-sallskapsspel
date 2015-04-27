package com.google.sprint1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

/**
 * Peer is a class containing socket information, IP, and
 * access to the input and outputstream of a Peer. 
 * @author Pontus
 *
 */
public class Peer {
	
	private static final String TAG = "Peer";

	private Socket mSocket;
	private InetAddress mIP;
	private OutputStream mOutStream;
	private InputStream mInStream;
	
	public Peer(Socket socket) throws SocketException
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
	
	public void closeSocket() throws SocketException
	{
		try {
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

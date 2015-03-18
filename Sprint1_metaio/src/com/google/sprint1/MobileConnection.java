package com.google.sprint1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class MobileConnection {

	private Handler mUpdateHandler;
	private MobileServer mMobileServer;

	private static final String TAG = "MobileConnection";

	private Socket mSocket;
	private int mPort = -1;

	public MobileConnection(Handler handler) {
		mUpdateHandler = handler;
		mMobileServer = new MobileServer(handler);
	}
	
	public void connectToServer(InetAddress address, int port) {
        //mChatClient = new ChatClient(address, port);
    }
	
	public void tearDown() {
        mMobileServer.tearDown();
        try {
            getSocket().close();
        } catch (IOException ioe) {
            Log.e("TAG", "Error when closing server socket.");
        }
    }
	
	public int getLocalPort() {
		return mPort;
	}

	public void setLocalPort(int port) {
		mPort = port;
	}

	private synchronized void setSocket(Socket socket) {
		Log.d(TAG, "setSocket being called.");
		if (socket == null) {
			Log.d(TAG, "Setting a null socket.");
		}
		if (mSocket != null) {
			if (mSocket.isConnected()) {
				try {
					mSocket.close();
				} catch (IOException e) {
					// TODO(alexlucas): Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mSocket = socket;
	}

	private Socket getSocket() {
		return mSocket;
	}

	
	private class MobileServer {
		ServerSocket mServerSocket = null;
		Thread mThread = null;

		public MobileServer(Handler handler) {
			mThread = new Thread(new ServerThread());
			mThread.start();
		}

		public void tearDown() {
			mThread.interrupt();
			try {
				mServerSocket.close();
				
			} catch (IOException ioe) {
				Log.e(TAG, "Error when closing server socket.");
			}
		}

		
		class ServerThread implements Runnable {

			@Override
			public void run() {

				try {
					// Since discovery will happen via Nsd, we don't need to
					// care which port is
					// used. Just grab an available one and advertise it via
					// Nsd.
					mServerSocket = new ServerSocket(0);
					setLocalPort(mServerSocket.getLocalPort());

					while (!Thread.currentThread().isInterrupted()) {
						Log.d(TAG, "ServerSocket Created, awaiting connection");
						setSocket(mServerSocket.accept());
						Log.d(TAG, "Connected.");
						/*
						 * if (mChatClient == null) { int port =
						 * mSocket.getPort(); InetAddress address =
						 * mSocket.getInetAddress(); connectToServer(address,
						 * port); }
						 */
					}
				} catch (IOException e) {
					Log.e(TAG, "Error creating ServerSocket: ", e);
					e.printStackTrace();
				}
			}
		}
	}

}

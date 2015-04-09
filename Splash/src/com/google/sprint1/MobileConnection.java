package com.google.sprint1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.metaio.sdk.jni.Vector3d;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MobileConnection {

	private Handler mUpdateHandler;
	private MobileServer mMobileServer;
	private GameClient mGameClient;

	private static final String TAG = "MobileConnection";

	private Socket mSocket;
	private int mPort = -1;

	public MobileConnection(Handler handler) {
		mUpdateHandler = handler;
		mMobileServer = new MobileServer(handler);
	}

	public void connectToServer(InetAddress address, int port) {
		mGameClient = new GameClient(address, port);
	}

	public void tearDown() {
		mMobileServer.tearDown();
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
					e.printStackTrace();
				}
			}
		}
		mSocket = socket;
	}

	public synchronized void sendData(Object obj)
	{
		if(!(mGameClient == null))
			mGameClient.sendData(obj);
		else
			Log.d(TAG, "Not connected to any server. Cannot send message");
	}
	
	public synchronized void updateData(DataPackage data, boolean local) {
		Vector3d vel = new Vector3d(data.velocityX,data.velocityY,data.velocityZ);
		Vector3d pos = new Vector3d(data.positionX,data.positionY,data.positionZ);
		
		GameState.getState().exsisting_paint_balls.get(data.id).fire(vel, pos);
		//TODO: Send data back to activity using mUpdateHandler.
		
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
					mServerSocket = new ServerSocket(0);
					setLocalPort(mServerSocket.getLocalPort());

					while (!Thread.currentThread().isInterrupted()) {
						Log.d(TAG, "ServerSocket Created, awaiting connection");
						setSocket(mServerSocket.accept());
						Log.d(TAG, "Connected.");

						if (mGameClient == null) {
							int port = mSocket.getPort();
							InetAddress address = mSocket.getInetAddress();
							connectToServer(address, port);
						}
					}
				} catch (IOException e) {
					Log.e(TAG, "Error creating ServerSocket: ", e);
					e.printStackTrace();
				}
			}
		}
	}

	private class GameClient {
		private InetAddress mAddress;
		private int PORT;
		private final String CLIENT_TAG = "GameClient";

		private Thread mSendThread;
		private Thread mRecThread;
		
		ObjectOutputStream outStream;

		public GameClient(InetAddress address, int port) {
			Log.d(CLIENT_TAG, "Creating GameClient");
			this.mAddress = address;
			this.PORT = port;

			
			mSendThread = new Thread(new SendingThread());
			mSendThread.start();
		}

		class SendingThread implements Runnable {

			public SendingThread() {
				
			}

			@Override
			public void run() {
				try {
					if (getSocket() == null) {
						setSocket(new Socket(mAddress, PORT));
						Log.d(CLIENT_TAG, "Client-side socket initialized.");
						outStream = new ObjectOutputStream(getSocket().getOutputStream());
					} else {
						Log.d(CLIENT_TAG,
								"Socket already initialized. skipping!");
					}

					mRecThread = new Thread(new ReceivingThread());
					mRecThread.start();

				} catch (UnknownHostException e) {
					Log.d(CLIENT_TAG, "Initializing socket failed, UHE", e);
				} catch (IOException e) {
					Log.d(CLIENT_TAG, "Initializing socket failed, IOE.", e);
				}

				while (true) {
					
				}
			}
		}
		/** This thread takes care of all the incoming packets from the active connections and deals with them */
		class ReceivingThread implements Runnable {

			@Override
			public void run() {

				ObjectInputStream input;
				try {
					
					input = new ObjectInputStream(getSocket().getInputStream());
					
					while (!Thread.currentThread().isInterrupted()) {
						//Loop that reads data from the stream. Currently just converts object to String and sends them along. 
						
						Object readData = null;
						readData = input.readObject();
						if (readData instanceof DataPackage) {
							Log.d(CLIENT_TAG, "Read from the stream: " + readData);
							DataPackage data = (DataPackage)readData;
							updateData(data, false);
						} else {
							break;
						}
					}
					input.close();

				} catch (IOException e) {
					Log.e(CLIENT_TAG, "Server loop error: ", e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		
		/** Sends a serializable object to the sockets output stream */
		public void sendData(Object obj) {
			try {
				
				Socket socket = getSocket();
				//Checks if socket is active for safety
				if (socket == null) {
					Log.d(CLIENT_TAG, "Socket is null, wtf?");
				} else if (socket.getOutputStream() == null) {
					Log.d(CLIENT_TAG, "Socket output stream is null, wtf?");
				}

				outStream.writeObject(obj);
				
			} catch (UnknownHostException e) {
				Log.d(CLIENT_TAG, "Unknown Host", e);
			} catch (IOException e) {
				Log.d(CLIENT_TAG, "I/O Exception", e);
			} catch (Exception e) {
				Log.d(CLIENT_TAG, "Error3", e);
			}
			Log.d(CLIENT_TAG, "Client sent data: " + obj);
		}
		
		/** Called to close down socket */
		public void tearDown() {
			try {
				getSocket().close();
			} catch (IOException ioe) {
				Log.e(CLIENT_TAG, "Error when closing server socket.");
			}
		}
		
	}
}

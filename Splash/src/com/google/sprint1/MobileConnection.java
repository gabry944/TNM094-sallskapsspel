package com.google.sprint1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.metaio.sdk.jni.Vector3d;

import android.os.Handler;
import android.util.Log;

public class MobileConnection {

	private Handler mUpdateHandler;
	private Server mMobileServer;
	private Client mGameClient;
	
	private List<Server> mServers;
	private List<Client> mClients;
	
	private static final String TAG = "MobileConnection";

	private Socket mSocket;
	private int mPort = -1;
	
	
	BlockingQueue<DataPackage> queue;
    private int QUEUE_CAPACITY = 32;
    
    
	public MobileConnection(Handler handler) {
		mServers = new ArrayList<Server>();
		mClients = new ArrayList<Client>();
		queue = new ArrayBlockingQueue<DataPackage>(QUEUE_CAPACITY);
				
		mUpdateHandler = handler;
		mMobileServer = new Server(handler);
	}

	public void connectToPeer(InetAddress address, int port) {
		mGameClient = new Client(address, port);
	}

	public void tearDown() {
		mMobileServer.tearDown();
		
		if(mGameClient != null){
			mGameClient.tearDown();
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

	private class Server {
		ServerSocket mServerSocket = null;
		Thread mThread = null;

		public Server(Handler handler) {
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

					Log.d(TAG, "ServerSocket Created, waiting for connections.");
					while (!Thread.currentThread().isInterrupted()) {
						Socket socket = mServerSocket.accept();
						//Connection found, create a listener thread.
						new Thread(new ListenerThread(socket)).start();
						
					}
				} catch (IOException e) {
					Log.e(TAG, "Error creating ServerSocket: ", e);
					e.printStackTrace();
				}
			}
		}
	}

	/** One ListenerThread is opened for each peer. It reads the inputStream continuously for data */
	private class ListenerThread implements Runnable{
		private Socket socket;
		private ObjectInputStream inStream;
		
		private final String TAG = "ListenerThread";
		public ListenerThread(Socket socket) {
			try {
				this.socket = socket;
				this.socket.setTcpNoDelay(true);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run(){
			try {
				inStream = new ObjectInputStream(socket.getInputStream());
				Log.d(TAG, "Listening to peer: " + socket.toString());
				
				while (!Thread.currentThread().isInterrupted()) {	
					Object readData = null;
					readData = inStream.readObject();
					if (readData instanceof DataPackage) {
						Log.d(TAG, "Read from the stream: " + readData);
						DataPackage data = (DataPackage)readData;
						updateData(data, false);
					}
				}
				inStream.close();

			} catch (IOException e) {
				Log.e("SOCKETTHREAD", "Server loop error: ", e);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class Client {
		private InetAddress mAddress;
		private int PORT;
		private final String CLIENT_TAG = "GameClient";

		private Thread mSendThread;
		private Thread mRecThread;
		
		ObjectOutputStream outStream;
		ObjectInputStream inStream;

		public Client(InetAddress address, int port) {
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
					} else {
						Log.d(CLIENT_TAG,
								"Socket already initialized. skipping!");
					}
					outStream = new ObjectOutputStream(getSocket().getOutputStream());
					/*inStream = new ObjectInputStream(getSocket().getInputStream());
					
					mRecThread = new Thread(new ReceivingThread());
					mRecThread.start();
					*/
				} catch (UnknownHostException e) {
					Log.d(CLIENT_TAG, "Initializing socket failed, UHE", e);
				} catch (IOException e) {
					Log.d(CLIENT_TAG, "Initializing socket failed, IOE.", e);
				}

				//Sending loop
				while (true) {
					DataPackage data;
					try {
						data = queue.take();
						sendData(data);
					} catch (InterruptedException e) {
						Log.e(CLIENT_TAG, "Sending loop failed.", e);
						e.printStackTrace();
					}
				}
			}
		}
		/** This thread takes care of all the incoming packets from the active connections and deals with them */
		class ReceivingThread implements Runnable {

			@Override
			public void run() {

				try {
					
					while (!Thread.currentThread().isInterrupted()) {
						//Loop that reads data from the stream. Currently just converts object to String and sends them along. 
						
						Object readData = null;
						readData = inStream.readObject();
						if (readData instanceof DataPackage) {
							Log.d(CLIENT_TAG, "Read from the stream: " + readData);
							DataPackage data = (DataPackage)readData;
							updateData(data, false);
						} else {
							break;
						}
					}
					inStream.close();

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

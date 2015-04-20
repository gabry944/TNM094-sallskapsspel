package com.google.sprint1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.metaio.sdk.jni.Vector3d;

import android.os.Handler;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

public class MobileConnection {

	private ServerSocket mServerSocket;
	private List<InetAddress> mIPs;
	private List<Peer> mPeers;
	
	private static final String TAG = "MobileConnection";
	public static final int SERVER_PORT = 8196;
	
	BlockingQueue<DataPackage> queue;
    private int QUEUE_CAPACITY = 32;
    
	public MobileConnection(Handler handler) {
		mIPs = new ArrayList<InetAddress>();
		mPeers = new ArrayList<Peer>();

		queue = new ArrayBlockingQueue<DataPackage>(QUEUE_CAPACITY);
			
		//Start a ServerThread
		new Thread(new ServerThread()).start();
	}

	public void connectToPeer(InetAddress address, int port) {
		if (!(mIPs.contains(address)) && mServerSocket.getInetAddress() != address)
		{
			new Thread(new ConnectionThread(address)).start();
		}else{
			Log.d(TAG,"Already connected to: " + address);
		}
	}

	public void tearDown() {
		try {
			mServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public synchronized void sendData(byte[] data)
	{
		if(!mPeers.isEmpty())
		{
			for(int i = 0; i < mPeers.size(); i++)
				sendPackage(data, mPeers.get(i));
		}else{
			Log.d(TAG, "Cannot send data. Not connected to any peers.");
		}
	}
	

	/** Sends a serializable object to the sockets output stream */
	private synchronized void sendPackage(byte[] data, Peer peer) {
		try {
			OutputStream outStream = peer.getOutputStream();
			outStream.write(data);
			outStream.flush();
			
		} catch (UnknownHostException e) {
			Log.d(TAG, "Unknown Host", e);
		} catch (IOException e) {
			Log.d(TAG, "I/O Exception", e);
		} catch (Exception e) {
			Log.d(TAG, "Error3", e);
		}
		Log.d(TAG, "Sent data to: " + peer.getAdress());
	}
	
	/** Handshake is called when the serversocket accepts (finds) another peer
	 * it will add the Peer to the peerlist and start to listen to it. 
	 * It also sends a list to all other peers it is connected to so the new peer can connect to them aswell
	 * @param socket
	 */
	private void handshake(Socket socket)
	{
		try {
			//Send back list with other peers
			Peer peer = new Peer(socket);
			Log.d(TAG, peer.getAdress() +" connected.");
			
			ByteBuffer buffer;
			for (int i = 0; i < mIPs.size(); i++)
			{
				byte[] byteAddress = mIPs.get(i).getAddress();
				
				buffer = ByteBuffer.allocate(DataPackage.BUFFER_HEAD_SIZE + byteAddress.length);	
				buffer.putInt(byteAddress.length);
				buffer.putChar(DataPackage.IP_LIST);
				buffer.put(byteAddress);
				Log.d(TAG, "Created IP to send with size: " + byteAddress.length);
				peer.getOutputStream().write(buffer.array());
				peer.getOutputStream().flush();
				buffer.clear();
			}
			
			Log.d(TAG, "Sent IP list");
			new Thread(new ListenerThread(peer)).start();

			//Add this peer to the list
			mPeers.add(peer);
			mIPs.add(peer.getAdress());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Handle the object found in the outputstream and sends it to the correct place
	 * @param o
	 */
	private synchronized void handleData(DataPackage data)
	{
		switch (data.getOperationCode())
		{
		case DataPackage.BALL_FIRED: 
				fireBall(data.getData());
				break;
			case DataPackage.ANT: 
				updateAnt(data.getData());
				break;
		case DataPackage.IP_LIST:
			try {
				Log.d(TAG, "Recieved from other peer: " + InetAddress.getByAddress(data.getData()).toString());
				connectToPeer(InetAddress.getByAddress(data.getData()), SERVER_PORT);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		default:
			break;
		}
		
		
	}
	
	/** One ServerThread will run listening for new connections. */
	class ServerThread implements Runnable {
		public ServerThread()
		{
			
		}
		@Override
		public void run() {

			try {
				mServerSocket = new ServerSocket(SERVER_PORT);
				Log.d(TAG, "ServerSocket Created, waiting for connections.");
				while (!Thread.currentThread().isInterrupted()) {
					Socket socket = mServerSocket.accept();
					
					//Connection found, init it.
					handshake(socket);
					
				}
			} catch (IOException e) {
				Log.e(TAG, "Error creating ServerSocket: ", e);
				e.printStackTrace();
			}
		}
	}
	
	
	/** One ListenerThread is opened for each peer. It reads the inputStream continuously for data */
	private class ListenerThread implements Runnable{
		
		private final String TAG = "ListenerThread";
		private Peer mPeer;
		public ListenerThread(Peer peer) {
			mPeer = peer;
		}
		
		public void run(){
			Log.d(TAG, "Listening to: " + mPeer.getAdress());
			while (!Thread.currentThread().isInterrupted()) {
				DataPackage data = new DataPackage(mPeer.getInputStream());
				handleData(data);
			}
			Log.d(TAG, "Stopped listening to: " + mPeer.getAdress());
		}
	}
	
	private class ConnectionThread implements Runnable{
		
		private InetAddress address;
		public ConnectionThread(InetAddress address)
		{
			this.address =address;
		}
		
		public void run(){
			try {
				Socket socket = new Socket(address, SERVER_PORT);

				Peer peer = new Peer(socket);
				mPeers.add(peer);
				mIPs.add(address);

				new Thread(new ListenerThread(peer)).start();
				Log.d(TAG, "Connected to: " + address);
			} catch (IOException e) {
				Log.e(TAG,"Error when connecting.", e);
				e.printStackTrace();
			}
		
		}
	}
	//FUNCTIONS FOR UPDATING GAME STATE
	private void fireBall(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int id = buffer.getInt();
		Vector3d vel = new Vector3d(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		Vector3d pos = new Vector3d(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		GameState.getState().exsisting_paint_balls.get(id).fire(vel, pos);
	}
	
	private void updateAnt(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int id = buffer.getInt();
		Vector3d pos = new Vector3d(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		GameState.getState().ants.get(id).setPosition(pos);
	}
	
}
package com.google.sprint1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

import android.os.Handler;
import android.util.Log;

public class MobileConnection {

	private ServerSocket mServerSocket;
	private List<InetAddress> mIPs;
	private List<Peer> mPeers;
	
	private static final String TAG = "MobileConnection";
	public static final int SERVER_PORT = 8196;
    
    private boolean handshakeActive = false;
    
	public MobileConnection(Handler handler) {
		mIPs = new ArrayList<InetAddress>();
		mPeers = new ArrayList<Peer>();

		//Start a ServerThread
		new Thread(new ServerThread()).start();
	}

	/**
	 * Connects to the given InetAdress and port. 
	 * @param address InetAddress to connect to.
	 */
	public synchronized void connectToPeer(InetAddress address) {
		if (!(mIPs.contains(address)) && mServerSocket.getInetAddress() != address)
		{
			Log.d(TAG, "Connecting to: " + address);
			Thread con = new Thread(new ConnectionThread(address));
			con.start();
		}else{
			Log.d(TAG,"Already connected to: " + address);
		}
	}

	/**
	 * tearDown is called when the server is supposed to shut down.
	 * NOT FULLY IMPLEMENTED.
	 */
	public void tearDown() {
		try {
			
			mServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of peers connected.
	 * @return number of peers connected.
	 */
	public int getNumberOfConnections(){
		return mIPs.size();
	}
	/**
	 * Sends data to ALL peers on the network. Simply calls
	 * sendPackage for each peer in mPeers.
	 * @param data Data to send. 
	 */
	public synchronized void sendData(byte[] data)
	{
		if(!mPeers.isEmpty())
		{
			for(int i = 0; i < mPeers.size(); i++)
				sendPackage(data, mPeers.get(i));
		}else{
			//Log.d(TAG, "Cannot send data. Not connected to any peers.");
		}
	}
	

	/** 
	 * Sends a byte array to the peer using the peers outputstream. 
	 * @param data Byte Array with the data to send.
	 * @param peer The peer to send to.
	 */
	public synchronized void sendPackage(byte[] data, Peer peer) {
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
	}
	
	/** 
	 * Handshake is called when the serversocket accepts (finds) another peer
	 * it will add the Peer to the peerlist and start to listen to it. 
	 * It also sends a list to all other peers it is connected to so the new peer can connect to them aswell
	 * @param socket
	 */
	private void handshake(Socket socket)
	{
		try {
			//Check if already connected
			if (!mIPs.contains(socket.getInetAddress()))
			{
				Peer peer = new Peer(socket);
				Log.d(TAG, peer.getAdress() +" connected.");
				Log.d(TAG, "SIZE OF MIPS: " + mIPs.size());
				
				//Send back list with other peers
				sendIPList(peer);

				new Thread(new ListenerThread(peer)).start();

				//Add this peer to the list
				mPeers.add(peer);
				mIPs.add(peer.getAdress());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/** 
	 * Sends back a list of peers to connect to.
	 * @param peer The peer recieving the handshake.
	 * @throws IOException 
	 */
	private synchronized void sendIPList(Peer peer) throws IOException{
		
		ByteBuffer buffer;
		buffer = ByteBuffer.allocate(DataPackage.BUFFER_HEAD_SIZE +  4*mIPs.size() + 4);
		buffer.putInt(4*mIPs.size()+ 4);
		buffer.putShort(DataPackage.IP_LIST);
		
		//ID assigned: 
		buffer.putInt(mIPs.size()+1);
		for (int i = 0; i < mIPs.size(); i++)
		{
			byte[] byteAddress = mIPs.get(i).getAddress();
			buffer.put(byteAddress);
			Log.d(TAG, "Created IP to send with size: " + byteAddress.length);
		}
		peer.getOutputStream().write(buffer.array());
		peer.getOutputStream().flush();
		buffer.clear();
		
		Log.d(TAG, "Sent IP list");
	}
	
	/**
	 * Handle the object found in the outputstream and sends it to the correct place.
	 * Where depends on the Operation code. Each different case has an own function
	 * to update data. 
	 * @param o
	 */
	private synchronized void handleData(DataPackage data)
	{
		switch (data.getOperationCode())
		{
		case DataPackage.BALL_FIRED: 
			fireBall(data.getData());
			break;
		case DataPackage.ANT_POS_UPDATE: 
			updateAntPos(data.getData());
			break;
		case DataPackage.ANT_HIT:
			antHit(data.getData());
			break;
		case DataPackage.ANT_REACHED_TOWER:
			antReachedTower(data.getData());
			break;
		case DataPackage.IP_LIST:
			resolveHandshake(data.getData());
			break;
		
		default:
			break;
		}
	}
	
	/**
	 * One ServerThread will run listening to new connections. When a connection is accepted
	 * it will run handshake() to send an initial package (with ips).
	 * @author Pontus
	 *
	 */
	class ServerThread implements Runnable {
		public ServerThread()
		{
			
		}
		@Override
		public void run() {

			try {
				mServerSocket = new ServerSocket(SERVER_PORT);
				mServerSocket.getInetAddress();
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
	
	
	/**
	 * One ListenerThread is opened for each peer. It reads the inputStream continuously for data and
	 * sends the data off to handleData when it is recieved. 
	 * @author Pontus
	 *
	 */
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
	
	/** 
	 * ConnectionThread class is a Thread that is used to connect to other peers.
	 * It creates a ListenerThread to listen and adds the Peer to mPeers and 
	 * the address to mIPs. The listener thread will most likely recieve a handshake
	 * shortly after connecting.  
	 * @author Pontus
	 *
	 */
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
				new Thread(new ListenerThread(peer)).start();
				
				mPeers.add(peer);
				mIPs.add(peer.getAdress());
				
				Log.d(TAG, "Connected to: " + address);
			} catch (IOException e) {
				Log.e(TAG,"Error when connecting.", e);
				e.printStackTrace();
			}	catch(NullPointerException e){
				Log.e(TAG,"Error when connecting.", e);
				e.printStackTrace();
			}
		
		}
	}
	//FUNCTIONS FOR UPDATING GAME STATE
	/**
	 * Function for dealing with BALL_FIRED OP. Fires the ball with the given velocity.
	 * @param data
	 */
	private synchronized void fireBall(byte[] data)
	{ 	
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int id = buffer.getInt();
		Vector3d vel = new Vector3d(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		Vector3d pos = new Vector3d(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		if(id < 20)
			GameState.getState().exsisting_paint_balls.get(id).fire(vel, pos);
	}
	
	/**
	 * Deals with the data with ANT_POS_UPDATE OP. Updates the position and rotation of the ant 
	 * and activates it if it is disabled. 
	 * @param data 
	 */
	private synchronized void updateAntPos(byte[] data)
	{
		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int id = buffer.getInt();
		Vector3d pos = new Vector3d(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		Rotation eulerRot = new Rotation(buffer.getFloat(),buffer.getFloat(),buffer.getFloat());
		if (!GameState.getState().ants.get(id).isActive())
			GameState.getState().ants.get(id).setActive(true);
		
		GameState.getState().ants.get(id).setPosition(pos);
		GameState.getState().ants.get(id).setRotation(eulerRot);
	}
	
	/**
	 * Deals with the data with ANT_HIT OP.
	 * @param data Data from stream. 
	 */
	private synchronized void antHit(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int antId = buffer.getInt();
		int playerId = buffer.getInt();
		int ballId = buffer.getInt();
		Log.d(TAG, "Collision with ant " + antId +" by player " + playerId);
		GameState.getState().ants.get(antId).setIsHit(true, playerId);
		GameState.getState().exsisting_paint_balls.get(ballId).disable();
	}
	
	/**
	 * Deals with the data with ANT_HIT OP.
	 * @param data Data from stream. 
	 */
	private synchronized void antReachedTower(byte[] data)
	{
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int antId = buffer.getInt();
		int playerId = buffer.getInt();

		Ant ant = GameState.getState().ants.get(antId);
		//GameState.getState().players.get(playerId).removeMarker();
		GameState.getState().players.get(playerId).increaseScore(ant.getType());
		Log.d(TAG, "Ant  " + antId +" reached player " + playerId);
		ant.setActive(false);
	}
	/**
	 * Function that deals with the data package sent from the server (or in this case host) in handshake.
	 * It recieves a list of IPS to connect to and assigns an ID to the player.
	 * ID is determined by calculating the amount of other peers in the network.
	 * @param data
	 */
	private synchronized void resolveHandshake(byte[] data)
	{
		//Check if handshake already took place to prevent multiple assigns of ID
		//TODO: Should probably find a better solution for this. 
		if (handshakeActive)
			return;
		
		handshakeActive = true;
		ByteBuffer buffer = ByteBuffer.wrap(data);
		
		GameState.getState().myPlayerID =buffer.getInt();
		
		//We are assuming IPv4 so each address is 4 bytes. 
		byte[] byteIP = new byte[4];
		Log.d(TAG, "Resolving handshake.");
		//Loops through the different IPS.
		while (buffer.hasRemaining())
		{
			try {
				buffer.get(byteIP);
				connectToPeer(InetAddress.getByAddress(byteIP));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Log.d(TAG, "Assigned ID: " + GameState.getState().myPlayerID);
	}
}
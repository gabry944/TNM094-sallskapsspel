package com.google.sprint1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.util.Log;

public class DataPackage{
	public static final String TAG = "DataPackage";
	
	//Different Operation Codes
	public static final short BALL_FIRED = 0;
	public static final short ANT_POS_UPDATE = 1;
	public static final short IP_LIST = 2;
	public static final short INVALID = 3;
	public static final short ANT_HIT = 4;
	public static final short ANT_REACHED_TOWER = 5;
	public static final short PLAYER_READY = 6;
	public static final short POWERUP_TAKEN = 7;
	
	//Buffer head is the first bytes of every package sent.
	//Contains Amount of data (in bytes) and operation code (in that order)
	public static final int BUFFER_HEAD_SIZE = 6;
	private byte[] bufferHead = new byte[BUFFER_HEAD_SIZE];
	private byte[] data;
	
	short operationCode;
	

	DataPackage(short OC, byte[] data)
	{
		operationCode = OC;
		this.data = data;
	}
	
	/**
	 * Creates a Datapackage by listening to an inputstream. 
	 * First reads the size and the operation code and then
	 * reads the rest of the data as a byte array. 
	 * @param instream
	 */
	DataPackage(InputStream instream)
	{
		try {
			//Bytes to read 
			instream.read(bufferHead);
			ByteBuffer buffer = ByteBuffer.wrap(bufferHead);
			int bytesToRead = buffer.getInt();
			operationCode = buffer.getShort();
			if(bytesToRead > 0) 
			{
				data = new byte[bytesToRead];
				instream.read(data);
			}else{
				operationCode = INVALID;
			}
				
		} catch (IOException e) {
			operationCode = INVALID;
			Log.e(TAG, "Error when creating DataPackage.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the data as a byte array.
	 * @return data as a byte array.
	 */
	public byte[] getData(){
		return data;
	}
	
	/** 
	 * Get the operation code.
	 * @return operation code as a char. 
	 */
	public short getOperationCode(){
		return operationCode;
	}
	
	public String toString(){
		return "Operation Code: " + operationCode + " (check DataPackage for what it reprensents)." + "Data size: " + data.length;
	}
}

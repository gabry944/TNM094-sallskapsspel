package com.google.sprint1;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;



public class DataPackage implements Serializable{
	
	public static final char BALL_FIRED = 'A';
	public static final char ANT_POS_UPDATE = 'B';
	public static final char IP_LIST = 'c';
	public static final char INVALID = 'I';
	
	public static final int BUFFER_HEAD_SIZE = 6;
	private byte[] bufferHead = new byte[BUFFER_HEAD_SIZE];
	private byte[] data;
	
	char operationCode;
	

	DataPackage(char OC, byte[] data)
	{
		operationCode = OC;
		this.data = data;
	}
	
	
	DataPackage(InputStream instream)
	{
		try {
			//Bytes to read 
			instream.read(bufferHead);
			ByteBuffer buffer = ByteBuffer.wrap(bufferHead);
			int bytesToRead = buffer.getInt();
			operationCode = buffer.getChar();
			if(bytesToRead > 0) 
			{
				data = new byte[bytesToRead];
				instream.read(data);
			}else{
				operationCode = INVALID;
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] getData(){
		return data;
	}
	
	public char getOperationCode(){
		return operationCode;
	}
	
	
	public String toString(){
		return "NOT IMPLEMENTED";
	}
}

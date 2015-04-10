package com.google.sprint1;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.metaio.sdk.jni.Vector3d;

enum DataType {
	BALL_FIRED,
	ANT_HIT,
};

public class DataPackage implements Serializable{
	
	public static final char BALL_FIRED = 'A';
	public static final char ANT_HIT = 'B';
	public static final int MAX_CAPACITY = 64;
	
	char operationCode;
	
	byte[] data;
	int id;
	float velocityX;
	float velocityY;
	float velocityZ;
	
	float positionX;
	float positionY;
	float positionZ;
	
	DataPackage(int id, Vector3d vel, Vector3d pos)
	{
		this.id = id;
		velocityX = vel.getX();
		velocityY = vel.getY();
		velocityZ = vel.getZ();
		
		positionX = pos.getX();
		positionY = pos.getY();
		positionZ = pos.getZ();
	}
	
	ByteBuffer getBuffer(){
		//Allocate a buffer and add OC and a byte array.
		ByteBuffer buffer = ByteBuffer.allocate(MAX_CAPACITY);
		buffer.putChar(operationCode);
		buffer.put(data);
		
		//Switch buffer to read mode and return
		buffer.flip();
		return buffer;
	}
	
	public String toString(){
		return ("id: " + id + "velocity: " + velocityX + ", "+ velocityY + ", " + velocityZ +"," +
				"position: " + positionX + "," + positionY +", " + positionZ);
	}
}

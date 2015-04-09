package com.google.sprint1;

import java.io.Serializable;

import com.metaio.sdk.jni.Vector3d;

enum DataType {
	BALL_FIRED,
	ANT_HIT,
};

public class DataPackage implements Serializable{
	
	public static final char BALL_FIRED = 'A';
	public static final char ANT_HIT = 'B';
	
	char message;
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
	
	public String toString(){
		return ("id: " + id + "velocity: " + velocityX + ", "+ velocityY + ", " + velocityZ +"," +
				"position: " + positionX + "," + positionY +", " + positionZ);
	}
}

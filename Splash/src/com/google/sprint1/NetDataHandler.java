package com.google.sprint1;

import java.nio.ByteBuffer;

import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class NetDataHandler {

	public static byte[] antPos(int id, Vector3d pos, Rotation rot)
	{
		//Allocate a buffer and add OC and a byte array.
		ByteBuffer buffer = ByteBuffer.allocate(DataPackage.BUFFER_HEAD_SIZE + 4*7);
		//amount of bytes
		buffer.putInt(4*7);
		//operation code
		buffer.putShort(DataPackage.ANT_POS_UPDATE);
		//data: id - position - rotation
		buffer.putInt(id);
		buffer.putFloat(pos.getX());
		buffer.putFloat(pos.getY());
		buffer.putFloat(pos.getZ());
		buffer.putFloat(rot.getEulerAngleRadians().getX());
		buffer.putFloat(rot.getEulerAngleRadians().getY());
		buffer.putFloat(rot.getEulerAngleRadians().getZ());
	
		return buffer.array();
	}
	
	public static byte[] ballFired(int id, Vector3d vel, Vector3d pos)
	{
		//Allocate a buffer and add OC and a byte array.
		ByteBuffer buffer = ByteBuffer.allocate(6 + 4*7);
		//amount of bytes
		buffer.putInt(7*4);
		//operation code
		buffer.putShort(DataPackage.BALL_FIRED);
		//data id - vel - pos
		buffer.putInt(id);
		buffer.putFloat(vel.getX());
		buffer.putFloat(vel.getY());
		buffer.putFloat(vel.getZ());
		buffer.putFloat(pos.getX());
		buffer.putFloat(pos.getY());
		buffer.putFloat(pos.getZ());
		
		return buffer.array();
	}
	
	public static byte[] antHit(int antId, int playerId, int ballId)
	{
		int DATA_SIZE = 4*3;
		//Allocate a buffer and add OC and a byte array.
		ByteBuffer buffer = ByteBuffer.allocate(DataPackage.BUFFER_HEAD_SIZE + DATA_SIZE);
		//amount of bytes
		buffer.putInt(DATA_SIZE);
		//operation code
		buffer.putShort(DataPackage.ANT_HIT);
		//data antId - playerId - ballId
		buffer.putInt(antId);
		buffer.putInt(playerId);
		buffer.putInt(ballId);
		
		return buffer.array();
	}
	
	public static byte[] antReachedTower(int antId, int playerId)
	{
		int DATA_SIZE = 4*2;
		//Allocate a buffer and add OC and a byte array.
		ByteBuffer buffer = ByteBuffer.allocate(DataPackage.BUFFER_HEAD_SIZE + DATA_SIZE);
		//amount of bytes
		buffer.putInt(DATA_SIZE);
		//operation code
		buffer.putShort(DataPackage.ANT_REACHED_TOWER);
		//data antId - playerId - ballId
		buffer.putInt(antId);
		buffer.putInt(playerId);
		
		return buffer.array();
	}
}

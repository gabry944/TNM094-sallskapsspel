package com.google.sprint1;

import java.io.Serializable;

import com.metaio.sdk.jni.Vector3d;

enum DataType {
	PAINTBALL,
	
};

public class DataPackage implements Serializable{
	
	DataType type = DataType.PAINTBALL;
	int id;
	Vector3d velocity;
	Vector3d position;
	
	DataPackage(int id, Vector3d vel, Vector3d pos)
	{
		this.id = id;
		velocity = vel;
		position = pos;
	}
	
	public String toString(){
		return ("id: " + id + "velocity: " + velocity + "position: " + position);
	}
}

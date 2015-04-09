package com.google.sprint1;


import java.io.Serializable;

import android.app.Activity;
import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.tools.io.AssetsManager;

public class PaintBall extends Drawable implements Serializable
{
	public static final String TAG = "PaintBall";
	
	public IGeometry geometry; 
	public IGeometry splashGeometry;
	public IGeometry paintballShadow;
	public int id;
	public boolean isActive;

	public PaintBall(int id, IGeometry geo, IGeometry splGeo, IGeometry pbShad) {
		super();
		this.id = id;
		geometry = geo;
		splashGeometry = splGeo;
		paintballShadow = pbShad;
		
		velocity= new Vector3d(0.0f,0.0f,0.0f);
		
		setGeometryProperties(geometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(splashGeometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(paintballShadow, 0.7f, new Vector3d(0f, 0f, 0f), new Rotation((float) (3*Math.PI/2), 0f, 0f));
		geometry.setVisible(false);	
		splashGeometry.setVisible(false);
		paintballShadow.setVisible(false);
		isActive = false;
		}
		
	/** Called every frame. Updates position and checks if on the ground */
	public void update(){
		
		physicsPositionCalibration();
		paintballShadow.setTranslation(new Vector3d(geometry.getTranslation().getX(),
					  									geometry.getTranslation().getY(),
					  									0f));
			
		// checks for collision with ground 	
		if(geometry.getTranslation().getZ() <= 0f)
		{	
			splashGeometry.setTranslation(geometry.getTranslation());
			velocity = new Vector3d(0.0f, 0.0f, 0.0f);
			geometry.setTranslation(new Vector3d(0f,0f,0f));
			splashGeometry.setVisible(true);
			velocity = new Vector3d(0f, 0f, 0f);
			disable();
			
		}
	}
	
	/** Check if paintball is current active */
	public boolean isActive()
	{
		return isActive;
	}
	
	/** Fire the ball */
	public void fire(Vector3d vel, Vector3d pos){
		geometry.setTranslation(pos);
		velocity = vel;
		geometry.setVisible(true);
		paintballShadow.setVisible(true);
		isActive = true;
	}
	
	/** Disable the ball */
	public void disable(){
		geometry.setVisible(false);
		paintballShadow.setVisible(false);
		isActive = false;
	}
	
	/** move an object depending on physics calculated with Euler model*/
	private void physicsPositionCalibration()
	{
		Vector3d totalForce =  new Vector3d(0f, 0f, 0f);
		Vector3d gravity = new Vector3d(0f, 0f, -9.82f);
		float mass = 0.1f;
		float timeStep = 0.2f;
		Vector3d acceleration =  new Vector3d(0f, 0f, 0f);
		
		// right now we only have gravity as force
		totalForce.setX(gravity.getX() * mass);
		totalForce.setY(gravity.getY() * mass);
		totalForce.setZ(gravity.getZ() * mass);
		
		// Newtons second law says that: F=ma => a= F/m
		acceleration.setX(totalForce.getX() / mass);
		acceleration.setY(totalForce.getY() / mass);
		acceleration.setZ(totalForce.getZ() / mass);
		
		// Euler method gives that Vnew=V+A*dt;
		velocity.setX(velocity.getX()+timeStep*acceleration.getX());
		velocity.setY(velocity.getY()+timeStep*acceleration.getY());
		velocity.setZ(velocity.getZ()+timeStep*acceleration.getZ());
		
		// Euler method gives that PositionNew=Position+V*dt;
		Vector3d position = geometry.getTranslation();
		position.setX(position.getX()+timeStep*velocity.getX());
		position.setY(position.getY()+timeStep*velocity.getY());
		position.setZ(position.getZ()+timeStep*velocity.getZ());
		
		// move object to the new position
		geometry.setTranslation(position);
		//object.setTranslation(object.getTranslation().add(velocity*timeStep));
	}
	
	public String toString(){
		return ("Paintball: Player " + id +". Position: " + geometry.getTranslation());
	}
}	
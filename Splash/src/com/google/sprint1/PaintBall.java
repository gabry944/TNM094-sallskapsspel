package com.google.sprint1;

import java.io.Serializable;

import android.app.Activity;
import android.util.Log;



import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class PaintBall extends Drawable 
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
			disable();
		}
		
		
		//Check for collision with ants
		for(int i = 0; i < 10 ; i++)
		{
			if (checkCollision(GameState.getState().ants.get(i).getGeometry())) { 
				//GameState.getState().ants.get(i).ant.setRotation(new Rotation( (float) (3 * Math.PI / 4), 0f, 0f), true);
				
				GameState.getState().ants.get(i).setIsHit(true);
				disable();
			}
		}
		
		
		for (int i = 0; i < GameState.getState().powerUps.size(); i++)
		{
			if (checkCollision(GameState.getState().powerUps.get(i).geometry)) {
				//player.superPower = true;
				GameState.getState().powerUps.get(i).geometry.setVisible(false);
			}
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
		splashGeometry.setTranslation(geometry.getTranslation());
		velocity = new Vector3d(0.0f, 0.0f, 0.0f);
		geometry.setTranslation(new Vector3d(0f,0f,0f));
		splashGeometry.setVisible(true);
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
	
	private boolean checkCollision(IGeometry obj) {
		
		int scale = 4;
		Vector3d boxmin = obj.getBoundingBox(true).getMin();
		Vector3d boxmax = obj.getBoundingBox(true).getMax();
		float min = 0;
		float max = 0;
		
		if(boxmin.getX() < boxmin.getY() && boxmin.getX() < boxmin.getZ() )
			min = boxmin.getX();
		else if(boxmin.getY() < boxmin.getX() && boxmin.getY() < boxmin.getZ())
			min = boxmin.getY();
		else
			min = boxmin.getZ();
		
		if(boxmax.getX() > boxmax.getY() && boxmax.getX() > boxmax.getZ() )
			max = boxmax.getX();
		else if(boxmax.getY() > boxmax.getX() && boxmax.getY() > boxmax.getZ())
			max = boxmax.getY();
		else
			max = boxmax.getZ();
		/*if (geometry.getTranslation().getX() + geometry.getBoundingBox().getMax().getX() >
		boxmin.getX() * scale + obj.getTranslation().getX()
		&& geometry.getTranslation().getX()	+ geometry.getBoundingBox().getMin().getX() <
		boxmax.getX() * scale + obj.getTranslation().getX()
		&& geometry.getTranslation().getY() + geometry.getBoundingBox().getMax().getY() > 
		boxmin.getY() * scale + obj.getTranslation().getY() 
		&& geometry.getTranslation().getY()	+ geometry.getBoundingBox().getMin().getY() < 
		boxmax.getY() * scale + obj.getTranslation().getY() 
		&& geometry.getTranslation().getZ()+ geometry.getBoundingBox().getMax().getZ() > 
		boxmin.getZ() * scale + obj.getTranslation().getZ() 
		&& geometry.getTranslation().getZ()+ geometry.getBoundingBox().getMin().getZ() < 
		boxmax.getZ() * scale + obj.getTranslation().getZ())*/
		if (geometry.getTranslation().getX() + geometry.getBoundingBox().getMax().getX() >
		min * scale + obj.getTranslation().getX()
		&& geometry.getTranslation().getX()	+ geometry.getBoundingBox().getMin().getX() <
		max * scale + obj.getTranslation().getX()
		&& geometry.getTranslation().getY() + geometry.getBoundingBox().getMax().getY() > 
		min * scale + obj.getTranslation().getY() 
		&& geometry.getTranslation().getY()	+ geometry.getBoundingBox().getMin().getY() < 
		max * scale + obj.getTranslation().getY() 
		&& geometry.getTranslation().getZ()+ geometry.getBoundingBox().getMax().getZ() > 
		min * scale + obj.getTranslation().getZ() 
		&& geometry.getTranslation().getZ()+ geometry.getBoundingBox().getMin().getZ() < 
		max * scale + obj.getTranslation().getZ())
			return true;
		else
			return false;
	}
	
	public String toString(){
		return ("Paintball: Player " + id +". Position: " + geometry.getTranslation());
	}
}	

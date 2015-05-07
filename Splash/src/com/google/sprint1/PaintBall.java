package com.google.sprint1;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class PaintBall extends Drawable 
{
	public static final String TAG = "PaintBall";
	
	public static int numberOfBalls = 0;
	
	private IGeometry geometry; 
	private IGeometry splashGeometry;
	private IGeometry paintballShadow;
	private int id; 
	private int playerId;
	private boolean isActive;
	private final MobileConnection connection;
	private Context context;
	
	public PaintBall(IGeometry geo, IGeometry splGeo, IGeometry pbShad, int playerID) {
		super();
	
		connection = GameState.getState().connection;
		
		this.id = numberOfBalls;
		numberOfBalls++;
		
		geometry = geo;
		splashGeometry = splGeo;
		paintballShadow = pbShad;
		playerId = playerID;
		
		startPosition= new Vector3d(0.0f,0.0f,0.0f);
		startVelocity= new Vector3d(0.0f,0.0f,0.0f);
		startTime = 0;
		
		setGeometryProperties(geometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(splashGeometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(paintballShadow, 0.7f, new Vector3d(0f, 0f, 0f), new Rotation((float) (3*Math.PI/2), 0f, 0f));
		geometry.setVisible(false);	
		splashGeometry.setVisible(false);
		paintballShadow.setVisible(false);
		isActive = false;
		
		}
	
	//return the id of the player who ownes the paintball
	public int getPlayerId()
	{
		return playerId;
	}
		
	public IGeometry getGeometry()
	{
		return geometry;
	}
	
	public void setPosition(Vector3d vec)
	{
		geometry.setTranslation(vec);
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int ID)
	{
		id = ID;
	}
	
	/** Called every frame. Updates position and checks if on the ground */
	public void update(){
		
		if(isActive)
		{
			physicsPositionCalibration();
			paintballShadow.setTranslation(new Vector3d(geometry.getTranslation().getX(),
					  									geometry.getTranslation().getY(),
					  									0f));
		
			if(getPlayerId() == GameState.getState().myPlayerID)
				checkCollisions();
			
			// checks for collision with ground 	
			if(geometry.getTranslation().getZ() <= 0f)
			{	
				disable();
			}
		}	
	}
	
	private void checkCollisions(){
		
		//Check for collision with ants
		for(int i = 0; i < GameActivity.getNrOfAnts() ; i++)
		{
			if (isActive() && checkCollision(GameState.getState().ants.get(i).getGeometry())) { 
				
				//Fucking skit ljud kan dö i ett hål
				//Log.d("Sound", "Should play sound??");
				//Sound.playSound(((ContextWrapper) context).getBaseContext());
				
				//GameState.getState().ants.get(i).ant.setRotation(new Rotation( (float) (3 * Math.PI / 4), 0f, 0f), true);
				Log.d(TAG, "Collision with ant " + i +" by player " + getPlayerId());
				connection.sendData(NetDataHandler.antHit(i, getPlayerId(), getId()));
				GameState.getState().ants.get(i).setIsHit(true, getPlayerId());
				
				disable();
			}
		}

		for (int i = 0; i < GameState.getState().powerUps.size(); i++)
		{
			if (checkCollision(GameState.getState().powerUps.get(i).getGeometry()) && isActive == true) {
				//player.superPower = true;
				GameState.getState().powerUps.get(i).setHit(true);
			}
		}
	}

	/** Check if paintball is current active */
	public boolean isActive()
	{
		return isActive;
	}
	
	/** Fire the ball */
	public void fire(Vector3d vel, Vector3d pos)
	{
		geometry.setTranslation(pos);
		startPosition = pos;
		startVelocity = vel;
		startTime = System.currentTimeMillis();
		geometry.setVisible(true);
		paintballShadow.setVisible(true);
		isActive = true;
		
	}
	
	/** Disable the ball */
	public void disable(){
		splashGeometry.setTranslation(geometry.getTranslation());
		startTime = 0;		
		splashGeometry.setVisible(true);
		geometry.setVisible(false);
		paintballShadow.setVisible(false);
		isActive = false;
		//startPosition = new Vector3d(0.0f, 0.0f, 0.0f);
		startVelocity = new Vector3d(0.0f, 0.0f, 0.0f);
		geometry.setTranslation(startPosition);
	}
	public void deactivate(){
		startTime = 0;		
		splashGeometry.setVisible(false);
		geometry.setVisible(false);
		paintballShadow.setVisible(false);
		isActive = false;
		startVelocity = new Vector3d(0.0f, 0.0f, 0.0f);
		geometry.setTranslation(startPosition);
	}
	
	/** move an object depending on physics calculated with Euler model*/
	private void physicsPositionCalibration()
	{
		//long currentTime = SystemClock.elapsedRealtime();
		long currentTime= System.currentTimeMillis();
		float deltaTime = currentTime - startTime;
		//Log.d(TAG, "CurrentTime: " + currentTime + "  DeltaTime : " + deltaTime);
		deltaTime = deltaTime/1000; // convert from ms to s
		deltaTime = deltaTime*8; // speed up time
		//Log.d(TAG, "deltaTime : " + deltaTime);
		Vector3d gravity = new Vector3d(0f, 0f, -9.82f);
		Vector3d position = new Vector3d(0f, 0f, 0f);
		
		// Calculate the objects position
		// x(t) = x(0) + v(0)*t + a * t^2
		// Right now we only have gravity as force: F = mg and a = F/m gives a = g
		position.setX(startPosition.getX()+startVelocity.getX()*deltaTime+gravity.getX()*deltaTime*deltaTime);		
		position.setY(startPosition.getY()+startVelocity.getY()*deltaTime+gravity.getY()*deltaTime*deltaTime);
		position.setZ(startPosition.getZ()+startVelocity.getZ()*deltaTime+gravity.getZ()*deltaTime*deltaTime);
		
		if (position.getZ()<0f)
			position.setZ(0f);
		
		// Move object to the new position
		geometry.setTranslation(position);
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
		
		// a false way to calculate collisions, take some extra buffer since not shore about rotation
		if (geometry.getTranslation().getX() + max+ 50f >
		min * scale + obj.getTranslation().getX() - 50f
		&& geometry.getTranslation().getX()	+ min - 50f <
		max * scale + obj.getTranslation().getX() + 50f
		&& geometry.getTranslation().getY() + max + 50f > 
		min * scale + obj.getTranslation().getY() - 50f
		&& geometry.getTranslation().getY()	+ min - 50f < 
		max * scale + obj.getTranslation().getY() + 50f
		&& geometry.getTranslation().getZ()+ max  + 50f > 
		min * scale + obj.getTranslation().getZ() - 50f
		&& geometry.getTranslation().getZ()+ min  - 50f < 
		max * scale + obj.getTranslation().getZ() + 50f)
			return true;
		else
			return false;
		
		//the true way to calculate, but do not take rotation into account 
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
	}
	
	public String toString(){
		//TODO uppdatera så att startTime, startPosition och startVelocity kommer med.
		return ("Paintball: Player " + id +". Position: " + geometry.getTranslation());
	}
}	

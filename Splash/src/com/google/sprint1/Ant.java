package com.google.sprint1;

import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Ant extends Drawable
{
	public static final String TAG = "Ant";
	private IGeometry ant;
	private boolean isHit;
	private Vector3d diffVec;
	
	float angDiffLimit = 3f;
	float speed = 5f;
	float rotationSpeed = 10f;
	
	/** constructor ant */
	public Ant(IGeometry geo, boolean hit) {
		super();
		ant = geo;
		isHit = hit;
		setGeometryProperties(ant, 4f, new Vector3d(0f, 0f, 0f), new Rotation((float)(Math.PI*3/2), 0f, 0f)); 
		ant.setVisible(false);
		diffVec = new Vector3d(0f, 0f, 0f);
	}
	
	public IGeometry getGeometry()
	{
		return ant;
	}
	
	public boolean getIsHit()
	{
		if(isHit == true)
			return true;
		else
			return false;
	}
	
	public void setIsHit(boolean hit)
	{
		if(hit == true)
			isHit = true;
		else
			isHit =  false;
	}
	
	/** function to spawn the ants at random time and random position */
	public void spawnAnt()
	{
		if(randBetween(1, 100) == 10)
		{
			//spawn ant at random
			ant.setVisible(true);
			ant.setTranslation(new Vector3d(randBetween(-600 , 600), randBetween(-600 , 600), 0f));
		}
		
	}
	
	/** Function to generate movement to the ants */
	public void randomMovement()
	{

		// new angle in radians 
		float angle = ant.getRotation().getAxisAngle().getZ() + randBetween( -angDiffLimit, angDiffLimit);
		
		float diffX = (float)Math.cos(angle);
		float diffY = (float)Math.sin(angle);
		
		Vector3d movement = new Vector3d(new Vector3d(ant.getTranslation().getX() + speed * diffX,
													  ant.getTranslation().getY() +  speed * diffY,
													  0f));
		
		//random movement of the ant until being hit 
		ant.setTranslation(movement);
		ant.setRotation(new Rotation((float)(Math.PI*3/2), 0f, angle * (float)(Math.PI/180) * rotationSpeed));  

	}
	
	/** Makes the ant go to the tower owned by the player who hit the ant */
	public void movementToTower(Vector3d pos)
	{
		pos.setZ(0f);
		
		diffVec = pos.subtract(ant.getTranslation());
		Log.d(TAG, "pos = " + pos);
		ant.setTranslation(ant.getTranslation().add((diffVec.getNormalized()).multiply(speed)));
		ant.setRotation( new Rotation( (float)(Math.PI*3/2),
										0f,
										(float)(Math.tanh(diffVec.getY()/diffVec.getX()))));
		
		//when ant reached tower
		if(diffVec.getX() < 2f && diffVec.getX() > -2f  && diffVec.getY() < 2f && diffVec.getY() > -2f)
		{
			ant.setVisible(false);
			setIsHit(false);
			spawnAnt();
			//player.point();
		}
	}
	
	
	/** function to see if ant is active = visible */
	public boolean isActive()
	{
		//is the ant already spawned
		if(ant.isVisible())
			return true;
		else
			return false;
	}
	
	
	/** calculate a random number between arg start and arg end */
	public static float randBetween(float start, float end)
	{
		return (float)(start + (int)Math.round(Math.random()* (end - start)));
	}

}



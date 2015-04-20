package com.google.sprint1;

import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Ant extends Drawable
{
	public static final String TAG = "Ant";
	private IGeometry ant;
	private IGeometry marker;
	private boolean isHit;
	private Vector3d diffVec;
	private float memory;
	
	float angDiffLimit = (float)(5*Math.PI/180);
	float speed = 1f;
	float angle = 0;
	float randNr = 0;
	int k = 0;
	
	/** constructor ant */
	public Ant(IGeometry geo, IGeometry hitMarker, boolean hit) {
		super();
		ant = geo;
		marker = hitMarker;
		isHit = hit;
		setGeometryProperties(ant, 60f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f)); 
		setGeometryProperties(hitMarker, 0.2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		ant.setVisible(false);
		marker.setVisible(false);
		diffVec = new Vector3d(0f, 0f, 0f);
		memory = 0f;
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
		ant.startAnimation("Take 001", true);
		if(randBetween(1, 100) == 10)
		{
			//spawn ant at random
			ant.setVisible(true);
			ant.setTranslation(new Vector3d(randBetween(-600 , 600), randBetween(-600 , 600), (float)(Math.PI*3/2)));
			ant.setRotation(new Rotation(randBetween(0f , 6.28f), randBetween(0f , 6.28f), (float)(Math.PI*3/2)));
		}
		
	}
	
	/** Function to generate movement to the ants */
	public void randomMovement()
	{		
		//if first time called
		if(k == 0)
		{
			float firstRand = randBetween2(angDiffLimit);
			angle = ant.getRotation().getEulerAngleRadians().getZ() + firstRand;
			memory = firstRand;
			k = 1;
		}
		
		//ant gets a new angle at random
		if(randBetween(1, 10) == 1)
		{
			randNr = randBetween2(angDiffLimit);
			angle = ant.getRotation().getEulerAngleRadians().getZ() + randNr;
			memory = randNr;
		}
		else 
		{
			angle = angle + memory;   // keep turning the same way as before 
		}
		
		float diffX = (float)Math.cos(angle);
		
		float diffY = (float)Math.sin(angle);
		
		Vector3d movement = new Vector3d(new Vector3d(ant.getTranslation().getX() + speed * diffX,
													  ant.getTranslation().getY() +  speed * diffY,
													  0f));
		
		//random movement of the ant until being hit 
		ant.setTranslation(movement);
		ant.setRotation(new Rotation(0f, 0f, angle + (float)(Math.PI*3/2) ));  //(float)(Math.PI*3/2)

	}	
	
	/** Makes the ant go to the tower owned by the player who hit the ant */
	public void movementToTower(Vector3d pos)
	{
		pos.setZ(0f);	
		
		diffVec = ant.getTranslation().subtract(pos);
		//Log.d(TAG, "pos = " + pos);
		
		// check since atan(y/x) == atan(-y/-x)
		if(diffVec.getX() < 0f)
			angle = (float)(Math.atan(diffVec.getY()/diffVec.getX()));
		else
			angle = (float)(Math.atan(diffVec.getY()/diffVec.getX()) + Math.PI);
		
		ant.setRotation( new Rotation( 0f, 0f, angle + (float)(Math.PI*3/2)));  // (float)(Math.PI*3/2)
		ant.setTranslation(ant.getTranslation().subtract((diffVec.getNormalized()).multiply(speed)));
		
		marker.setTranslation(new Vector3d(ant.getTranslation().getX(), ant.getTranslation().getY(), 50f));
		marker.setVisible(true);
		marker.startAnimation("Take 001", true);
		
		//when ant reached tower
		if(diffVec.getX() < 2f && diffVec.getX() > -2f  && diffVec.getY() < 2f && diffVec.getY() > -2f)
		{
			Player.setScore();
			ant.setVisible(false);
			marker.setVisible(false);
			marker.startAnimation("Take 001", false);
			setIsHit(false);
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
	
	public static float randBetween2(float end)
	{
		return (float)(Math.random()* (2*end) - end);
	}

}



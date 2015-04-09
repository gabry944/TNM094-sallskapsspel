package com.google.sprint1;

import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Ant extends Drawable
{
	public static final String ANT = "AntActivity";
	public IGeometry ant;
	float angDiffLimit = 3f;
	float speed = 10f;
	
	
	public Ant(IGeometry geo) {
		super();
		ant = geo;
		setGeometryProperties(ant, 4f, new Vector3d(0f, 0f, 0f), new Rotation((float)(Math.PI*3/2), 0f, 0f)); 
		ant.setVisible(false);
	}
	
	public void spawnAnt()
	{
		if(randBetween(1, 500) == 10)
		{
			//spawn ant at random
			ant.setVisible(true);
			ant.setTranslation(new Vector3d(randBetween(-600 , 600), randBetween(-600 , 600), 0f));
		}
		
	}
	
	public float movement()
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
		ant.setRotation(new Rotation((float)(Math.PI*3/2), angle * (float)(Math.PI/180), 0f));
		return angle;
	}
	
	public boolean isActive()
	{
		//is the ant already spawned
		if(ant.isVisible())
			return true;
		else
			return false;
	}
	
	public static float randBetween(float start, float end)
	{
		return (float)(start + (int)Math.round(Math.random()* (end - start)));
	}

}



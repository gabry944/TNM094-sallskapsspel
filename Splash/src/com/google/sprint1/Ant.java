package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Ant extends Drawable
{
	
	public IGeometry ant;
	
	
	public Ant(IGeometry geo) {
		super();
		ant = geo;
		setGeometryProperties(ant, 4f, new Vector3d(0f, 0f, 0f), new Rotation((float)(Math.PI*3/2), 0f, 0f)); 
		ant.setVisible(false);
	}
	
	public void spawnAnt()
	{
		//spawn ant at random
		ant.setVisible(true);
		ant.setTranslation(new Vector3d(randBetween(-600 , 600), randBetween(-600 , 600), 0f));
		
		
	}
	
	public void movement()
	{
		//random movement of the ant until being hit 
		ant.setTranslation(new Vector3d(ant.getTranslation().getX() + randBetween(-3,3),
										ant.getTranslation().getY() + randBetween(-3,3),
										0f));
	}
	
	public boolean isActive()
	{
		//is the ant already spawned
		if(ant.isVisible())
			return true;
		else
			return false;
	}
	
	public static int randBetween(int start, int end)
	{
		return start + (int)Math.round(Math.random()* (end - start));
	}

}



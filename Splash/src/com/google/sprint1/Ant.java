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
		setGeometryProperties(ant, 4f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, (float)(Math.PI*3/2)));
		ant.setVisible(false);
	}
	
	public void spawnAnt()
	{
		//spawn ant at random 
		
		
	}
	
	public void movement()
	{
		//random movement of the ant until being hit 
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



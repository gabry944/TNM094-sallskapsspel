package com.google.sprint1;

import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class PowerUp extends Drawable {
	
	public static final String TAG = "powerUp";
	private IGeometry geometry;
	private boolean visible;
	private float timer;
	private float startTime;
	
	public PowerUp(IGeometry geo){
		geometry = geo;
		visible = true;
		setGeometryProperties(geometry, 2.1f, new Vector3d(0f, 0f, 50f), new Rotation(0f, 0f, 0f));
		timer = 15f;
		
	}
	
	public IGeometry getGeometry()
	{
		return geometry;
	}
	
	public void setHit(boolean hit)
	{
		visible = !hit;
		if(hit)
			startTime(timer);
	}
	
	public boolean isHit()
	{
		if (visible)
			return false;
		else 
			return true;
	}
	
	public void update()
	{	
		float scaleStart = 0f;
		/** Function for animation on the powerup */
			if (geometry.getScale().getX() > 4.0f) {
				scaleStart = -0.04f;
			}
			if (geometry.getScale().getX() < 1.0f) {
				scaleStart = 0.04f;
			}
			geometry.setScale(geometry.getScale().add(
					new Vector3d(scaleStart, scaleStart, scaleStart)));
			// Log.d(TAG, "scale = " + powerUp.getScale());
			if(visible == false)
			{
				geometry.setVisible(false);
				if(updateTimer())	//if the timer returns 0 the powerup respawns
				{
					timer = 15f;
					geometry.setVisible(true);
					setHit(false);
					//aim.setPowerUp(false);
					GameState.getState().powerUps.get(0).setHit(false);
				}
			}
	}
	
	public void startTime(float timerTime)
	{
		
	}
	
	public boolean updateTimer()
	{
//		float deltaTime;
//		if(timer == 15f)		//if first time entering the function
//		{
//			startTime = System.nanoTime();
//			//deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
//			timer = startTime - 0.1f;	//to avoid this if again
//		}
//		else
//		{
//			deltaTime = startTime - (System.nanoTime() / 1000000000.0f);
//			timer = timer - deltaTime;			
//		}
		
		timer = timer - 0.05f;
		Log.d(TAG, "timer : " + timer);

		if(timer <= 0f)
			return true;
		else
			return false;
	}
	
	
}

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
	private long prevTime;
	private boolean collision = false;
	private float scaleDiff = 0.08f;
	
	public PowerUp(IGeometry geo){
		geometry = geo;
		visible = true;
		setGeometryProperties(geometry, 2.1f, new Vector3d(0f, 0f, 100f), new Rotation(0f, 0f, (float)(Math.PI/2)));
		timer = 20f;
		
		geometry.startAnimation("Take 001", true);
		
		
	}
	
	public IGeometry getGeometry()
	{
		return geometry;
	}
	
	public void setHit(boolean hit)
	{
		visible = !hit;
		collision = true;
		if(hit)
			startTime();
	}
	
	public boolean isHit()
	{
		if (visible)
			return false;
		else 
			return true;
	}
	
	/**Return true if collision happened*/
	public boolean getCollision()
	{
		if(collision == true){
			collision = false;
			return true;
		}
		else
			return false;
	}
	
	public void update()
	{			
		
		/** Function for animation on the powerup */
			if (geometry.getScale().getX() > 2.5f) {
				scaleDiff = -0.08f;
			}
			if (geometry.getScale().getX() < 1.0f) {
				scaleDiff = 0.08f;
			}
			geometry.setScale(geometry.getScale().add(
					new Vector3d(scaleDiff, scaleDiff, scaleDiff)));
			// Log.d(TAG, "scale = " + powerUp.getScale());
			if(visible == false)
			{
				geometry.setVisible(false);
				if(updateTimer())	//if the timer returns 0 the powerup respawns
				{
					timer = 20f;
					geometry.setVisible(true);
					setHit(false);
					//aim.setPowerUp(false);
					GameState.getState().powerUps.get(0).setHit(false);
				}
			}
	}
	
	public void startTime()
	{
		prevTime = System.currentTimeMillis();
	}
	
	public boolean updateTimer()
	{
		long currentTime = System.currentTimeMillis();
		
		float deltaTime = currentTime - prevTime;
		deltaTime = deltaTime/1000; // convert from ms to s
		prevTime = System.currentTimeMillis();	// save prevTime for next iteration
		
		timer = timer - deltaTime;
		
//		timer = timer - 0.05f;
		//Log.d(TAG, "timer : " + timer);

		if(timer <= 0f)
			return true;
		else
			return false;
	}
	
	
}

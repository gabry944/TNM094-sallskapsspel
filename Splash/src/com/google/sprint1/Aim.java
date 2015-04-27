package com.google.sprint1;

import java.util.ArrayList;

import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Aim extends Drawable 
{

	public static final String TAG = "Aim";
	private IGeometry crosshair;
	private boolean powerUp;
	private ArrayList<IGeometry> ballPath; // lista med bollar som visar parabeln för den flygande färgbollen
	private ArrayList<IGeometry> ballPathShadow; // skuggor till parabelsiktet
	
	//varibles for draw aim path
	Vector3d gravity;
	Vector3d position;
	float zVelocity;
	float timeToLanding;
	float deltaTime;
	float currentTime;
	
	/** constructor Aim */
	public Aim(IGeometry geo, ArrayList<IGeometry> ball, ArrayList<IGeometry> ballShadow) {
		super();
		powerUp = false;
		crosshair = geo;
		setGeometryProperties(crosshair, 4f, new Vector3d(0f, 0f, 2f),	new Rotation(0f, 0f, 0f)); 
		crosshair.setVisible(false);
		
		ballPath = ball;
		ballPathShadow = ballShadow;
		for (int i = 0; i < 10; i++) 
		{
			setGeometryProperties(ballPath.get(i), 0.5f, new Vector3d(-550, -450, 200f), new Rotation(0f, 0f, 0f)); //TODO set tower position values instead of hard code the values here
			setGeometryProperties(ballShadow.get(i), 0.2f, new Vector3d(-550, -450, 0), new Rotation(0f, 0f, 0f));

			ballPath.get(i).setVisible(false);
			ballShadow.get(i).setVisible(false);
		}
		
		
		gravity = new Vector3d(0f, 0f, -9.82f);
		position = new Vector3d(0f, 0f, 0f);
		zVelocity = 0;
		timeToLanding = 0;
		deltaTime = 0;
		currentTime = 0;
	}
	
	/** Function to draw the path of the ball (aim) */
	public void drawBallPath(Vector3d startVelocity, Vector3d startPosition) 
	{ 
		//Vector3d gravity = new Vector3d(0f, 0f, -9.82f);
		//Vector3d position = new Vector3d(0f, 0f, 0f);
		//float zVelocity = startVelocity.getZ();
		zVelocity = startVelocity.getZ();
		//float timeToLanding = (float) (zVelocity / (2 * 9.82f) + Math.sqrt(Math.pow( zVelocity / (2 * 9.82), 2) + startPosition.getZ() / 9.82));
		timeToLanding = (float) (zVelocity / (2 * 9.82f) + Math.sqrt(Math.pow( zVelocity / (2 * 9.82), 2) + startPosition.getZ() / 9.82));
		Log.d(TAG, "time to landing : " + timeToLanding);
		//float deltaTime = timeToLanding/(10);
		deltaTime = timeToLanding/(15);
		Log.d(TAG, "delta time : " + deltaTime);
		currentTime = 0;
		
		if (powerUp)
		{
			position.setX(startPosition.getX()+startVelocity.getX()*timeToLanding+gravity.getX()*timeToLanding*timeToLanding);		
			position.setY(startPosition.getY()+startVelocity.getY()*timeToLanding+gravity.getY()*timeToLanding*timeToLanding);
			position.setZ(startPosition.getZ()+startVelocity.getZ()*timeToLanding+gravity.getZ()*timeToLanding*timeToLanding);
			
			crosshair.setTranslation(new Vector3d( position.getX(), position.getY(), 0f));
			crosshair.setVisible(true);
		}
		else
		{
			for (int i = 0; i < 10; i++) 
			{
				// Calculate the objects position after i timestep
				currentTime += deltaTime;
				position.setX(startPosition.getX()+startVelocity.getX()*currentTime+gravity.getX()*currentTime*currentTime);		
				position.setY(startPosition.getY()+startVelocity.getY()*currentTime+gravity.getY()*currentTime*currentTime);
				position.setZ(startPosition.getZ()+startVelocity.getZ()*currentTime+gravity.getZ()*currentTime*currentTime);
				
				//check for collision with ground
				if (position.getZ()<0f)
				{
				ballPath.get(i).setVisible(false);
				ballPathShadow.get(i).setVisible(false);
				//Log.d(TAG, "boll som försvinner: " + i + " currentTime: " + currentTime + " Z-led: " + position.getZ());
				}
				else
				{
					//Log.d(TAG, "boll som syns: " + i + " currentTime: " + currentTime);
					ballPath.get(i).setTranslation(position);
					ballPathShadow.get(i).setTranslation(new Vector3d(position.getX() , position.getY(),0f));	
				}
			}
		}
	}
	
	public void activate()
	{
		if(powerUp)
    	{
    		crosshair.setVisible(true);
    	}
	    else
	    {                	
			 for(int i = 0; i < 10; i++)
	         {
	         	ballPath.get(i).setVisible(true);
	         	ballPathShadow.get(i).setVisible(true);
	         } 
	    }
	}
	
	public void deactivate()
	{
		if(powerUp)
    	{
    		crosshair.setVisible(false);
    	}
	    else
	    {                	
			 for(int i = 0; i < 10; i++)
	         {
	         	ballPath.get(i).setVisible(false);
	         	ballPathShadow.get(i).setVisible(false);
	         } 
	    }
	}
	
	public boolean getPowerUp()
	{
		if(powerUp == true)
			return true;
		else
			return false;
	}
	
	public void setPowerUp(boolean power)
	{
		if(power == true)
			powerUp = true;
		else
			powerUp =  false;
	}
}

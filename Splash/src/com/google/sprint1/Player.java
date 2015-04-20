package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Player extends Drawable {

	//public String ip;
	private int score;
	
	public IGeometry towerGeometry;
	public IGeometry slingshotGeometry;
	public IGeometry ballGeometry;
	public IGeometry touchSphere;

	public boolean superPower; 
	
	public Player(IGeometry towerGeo,IGeometry slingshotGeo,IGeometry ballGeo, Vector3d startPosPlayer, IGeometry invisibleBall)
	{
		score = 0;
		superPower = false;
		towerGeometry = towerGeo;
		slingshotGeometry = slingshotGeo;
		ballGeometry = ballGeo;
		startPosition = startPosPlayer;
		touchSphere = invisibleBall; 
		
		//setGeometryProperties(towerGeometry, 3f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(towerGeometry, 3f, new Vector3d(startPosition.getX(), startPosition.getY(), 0f), new Rotation(0f, 0f, 0f));
		//setGeometryProperties(slingshotGeometry, 2f, new Vector3d(-685f, -485f, 250f), new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
		setGeometryProperties(slingshotGeometry, 2f, new Vector3d(startPosition.getX()-35f, startPosition.getY() + 35f, startPosition.getZ() -100f), new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
		//setGeometryProperties(ballGeometry, 2f, new Vector3d(-650f, -520f, 350f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(ballGeometry, 2f, startPosition, new Rotation(0f, 0f, 0f));
		//setGeometryProperties(touchSphere, 6f, new Vector3d(-650f, -520f, 250f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(touchSphere, 6f, startPosition, new Rotation(0f, 0f, 0f));
		
		//touchSphere.setTransparency(1);// set to fully transparant
		//touchSphere.setTransparency(0);
		//touchSphere.setVisible(false);
		//touchSphere.setOcclusionMode(true);
		
	}
	
	public void increaseScore()
	{
		score++;
	}
	
	public int getScore()
	{
		return score;
	}

	public Vector3d getPosition()
	{
		return startPosition;
	}
}

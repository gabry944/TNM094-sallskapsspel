package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Player extends Drawable {

	//public String ip;
	//private static int score;
	public int score;
	
	public IGeometry towerGeometry;
	public IGeometry slingshotGeometry;
	public IGeometry ballGeometry;
	public IGeometry marker;
	//private Vector3d startPosition;

	public boolean superPower; 
	
	public Player(IGeometry anthillGeo,IGeometry slingshotGeo,IGeometry ballGeo, Vector3d startPosPlayer, IGeometry Marker)
	{
		score = 0;
		superPower = false;
		towerGeometry = anthillGeo;
		slingshotGeometry = slingshotGeo;
		ballGeometry = ballGeo;
		startPosition = startPosPlayer;
		marker = Marker;

		setGeometryProperties(anthillGeo, 8f, new Vector3d(startPosition.getX(), startPosition.getY(), 0f), new Rotation(0f, 0f, 0f));

		if((startPosPlayer.getX() == -650f && startPosPlayer.getY() == 520f) || (startPosPlayer.getX() == 650f && startPosPlayer.getY() == -520f))
		{
			setGeometryProperties(slingshotGeometry, 2f, new Vector3d(startPosition.getX() + 35f, startPosition.getY() + 35f, startPosition.getZ() -100f), new Rotation((float)Math.PI/2, 0f, -(float)Math.PI/4));
		}
		else
			setGeometryProperties(slingshotGeometry, 2f, new Vector3d(startPosition.getX() - 35f, startPosition.getY() + 35f, startPosition.getZ() -100f), new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
		
		setGeometryProperties(ballGeometry, 4f, startPosition, new Rotation(0f, 0f, 0f));
		//setGeometryProperties(touchSphere, 6f, startPosition, new Rotation(0f, 0f, 0f));
		setGeometryProperties(marker, 0.2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		marker.setVisible(false);
		
	}
	
	//increase the score with points depending on the ant being hit
	public void increaseScore(int points)
	{
		score = score + points;
	}
	
	//return player score
	public int getScore()
	{
		return score;
	}

	//return player tower position
	public Vector3d getPosition()
	{
		return startPosition;
	}
	
	//displays a marker over the ant when being hit
	public void setMarker(Vector3d pos)
	{
		marker.setTranslation(new Vector3d(pos.getX(), pos.getY(), 50f));
		marker.setVisible(true);
		//marker.startAnimation("Take 001", true);
	}
	
	//remove marker over ant when it reached the tower
	public void removeMarker()
	{
		marker.setVisible(false);
		//marker.startAnimation("Take 001", false);
	}
}

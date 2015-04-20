package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class Player extends Drawable {

	//public String ip;
	private static int score;
	
	public IGeometry towerGeometry;
	public IGeometry slingshotGeometry;
	public IGeometry ballGeometry;

	public boolean superPower; 
	/*private Vector3d startPosPlayer1 = new Vector3d(-600f, -450f, 165f);
	private Vector3d startPosPlayer2 = new Vector3d(-600f, 450f, 165f);
	private Vector3d startPosPlayer3 = new Vector3d(600f, -450f, 165f);
	private Vector3d startPosPlayer4 = new Vector3d(600f, 450f, 165f);*/
	
	
	public Player(IGeometry towerGeo,IGeometry slingshotGeo,IGeometry ballGeo, Vector3d startPosPlayer)
	{
		score = 0;
		superPower = false;
		towerGeometry = towerGeo;
		slingshotGeometry = slingshotGeo;
		ballGeometry = ballGeo;
		startPosition = startPosPlayer;
		
		//setGeometryProperties(towerGeometry, 3f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(towerGeometry, 3f, new Vector3d(startPosition.getX() + 35f, startPosition.getY()- 35f, 0f), new Rotation(0f, 0f, 0f));
		//setGeometryProperties(slingshotGeometry, 2f, new Vector3d(-685f, -485f, 250f), new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
		setGeometryProperties(slingshotGeometry, 2f, startPosition, new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
		//setGeometryProperties(ballGeometry, 2f, new Vector3d(-650f, -520f, 350f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(ballGeometry, 2f, new Vector3d(startPosition.getX()+35f, startPosition.getY()- 35f, startPosition.getZ() +100f), new Rotation(0f, 0f, 0f));
		
	}
	
	public static void increaseScore()
	{
		score++;
	}
	
	public static int getScore()
	{
		return score;
	}

	public Vector3d getPosition()
	{
		return startPosition;
	}
	
	/*public Player(int id)
	{
		if (id == 1)
		{
			position = new Vector3d(startPosPlayer1);
		}
		
		if (id == 2)
		{
			position = new Vector3d(startPosPlayer2);
		}
		
		if (id == 3)
		{
			position = new Vector3d(startPosPlayer3);
		}
		
		if (id == 4)
		{
			position = new Vector3d(startPosPlayer4);
		}
			
	}*/

}

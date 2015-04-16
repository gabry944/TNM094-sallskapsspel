package com.google.sprint1;

import com.metaio.sdk.jni.Vector3d;

public class Player {
	
	public int id;

	public String ip;
	private Vector3d position;
	private static int score;

	public boolean superPower; 
	private Vector3d startPosPlayer1 = new Vector3d(-600f, -450f, 165f);
	private Vector3d startPosPlayer2 = new Vector3d(-600f, 450f, 165f);
	private Vector3d startPosPlayer3 = new Vector3d(600f, -450f, 165f);
	private Vector3d startPosPlayer4 = new Vector3d(600f, 450f, 165f);
	
	
	public Player()
	{
		id = 0;		//id of player 1-4
		position = new Vector3d(0f, 0f, 0f);	//position of canon
		score = 0;
		superPower = false;
	}
	
	public static void setScore()
	{
		score++;
	}
	
	public static int getScore()
	{
		return score;
	}

	public Vector3d getPosition()
	{
		return position;
	}
	
	public Player(int id)
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
			
	}

}

package com.google.sprint1;

import android.util.Log;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
/**Class for all the ants */
public class Ant extends Drawable
{
	public static final String TAG = "Ant";
	public static final int SMALL_ANT = 1;
	public static final int BIG_ANT = 2;
	public static final int GIANT_ANT = 3;
	
	private static int numberOfAnts;
	
	private IGeometry ant;
	private boolean isHit;
	private boolean isReached;
	private boolean collision;
	private Vector3d diffVec;
	private float memory;
	private int id;
	private int type;
	private int ownedByPlayer;
	private IGeometry markerBlue;
	private IGeometry markerGreen;
	private IGeometry markerRed;
	private IGeometry markerYellow;
	
	private final MobileConnection connection;
	
	float angDiffLimit = (float)(5*Math.PI/180);
	float speed = 2f;
	float fastSpeed = 3f;
	float angle = 0;
	float randNr = 0;
	int k = 0;
	
	/** constructor ant */
	public Ant(IGeometry geo, int antType, IGeometry markBlue, IGeometry markGreen, IGeometry markRed, IGeometry markYellow) {
		super();
		id = numberOfAnts;
		numberOfAnts++;
		
		connection = NetworkState.getState().getMobileConnection();
		
		markerBlue = markBlue;
		markerGreen = markGreen;
		markerRed = markRed;
		markerYellow = markYellow;
		
		setGeometryProperties(markerBlue, 0.3f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(markerGreen, 0.3f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(markerRed, 0.3f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		setGeometryProperties(markerYellow, 0.3f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
		
		markerBlue.setVisible(false);
		markerGreen.setVisible(false);
		markerRed.setVisible(false);
		markerYellow.setVisible(false);
		
		ant = geo;
		type = antType;		//1 = small ant, 2 = big ant, 3 = giant ant
		ownedByPlayer = -1;	
		isHit = false;
		isReached = false;
		collision = false;
		setGeometryProperties(ant, 30f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f)); 
		
		if (type == BIG_ANT)
			ant.setScale(50f);
		else if(type == GIANT_ANT)
			ant.setScale(70f);
		
		ant.setVisible(false);
		diffVec = new Vector3d(0f, 0f, 0f);
		memory = 0f;
	}
	 
	/** return the type of the ant */
	public int getType()
	{
		return type;
	}
	
	/** return which player the ant has been hit by */
	public int getOwnedByPlayer()
	{
		return ownedByPlayer;
	}
	
	/** set which player hit the ant */
	public void setOwnedByPlayer(int id)
	{
		ownedByPlayer = id;
	}
	
	/** return ant geometry */
	public IGeometry getGeometry()
	{
		return ant;
	}
	
	/** return if the ant is hit */
	public boolean getIsHit()
	{
		if(isHit == true)
			return true;
		else
			return false;
	}
	
	/** set the ant to hit */
	public void setIsHit(boolean hit, int id)
	{
		if(hit == true)
		{
			setOwnedByPlayer(id);
			ant.startAnimation("Take 001", false);
			isHit = true;
			collision = true;
		}
		else
		{
			isHit =  false;
			setOwnedByPlayer(id);
		}
	}
	
	/** function to spawn the ants at random time and random position */
	public void spawnAnt()
	{
		//ant.startAnimation("Take 001", true);
		if(randBetween(1, 100) == 10)
		{
			//spawn ant at random
			ant.setVisible(true);
			ant.setTranslation(new Vector3d(randBetween(-600 , 600), randBetween(-600 , 600), 0f));
			ant.setRotation(new Rotation(randBetween(0f , 6.28f), randBetween(0f , 6.28f), 0));	//(float)(Math.PI*3/2)
		}
		
	}
	
	/** Update function that is allways called and therefor updated in real time */
	public void update()
	{
		if(!isActive())
		{
			// if not already spawned, spawn at random 
			spawnAnt();
		}
		else{
			if (isHit){ 
				//if ant is hit move to tower else move at random 
				movementToTower(GameState.getState().players.get(ownedByPlayer).getPosition());
			}else{
				randomMovement(); 
			}
			
		}
	}
	/** Function to generate movement to the ants */
	public void randomMovement()
	{		
		//if first time called
		if(k == 0)
		{
			float firstRand = randBetween2(angDiffLimit);
			angle = ant.getRotation().getEulerAngleRadians().getZ() + firstRand;
			memory = firstRand;
			k = 1;
		}
		
		//ant gets a new angle at random
		if(randBetween(1, 30) == 1)
		{
			randNr = randBetween2(angDiffLimit);
			angle = ant.getRotation().getEulerAngleRadians().getZ() + randNr;
			memory = randNr;
		}
		else 
		{
			angle = angle + memory;   // keep turning the same way as before 
		}
		
		float diffX = (float)Math.cos(angle);
		
		float diffY = (float)Math.sin(angle);
		
		Vector3d movement = new Vector3d(new Vector3d(ant.getTranslation().getX() + speed * diffX,
													  ant.getTranslation().getY() +  speed * diffY,
													  0f));
		
		//random movement of the ant until being hit 
		ant.setTranslation(movement);
		ant.setRotation(new Rotation((float)(Math.PI/2), 0f, angle ));  

	}	
	
	/** Makes the ant go to the tower owned by the player who hit the ant */
	public void movementToTower(Vector3d pos)
	{
		//pos.setZ(0f);
		setMarker(getOwnedByPlayer());
		diffVec = ant.getTranslation().subtract(pos);
		diffVec.setZ(0f);
		isReached = false;
		
		// check since atan(y/x) == atan(-y/-x)
		if(diffVec.getX() < 0f)
			angle = (float)(Math.atan(diffVec.getY()/diffVec.getX()));
		else
			angle = (float)(Math.atan(diffVec.getY()/diffVec.getX()) + Math.PI);
		
		ant.setRotation( new Rotation((float)(Math.PI/2), 0f, angle)); //(float)(Math.PI*3/2) 
		ant.setTranslation(ant.getTranslation().subtract((diffVec.getNormalized()).multiply(fastSpeed)));
		
		
		//when ant reached tower
		if(diffVec.getX() < 2f && diffVec.getX() > -2f  && diffVec.getY() < 2f && diffVec.getY() > -2f)
		{
			int points = getType();
			isReached = true;
			
			//animate anthill
			GameState.getState().players.get(ownedByPlayer).playAnthillAnimation();
			
			GameState.getState().players.get(ownedByPlayer).increaseScore(points);
			connection.sendData(NetDataHandler.antReachedTower(getId(), ownedByPlayer));
			Log.d(TAG, "Ant  " + getId() +" reached player " + ownedByPlayer);
			setActive(false);
			//player.point();
						
			
		}
	}
	
	/** Return true if ant reached tower*/
	public boolean getTowerIsReached()
	{
		if(isReached == true){
			isReached = false;
			return true;
		}
		else
			return false;
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
	
	/** displays a marker over the ant when being hit */
	public void setMarker(int i)
	{
		
		Vector3d pos = ant.getTranslation();
		
		if(i == 0)
		{
			markerBlue.setTranslation(new Vector3d(pos.getX(), pos.getY(), 50f));
			markerBlue.setVisible(true);
		}
		else if(i == 1)
		{
			markerGreen.setTranslation(new Vector3d(pos.getX(), pos.getY(), 50f));
			markerGreen.setVisible(true);
		}
		else if(i == 2)
		{
			markerRed.setTranslation(new Vector3d(pos.getX(), pos.getY(), 50f));
			markerRed.setVisible(true);
		}
		else if(i == 3)
		{
			markerYellow.setTranslation(new Vector3d(pos.getX(), pos.getY(), 50f));
			markerYellow.setVisible(true);
		}
		
	}
	
	
	/** function to set position for ant */
	public void setPosition(Vector3d pos)
	{
		ant.setTranslation(pos);
		setMarker(getOwnedByPlayer());
	}
	
	/** function to get ants position */
	public Vector3d getPosition()
	{
		return ant.getTranslation();
	}
	
	/** function to get ants rotation */
	public Rotation getRotation()
	{
		return ant.getRotation();
	}
	
	/** function to set rotation for ant */
	public void setRotation(Rotation rot)
	{
		ant.setRotation(rot);
	}
		
	/** returns the ants id */
	public int getId()
	{
		return id;
	}
	
	/** function to see if ant is active = visible */
	public boolean isActive()
	{
		//is the ant already spawned
		if(ant.isVisible())
			return true;
		else
			return false;
	}
	
	/** Function to set if the ant is active or not */
	public void setActive(boolean active)
	{
		ant.setVisible(active);
		if (!active)
			setIsHit(false, -1);
	}
	
	/** calculate a random number between arg start and arg end */
	public static float randBetween(float start, float end)
	{
		return (float)(start + (int)Math.round(Math.random()* (end - start)));
	}
	
	public static float randBetween2(float end)
	{
		return (float)(Math.random()* (2*end) - end);
	}

}



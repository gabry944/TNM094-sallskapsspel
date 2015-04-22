package com.google.sprint1;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.sprint1.NetworkService.LocalBinder;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.GestureHandlerAndroid;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.GestureHandler;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

/**
 * GameActivity to handle the game
 * 
 */

public class GameActivity extends ARViewActivity // implements
													// OnGesturePerformedListener
{
	public final static int NUM_OF_ANTS = 7;
	
	/* Variables for objects in the game */

	public Player player;
	public Player bluePlayer;
	public Player greenPlayer;
	public Player redPlayer;
	public Player yellowPlayer;


	GameState gameState;

	Ant giantAnt;
	Ant ant;
	Ant bigAnt;
	Aim aim;


	private IGeometry ball;
	private IGeometry ballShadow;
	//private IGeometry touchSphere;

	private IGeometry groundPlane;
	
	//Gesture handler
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;
		
	private double angleForCanon;
	
	private Vector3d touchVec; // Difference between ball and tower when shooting

	// point count
	protected int point;
	TextView displayPoints;

	// Variables for Service handling
	private NetworkService mService;
	boolean mBound = false;

	// FPS specific variables
	//private int frameCounter = 0;
	//private double lastTime;

	public static final String TAG = "GameActivity";

	protected void onDestroy() {
		Log.d(TAG, "i onDestroy i GameActivity");
		// Unbind from service
		if (mBound) {
			unbindService(mServiceConnection);
			mBound = false;
		}
		super.onDestroy();
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		//Create and show a DialogFragment with How-to-play instructions
		FragmentManager fm = getSupportFragmentManager();		
		InstructionsDialog dFragment = new InstructionsDialog();
		// Show DialogFragment
		dFragment.show(fm, "Dialog Fragment");

	}

	/** Attaching layout to the activity */
	@Override
	public int getGUILayout() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return R.layout.activity_game;
	}

	@Override		
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		
		// Bind to NetworkService
		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		GameState.getState().exsisting_paint_balls = new ArrayList<PaintBall>(20);
		GameState.getState().ants = new ArrayList<Ant>(NUM_OF_ANTS);
		GameState.getState().bigAnts = new ArrayList<Ant>(NUM_OF_ANTS);
		GameState.getState().giantAnts = new ArrayList<Ant>(1);
		

		touchVec = new Vector3d(0f, 0f, 0f);
		
		angleForCanon = Math.PI/6;
		
		//player = GameState.getState().players.get(0);
		

		GameState.getState().gameStartTime = System.currentTimeMillis();
		
		//Gesture handler
		mGestureMask = GestureHandler.GESTURE_DRAG;
		mGestureHandler = new GestureHandlerAndroid(metaioSDK, mGestureMask);		

	}

	/** Called when the user clicks the Exit button (krysset) */
	public void onExitButtonClick(View v) {
		finish();
	}


	/**
	 * Create a geometry, the string input gives the filepach (relative from the
	 * asset folder) to the geometry First check if model is found ->Load and
	 * create 3D geometry ->check if model was loaded successfully Returns the
	 * loaded model if success, otherwise null
	 */
	private IGeometry Load3Dmodel(String filePath) {
		// Getting the full file path for a 3D geometry
		final File modelPath = AssetsManager.getAssetPathAsFile(
				getApplicationContext(), filePath);
		// First check if model is found
		if (modelPath != null) {
			// Load and create 3D geometry
			IGeometry model = metaioSDK.createGeometry(modelPath);

			// check if model was loaded successfully
			if (model != null)
				return model;
			else
				MetaioDebug.log(Log.ERROR, "Error loading geometry: "
						+ modelPath);
		} else
			MetaioDebug.log(Log.ERROR, "Could not find 3D model");

		return null;
	}

	/** Loads the marker and the 3D-models to the game */
	@Override
	protected void loadContents() 
	{
		try {
			/** Load Marker */
			// Getting a file path for tracking configuration XML file
			File trackingConfigFile = AssetsManager.getAssetPathAsFile(
					getApplicationContext(), "marker/TrackingData_Marker.xml");

			// Assigning tracking configuration
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile);

			MetaioDebug.log("Tracking data loaded: " + result);

			/** Load Object */
			
			//create ground plane
			
			groundPlane = Load3Dmodel("groundPlane/plane2.mfbx");
			geometryProperties(groundPlane, 25f, new Vector3d(0f, 0f, -15f), new Rotation(0f, 0f, 0f));
			
			//creates the tower	
			
			bluePlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotBlue.mfbx"), Load3Dmodel("paintball/paintball/ballBlue.mfbx"), new Vector3d(-650f, -520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));
			greenPlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotGreen.mfbx"), Load3Dmodel("paintball/paintball/ballGreen.mfbx"), new Vector3d(650f, 520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));	
			redPlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotRed.mfbx"), Load3Dmodel("paintball/paintball/ballRed.mfbx"), new Vector3d(-650f, 520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));
			yellowPlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotYellow.mfbx"), Load3Dmodel("paintball/paintball/ballYellow.mfbx"), new Vector3d(650f, -520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));
			
			//! TODO make sure that init is called!
			GameState.getState().addPlayer(bluePlayer);	
			GameState.getState().addPlayer(greenPlayer);
			GameState.getState().addPlayer(redPlayer);
			GameState.getState().addPlayer(yellowPlayer);
			
			// the player this unit has is decided by the player id in game state
			player = GameState.getState().players.get(GameState.getState().myPlayerID);
			mGestureHandler.addObject(player.touchSphere, 1);

			// Load powerUps
			PowerUp power = new PowerUp(Load3Dmodel("powerUps/aimPowerUp.mfbx"));
			GameState.getState().powerUps.add(power);
			
			// creates the aim path
			ArrayList<IGeometry> ballPath = new ArrayList<IGeometry>(10);
			ArrayList<IGeometry> ballPathShadow = new ArrayList<IGeometry>(10);			
			for (int i = 0; i < 10; i++) 
			{
				ball = Load3Dmodel("paintball/paintball/ballBlue.mfbx");
				ballShadow = Load3Dmodel("paintball/paintballShadow.mfbx");
				ballPath.add(ball);
				ballPathShadow.add(ballShadow);
			}
			
			// Load aim (crosshair and ballPath)			
			aim = new Aim(Load3Dmodel("crosshair/crosshair.mfbx"),ballPath,ballPathShadow);
			

			// creates a list of ants 
			for(int i = 0; i < NUM_OF_ANTS; i++)
			{
				// create ant geometry
				ant = new Ant(i, Load3Dmodel("ant/smallAnt/ant.mfbx"), Load3Dmodel("ant/markers/boxBlue.mfbx"), 1);
				GameState.getState().ants.add(ant);
				bigAnt = new Ant(i, Load3Dmodel("ant/bigAnt/ant.mfbx"), Load3Dmodel("ant/markers/boxBlue.mfbx"), 2);
				GameState.getState().bigAnts.add(bigAnt);
				GameState.getState().bigAnts.get(i).bigAnt();
			}
			giantAnt = new Ant(0, Load3Dmodel("ant/giantAnt/ant.mfbx"), Load3Dmodel("ant/markers/boxBlue.mfbx"), 3);
			GameState.getState().giantAnts.add(giantAnt);
			GameState.getState().giantAnts.get(0).giantAnt();
			
			// creates a list of paint blue balls that player 0 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().exsisting_paint_balls.add(
						new PaintBall(i,Load3Dmodel("paintball/paintball/ballBlue.mfbx"),
									  Load3Dmodel("paintball/splash/splashBlue.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx")));
			}
			// creates a list of paint blue balls that player 1 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().exsisting_paint_balls.add(
						new PaintBall(i,Load3Dmodel("paintball/paintball/ballGreen.mfbx"),
									  Load3Dmodel("paintball/splash/splashGreeen.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx")));
			}
			// creates a list of paint blue balls that player 2 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().exsisting_paint_balls.add(
						new PaintBall(i,Load3Dmodel("paintball/paintball/ballRed.mfbx"),
									  Load3Dmodel("paintball/splash/splashRed.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx")));
			}
			// creates a list of paint blue balls that player 3 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().exsisting_paint_balls.add(
						new PaintBall(i,Load3Dmodel("paintball/paintball/ballYellow.mfbx"),
									  Load3Dmodel("paintball/splash/splashYellow.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx")));
			}
			
		} 
		catch (Exception e) 
		{
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}

	// function to set the properties for the geometry
	public void geometryProperties(IGeometry geometry, float scale,
				Vector3d translationVec, Rotation rotation) 
	{
		geometry.setScale(scale);
		geometry.setTranslation(translationVec, true);
		geometry.setRotation(rotation, true);
	}

	/** Render Loop */
	@Override
	public void onDrawFrame() {
		super.onDrawFrame();

		// If content not loaded yet, do nothing
		if ( GameState.getState().exsisting_paint_balls.isEmpty())
			return;

		if(GameState.getState().powerUps.get(0).isHit())
			aim.setPowerUp(true);
		//spawn ant at random and move ants
		for ( int i = 0; i < NUM_OF_ANTS ; i++)
		{
			if(!GameState.getState().ants.get(i).isActive())
			{
				// if not already spawned, spawn at random 
				GameState.getState().ants.get(i).spawnAnt();
			}
			
			if(!GameState.getState().bigAnts.get(i).isActive())

			{
				// if not already spawned, spawn at random 
				GameState.getState().bigAnts.get(i).spawnAnt();
			}
			
			//if ant is hit move to tower else move at random 
			if(GameState.getState().ants.get(i).getIsHit() == true)
			{
				GameState.getState().ants.get(i).movementToTower(new Vector3d(player.getPosition()));
			}
			else
				GameState.getState().ants.get(i).randomMovement();
			
			if(GameState.getState().bigAnts.get(i).getIsHit() == true)
			{
				GameState.getState().bigAnts.get(i).movementToTower(new Vector3d(player.getPosition()));
			}
			else
				GameState.getState().bigAnts.get(i).randomMovement();
					

			
			//Allocate a buffer and add OC and a byte array.
    		ByteBuffer buffer = ByteBuffer.allocate(6 + 4*4);
    		//amount of bytes
    		buffer.putInt(4*4);
    		//operation code
    		buffer.putChar(DataPackage.ANT);
    		//data 
    		buffer.putInt(GameState.getState().ants.get(i).getId());
    		buffer.putFloat(GameState.getState().ants.get(i).getPosition().getX());
    		buffer.putFloat(GameState.getState().ants.get(i).getPosition().getY());
    		buffer.putFloat(GameState.getState().ants.get(i).getPosition().getZ());
    		
			//mService.mConnection.sendData(buffer.array());
		}
		
		//for the one giant Ant
		if(!GameState.getState().giantAnts.get(0).isActive())

		{
			// if not already spawned, spawn at random 
			GameState.getState().giantAnts.get(0).spawnAnt();
		}
		
		if(GameState.getState().giantAnts.get(0).getIsHit() == true)
		{
			GameState.getState().giantAnts.get(0).movementToTower(new Vector3d(player.getPosition()));
		}
		else
			GameState.getState().giantAnts.get(0).randomMovement();
				
		//Update powerup(s)
		for (int i = 0; i < GameState.getState().powerUps.size(); i++)
		{
			GameState.getState().powerUps.get(i).update();
		}

		//Update Paintballs
		if (!GameState.getState().exsisting_paint_balls.isEmpty()) {
			for (PaintBall obj : GameState.getState().exsisting_paint_balls) {
				if (obj.isActive()) {
					obj.update();
				}
			}
		}
		
		//Update Gametime 
		GameState.getState().updateTime();
		if (GameState.getState().gameTimeLeft <= 0) 
		{
			Intent GameEnded = new Intent(this, GameEndedActivity.class);
			startActivity(GameEnded);
		}
		
		showScore();
		//updateFps();
	}

	/** function that activates when an object is being touched */
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// Only implemented because its required by the parent class
	}

    private PaintBall getAvailableBall(int id)
    {
    	PaintBall ball;
    	for(int i=id*5; i < (id+1)*5;i++)
    	{
    		ball = GameState.getState().exsisting_paint_balls.get(i);
    		if (!(ball.getGeometry().isVisible()))
    			return ball;
    	}
    	
    	return null;
    }
    
	/** Pause function, makes you return to the main menu when pressing "back" */
	@Override
	public void onPause() {
		super.onPause();
		// creates a fade between scenes
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//Gesture handler
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		super.onTouch(v, event);

		mGestureHandler.onTouch(v, event);

    	//coordinates between tower and "slangbella"
		player.ballGeometry.setTranslation(player.touchSphere.getTranslation());
		
		touchVec = new Vector3d(-(player.ballGeometry.getTranslation().getX()-player.towerGeometry.getTranslation().getX()),
									-(player.ballGeometry.getTranslation().getY()-player.towerGeometry.getTranslation().getY()),
									0f);   
		

		// Math.sin(Math.PI/6) angle PI/6 = 30' => sin(pi/6) = 0.5 && Math.cos(Math.PI/6) angle PI/6 = 30' => cos(pi/6) = 0.5
		Vector3d vel = new Vector3d((float)(touchVec.getX()/3* Math.cos(angleForCanon)), 
									(float)(touchVec.getY()/3* Math.cos(angleForCanon)),
									(float)(Math.abs(touchVec.getX()/5)* Math.sin(angleForCanon)+ Math.abs(touchVec.getY()/5)*Math.sin(angleForCanon)));
			

		//aim.drawBallPath(vel, player.getPosition()); 
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:                
                if(player.superPower == true)
                		aim.setPowerUp(true);
                
                aim.activate();
                break;
            case MotionEvent.ACTION_MOVE:    
            	
            	//don't draw path if touched outside touchSphere
            	if(!(Math.abs(touchVec.getX()) < 0.1f))
            	{
	            	aim.drawBallPath(vel, player.getPosition()); 	            		
            	}
            	
            	player.ballGeometry.setTranslation(player.touchSphere.getTranslation());
                break;
            case MotionEvent.ACTION_UP:
            	aim.deactivate();
            	
            	// move slingshot to original position
        		player.ballGeometry.setTranslation(player.towerGeometry.getTranslation());
        		player.ballGeometry.setTranslation(new Vector3d(0f, 0f, 350f), true);
        		player.touchSphere.setTranslation(player.ballGeometry.getTranslation());
        		
            	PaintBall ball = getAvailableBall(GameState.getState().myPlayerID);
        		if(ball != null)
        		{
            		Vector3d pos = player.getPosition();
            		
        			
        			//check if touched outside sphere -> do nothing
        			if(!(Math.abs(touchVec.getX()) < 0.1f))
        			{
        				//Allocate a buffer and add OC and a byte array.
                		ByteBuffer buffer = ByteBuffer.allocate(6 + 4*7);
                		//amount of bytes
                		buffer.putInt(7*4);
                		//operation code
                		buffer.putChar(DataPackage.BALL_FIRED);
                		//data 
                		buffer.putInt(ball.getId());
                		buffer.putFloat(vel.getX());
                		buffer.putFloat(vel.getY());
                		buffer.putFloat(vel.getZ());
                		buffer.putFloat(pos.getX());
                		buffer.putFloat(pos.getY());
                		buffer.putFloat(pos.getZ());
                		
            			mService.mConnection.sendData(buffer.array());
            			ball.fire(vel, pos); 
        			}
        		}
        		break;
        }        
		return true;
	}
	
	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() 
	{
		return null;
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
	
	private void showScore()
	{
		//Necessary to run on UI thread to be able to edit the TextView
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView displayPoints = (TextView) findViewById(R.id.myPoints);
				displayPoints.setText("Score: " + player.getScore());
				
				TextView displayTime= (TextView) findViewById(R.id.timeLeft);
				displayTime.setText(GameState.getState().timeToString());
			}
		});
	}
	
	public static int getNrOfAnts()
	{
		return NUM_OF_ANTS;
	}
		
	/**Updates Fps each frame and display it to user once every second*/ 
//	private void updateFps() {
//		
//		//Adds one each frame
//		frameCounter++;
//		
//		//Uses internal clock to calculate the difference between current time and last time
//		double currentTime = System.currentTimeMillis() - lastTime;
//		//calculates the fps (*1000 due to milliseconds)
//		final int fps = (int) (((double) frameCounter / currentTime) * 1000);
//		
//		//Only displays if current time is over one second
//		if (currentTime > 1.0) {
//			lastTime = System.currentTimeMillis();
//			frameCounter = 0;
//			
//			//Necessary to run on UI thread to be able to edit the TextView
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					TextView displayPoints = (TextView) findViewById(R.id.myPoints);
//					displayPoints.setText("FPS: " + fps);
//				}
//			});
//		}
//	}
}
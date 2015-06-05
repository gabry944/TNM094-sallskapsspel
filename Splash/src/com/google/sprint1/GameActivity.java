package com.google.sprint1;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.GestureHandlerAndroid;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.ELIGHT_TYPE;
import com.metaio.sdk.jni.GestureHandler;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.ILight;
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
	// Number of small ants - big ants - giant ants
	public final static int NUM_OF_ANTS[] = {7, 4, 1};
	
	/* Variables for objects in the game */

	public Player player;
	public Player bluePlayer;
	public Player greenPlayer;
	public Player redPlayer;
	public Player yellowPlayer;

	private int currentBall = 0;
	GameState gameState;

	Aim aim;

	boolean gametimeSet =false;
	
	private ILight mSpotLight;

	private IGeometry ball;
	private IGeometry ballShadow;
	//private IGeometry touchSphere;

	//private IGeometry groundPlane;
	
	//Gesture handler
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;
		
	private double angleForCanon;
	
	private Vector3d touchVec; // Difference between ball and tower when shooting

	// point count
	protected int point;
	TextView displayPoints;

	// FPS specific variables
	//private int frameCounter = 0;
	//private double lastTime;

	public static final String TAG = "GameActivity";
	public static Activity fa;

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
		
		fa = this;
		
		GameState.getState().paintBalls = new ArrayList<PaintBall>(20);
		GameState.getState().ants = new ArrayList<Ant>(NUM_OF_ANTS[0] + NUM_OF_ANTS[1] + NUM_OF_ANTS[2]);
		

		touchVec = new Vector3d(0f, 0f, 0f);
		
		angleForCanon = Math.PI/6;
		
		//player = GameState.getState().players.get(0);
		

		GameState.getState().gameStartTime = System.currentTimeMillis();
		
		//Gesture handler
		mGestureMask = GestureHandler.GESTURE_DRAG;
		mGestureHandler = new GestureHandlerAndroid(metaioSDK, mGestureMask);
		
		
	}

	/**
	 *  Called when the user clicks the back arrow button 
	 */
	public void stopButton(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
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

			GameState.getState().nrOfPlayers = 1 + NetworkState.getState().getMobileConnection().getNumberOfConnections();
			
			// create light



			mSpotLight = metaioSDK.createLight();
			mSpotLight.setAmbientColor(new Vector3d(0.5f, 0.5f, 0.5f)); // slightly red ambient
			mSpotLight.setType(ELIGHT_TYPE.ELIGHT_TYPE_DIRECTIONAL);
			mSpotLight.setDirection(new Vector3d(0.2f, -0.7f, 0.2f));
			mSpotLight.setDiffuseColor(new Vector3d(1, 1, 1f)); // yellow
			mSpotLight.setCoordinateSystemID(1);
			//mSpotLight.setTranslation(new Vector3d(0f,500f,0f));

			/** Load Object */
			
			//create ground plane			
//			groundPlane = Load3Dmodel("groundPlane/grassplane2.mfbx");
//			geometryProperties(groundPlane, 25f, new Vector3d(0f, 0f, -15f), new Rotation(0f, 0f, 0f));
			
			//creates the tower				
			bluePlayer = new Player(Load3Dmodel("anthill/anthill.mfbx"), Load3Dmodel("tower/slingshotBlue.mfbx"), Load3Dmodel("paintball/paintball/ballBlue.mfbx"), new Vector3d(-650f, -520f, 220f));
			greenPlayer = new Player(Load3Dmodel("anthill/anthill.mfbx"), Load3Dmodel("tower/slingshotGreen.mfbx"), Load3Dmodel("paintball/paintball/ballGreen.mfbx"), new Vector3d(650f, 520f, 220f));	
			redPlayer = new Player(Load3Dmodel("anthill/anthill.mfbx"), Load3Dmodel("tower/slingshotRed.mfbx"), Load3Dmodel("paintball/paintball/ballRed.mfbx"), new Vector3d(-650f, 520f, 220f));
			yellowPlayer = new Player(Load3Dmodel("anthill/anthill.mfbx"), Load3Dmodel("tower/slingshotYellow.mfbx"), Load3Dmodel("paintball/paintball/ballYellow.mfbx"), new Vector3d(650f, -520f, 220f));
			
			GameState.getState().addPlayer(bluePlayer);	
			GameState.getState().addPlayer(greenPlayer);
			GameState.getState().addPlayer(redPlayer);
			GameState.getState().addPlayer(yellowPlayer);
			
			// the player this unit has is decided by the player id in game state
			player = GameState.getState().players.get(GameState.getState().myPlayerID);
			player.ballGeometry.setVisible(true);
			mGestureHandler.addObject(player.ballGeometry, 1);

			// Load powerUps
			PowerUp power = new PowerUp(Load3Dmodel("powerUps/aniPowerUp.mfbx"));
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
			Ant ant;
			//Small Ants
			for(int i = 0; i < NUM_OF_ANTS[0]; i++)
			{
				// create ant geometry
				ant = new Ant(Load3Dmodel("ant/smallAnt/testAnt.mfbx"), Ant.SMALL_ANT, Load3Dmodel("ant/markers/boxBlue.mfbx"), Load3Dmodel("ant/markers/boxGreen.mfbx"), Load3Dmodel("ant/markers/boxRed.mfbx"), Load3Dmodel("ant/markers/boxYellow.mfbx"));
				GameState.getState().ants.add(ant);
				
			}
			//Big ants
			for(int i = 0; i< NUM_OF_ANTS[1]; i++)
			{
				ant = new Ant(Load3Dmodel("ant/bigAnt/testAnt.mfbx"), Ant.BIG_ANT, Load3Dmodel("ant/markers/boxBlue.mfbx"), Load3Dmodel("ant/markers/boxGreen.mfbx"), Load3Dmodel("ant/markers/boxRed.mfbx"), Load3Dmodel("ant/markers/boxYellow.mfbx"));
				GameState.getState().ants.add(ant);	
			}
			//Giant ants
			for(int i = 0; i < NUM_OF_ANTS[2]; i++)
			{
				ant = new Ant(Load3Dmodel("ant/giantAnt/testAnt.mfbx"), Ant.GIANT_ANT, Load3Dmodel("ant/markers/boxBlue.mfbx"), Load3Dmodel("ant/markers/boxGreen.mfbx"), Load3Dmodel("ant/markers/boxRed.mfbx"), Load3Dmodel("ant/markers/boxYellow.mfbx"));
				GameState.getState().ants.add(ant);	
			}
			
			// creates a list of paint blue balls that player 0 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().paintBalls.add(
						new PaintBall(Load3Dmodel("paintball/paintball/ballBlue.mfbx"),
									  Load3Dmodel("paintball/splash/splashBlue.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx"), 0));
			}
			// creates a list of paint blue balls that player 1 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().paintBalls.add(
						new PaintBall(Load3Dmodel("paintball/paintball/ballGreen.mfbx"),
									  Load3Dmodel("paintball/splash/splashGreen.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx"), 1));
			}
			// creates a list of paint blue balls that player 2 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().paintBalls.add(
						new PaintBall(Load3Dmodel("paintball/paintball/ballRed.mfbx"),
									  Load3Dmodel("paintball/splash/splashRed.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx"), 2));
			}
			// creates a list of paint blue balls that player 3 shoots
			for (int i = 0; i < 5; i++) {
				// add paint ball to list of paint balls
				GameState.getState().paintBalls.add(
						new PaintBall(Load3Dmodel("paintball/paintball/ballYellow.mfbx"),
									  Load3Dmodel("paintball/splash/splashYellow.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx"), 3));
			}
	
			//ALL RESOURCES LOADED - PLAYER IS READY TO START GAME
			
			NetworkState.getState().getMobileConnection().sendData(NetDataHandler.playerReady(GameState.getState().myPlayerID));
			GameState.getState().playersReady++;
			
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

		// If all players are not ready, do nothing
		if ( GameState.getState().playersReady < GameState.getState().nrOfPlayers)
			return;

		if (gametimeSet)
		{
			GameState.getState().gameStartTime = System.currentTimeMillis();
			gametimeSet = true;
		}
		
		if(GameState.getState().powerUps.get(0).isHit()) 
		aim.setPowerUp(true);
		
		//if(GameState.getState().powerUps.get(0).getCollision())
		//	SoundEffect.playSound(getBaseContext());
		

		//spawn ant at random and move ants
		if(GameState.getState().myPlayerID == 0)
		{
			Ant ant;
			for ( int i = 0; i <  GameState.getState().ants.size(); i++)
			{
				ant = GameState.getState().ants.get(i); 
				//paintBall = GameState.getState().paintBall; 
				ant.update();
				
				if(ant.getCollision()){
					SoundEffect.playSound(getBaseContext(), 1);
				}
				/*else if(ant.getTowerIsReached()){
					SoundEffect.playSound(getBaseContext());
				}*/
				
				//send position and rotation to other players
				//TODO: Move to Ant class?
				if(ant.isActive())
				{
					NetworkState.getState().getMobileConnection().sendData(NetDataHandler.antPos(ant.getId(),
																		ant.getPosition(),
																		ant.getRotation()));
				}
			}

		}


		//Update powerup(s)
		for (int i = 0; i < GameState.getState().powerUps.size(); i++)
		{
			GameState.getState().powerUps.get(i).update();
		}

		//Update Paintballs
		if (!GameState.getState().paintBalls.isEmpty()) {
			for (PaintBall obj : GameState.getState().paintBalls) {
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

	/** Returns if there is an available ball */
    private PaintBall getAvailableBall(int id)
    {
    	PaintBall ball;

		currentBall = (currentBall + 1) % 5;
		Log.d("Ball", "CurrentBall "+ currentBall);
		
		ball = GameState.getState().paintBalls.get(currentBall + id*5);
		if (!(ball.getGeometry().isVisible()))
			return ball;
	
    	
    	return null;
    }
    
	/** Pause function, makes you return to the main menu when pressing "back" */
	@Override
	public void onPause() {
		super.onPause();
		// creates a fade between scenes
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	/** Function to handle all the gestures on the screen */
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		super.onTouch(v, event);

		mGestureHandler.onTouch(v, event);
		

    	//coordinates between tower and "slangbella"
		//player.ballGeometry.setTranslation(player.touchSphere.getTranslation());
		
		touchVec.setX(-(player.ballGeometry.getTranslation().getX()-player.towerGeometry.getTranslation().getX()));
		touchVec.setY(-(player.ballGeometry.getTranslation().getY()-player.towerGeometry.getTranslation().getY()));
		touchVec.setZ(0f);   
		

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
            	
            	//player.ballGeometry.setTranslation(player.touchSphere.getTranslation());
                break;
            case MotionEvent.ACTION_UP:
            	aim.deactivate();
            	
            	// move slingshot to original position
        		player.ballGeometry.setTranslation(player.getPosition());
        		
            	PaintBall ball = getAvailableBall(GameState.getState().myPlayerID);
        		if(ball != null)
        		{
            		Vector3d pos = player.getPosition();            		
        			
        			//check if touched outside sphere -> do nothing
        			if(!(Math.abs(touchVec.getX()) < 0.1f))
        			{
                		//Send it off to the network
        				NetworkState.getState().getMobileConnection().sendData(NetDataHandler.ballFired(ball.getId(), vel, pos));
            		
            			//Fire the ball locally
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
	
	private void showScore()
	{
		//Necessary to run on UI thread to be able to edit the TextView
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				//Display all the players score:
				TextView displayBluePoints = (TextView) findViewById(R.id.bluePoints);
				displayBluePoints.setText(""+ GameState.getState().players.get(0).score);
				
				TextView displayGreenPoints = (TextView) findViewById(R.id.greenPoints);
				displayGreenPoints.setText(""+ GameState.getState().players.get(1).score);
				
				TextView displayRedPoints = (TextView) findViewById(R.id.redPoints);
				displayRedPoints.setText(""+ GameState.getState().players.get(2).score);
				
				TextView displayYellowPoints = (TextView) findViewById(R.id.yellowPoints);
				displayYellowPoints.setText(""+ GameState.getState().players.get(3).score);
				
				// Display the time left in game round
				TextView displayTime= (TextView) findViewById(R.id.timeLeft);
				displayTime.setText(GameState.getState().timeToString());
			}
		});
	}
	
	/** Return number of ants */
	public static int getNrOfAnts()
	{
		return GameState.getState().ants.size();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		GameState.getState().eraseGameState();
		//Log.d(TAG,"On destroy");
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
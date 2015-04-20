package com.google.sprint1;

import java.io.File;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
	/* Variables for objects in the game */
	/*private IGeometry towerGeometry1;
	private IGeometry canonGeometry1;
	private IGeometry ballGeometry1;
	private IGeometry towerGeometry2;
	private IGeometry canonGeometry2;
	private IGeometry ballGeometry2;
	private IGeometry towerGeometry3;
	private IGeometry canonGeometry3;
	private IGeometry ballGeometry3;
	private IGeometry towerGeometry4;
	private IGeometry canonGeometry4;*/

	Player player;
	Player bluePlayer;
	Player greenPlayer;
	Player redPlayer;
	Player yellowPlayer;

	//private IGeometry aimPowerUp;
	//private IGeometry aniBox;

	//private IGeometry crosshair;

	GameState gameState;

	Ant ant;
	Aim aim;


	private IGeometry ball;
	private IGeometry ballShadow;
	//private IGeometry touchSphere;

	
	private IGeometry movBox;

	
	//Gesture handler
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;
		
	private double angleForCanon;
	
	private Vector3d touchVec; // Difference between ball and tower when shooting

	// point count
	protected int point;
	TextView displayPoints;

	float temp;
	float scaleStart; // skalning av pilen för siktet

	// Variables for Service handling
	private NetworkService mService;
	boolean mBound = false;

	// FPS specific variables
	//private int frameCounter = 0;
	//private double lastTime;

	public static final String TAG = "GameActivity";

	protected void onDestroy() {

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

		// Bind to NetworkService
		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
		
		GameState.getState().exsisting_paint_balls = new ArrayList<PaintBall>(20);
		GameState.getState().ants = new ArrayList<Ant>(10);
		

		touchVec = new Vector3d(0f, 0f, 0f);

		
		//player = new Player(4);
		
		angleForCanon = Math.PI/6;
		
		//player = GameState.getState().players.get(0);
		
		temp = 20f;

		scaleStart = 0f;
		
		//Gesture handler
		mGestureMask = GestureHandler.GESTURE_DRAG;
		mGestureHandler = new GestureHandlerAndroid(metaioSDK, mGestureMask);		

	}

	/** Called when the user clicks the Exit button (krysset) */
	public void onExitButtonClick(View v) {
		finish();
	}

	public void onClickSendData(View v) {
		TestClass test = new TestClass(5, "hej");
		//mService.mConnection.sendData(test);
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

			//creates the tower

			/*towerGeometry1 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry1, 3f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));
=======
			towerGeometry1 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry1, 2f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));

			canonGeometry1 = Load3Dmodel("tower/slingshotRed.mfbx");
			geometryProperties(canonGeometry1, 1.5f, new Vector3d(-675f, -495f, 165f), new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
			
			//Load touchSphere to easier shoot from tower
			touchSphere = Load3Dmodel("tower/invisibleBall.mfbx");
			geometryProperties(touchSphere, 6f, new Vector3d(-650f, -520f, 250f), new Rotation(0f, 0f, 0f));
			
			ballGeometry1 = Load3Dmodel("paintball/paintball/ballRed.mfbx");
			geometryProperties(ballGeometry1, 2f, new Vector3d(-650f, -520f, 350f), new Rotation(0f, 0f, 0f));
			mGestureHandler.addObject(ballGeometry1, 1);	*/		
			
			bluePlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotBlue.mfbx"), Load3Dmodel("paintball/paintball/ballBlue.mfbx"), new Vector3d(-650f, -520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));
			greenPlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotGreen.mfbx"), Load3Dmodel("paintball/paintball/ballGreen.mfbx"), new Vector3d(650f, 520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));	
			redPlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotRed.mfbx"), Load3Dmodel("paintball/paintball/ballRed.mfbx"), new Vector3d(-650f, 520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));
			yellowPlayer = new Player(Load3Dmodel("tower/tower.mfbx"), Load3Dmodel("tower/slingshotYellow.mfbx"), Load3Dmodel("paintball/paintball/ballYellow.mfbx"), new Vector3d(650f, -520f, 350f), Load3Dmodel("tower/invisibleBall.mfbx"));
				
			
			player = bluePlayer;
			//mGestureHandler.addObject(player.ballGeometry, 1);
			mGestureHandler.addObject(player.touchSphere, 1);
			
			
			/*towerGeometry2 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry2, 2f, new Vector3d(650f, 520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry2 = Load3Dmodel("tower/slingshotBlue.mfbx");
			geometryProperties(canonGeometry2, 1.5f, new Vector3d(625f, 545f, 165f), new Rotation((float)Math.PI/2, 0f, (float)Math.PI/4));
			ballGeometry2 = Load3Dmodel("paintball/paintball/ballBlue.mfbx");
			geometryProperties(ballGeometry2, 2f, new Vector3d(650f, 520f, 250f), new Rotation(0f, 0f, 0f));
			
			towerGeometry3 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry3, 2f, new Vector3d(-650f, 520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry3 = Load3Dmodel("tower/slingshotYellow.mfbx");
			geometryProperties(canonGeometry3, 1.5f, new Vector3d(-625f, 545f, 165f), new Rotation((float)Math.PI/2, 0f, -(float)Math.PI/4));
			ballGeometry3 = Load3Dmodel("paintball/paintball/ballYellow.mfbx");
			geometryProperties(ballGeometry3, 2f, new Vector3d(-650f, 520f, 250f), new Rotation(0f, 0f, 0f));
			
			towerGeometry4 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry4, 2f, new Vector3d(650f, -520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry4 = Load3Dmodel("tower/slingshotGreen.mfbx");
			geometryProperties(canonGeometry4, 1.5f, new Vector3d(675f, -495f, 165f), new Rotation((float)Math.PI/2, 0f, -(float)Math.PI/4));
			ballGeometry4 = Load3Dmodel("paintball/paintball/ballGreen.mfbx");
			geometryProperties(ballGeometry4, 2f, new Vector3d(650f, -520f, 250f), new Rotation(0f, 0f, 0f));
*/

			// Load powerUps
			PowerUp power = new PowerUp(Load3Dmodel("powerUps/aimPowerUp.mfbx"));
			GameState.getState().powerUps.add(power);
			
			movBox = Load3Dmodel("movBox.mfbx");
			geometryProperties(movBox, 4f, new Vector3d(0, 0, 100f), new Rotation(0f, 0f, 0f));
			movBox.startAnimation("Take 001", true);
			
			// creates the aim path
			ArrayList<IGeometry> ballPath = new ArrayList<IGeometry>(10);
			ArrayList<IGeometry> ballPathShadow = new ArrayList<IGeometry>(10);			
			for (int i = 0; i < 10; i++) 
			{
				ball = Load3Dmodel("paintball/paintball/ballRed.mfbx");
				ballShadow = Load3Dmodel("paintball/paintballShadow.mfbx");
				ballPath.add(ball);
				ballPathShadow.add(ballShadow);
			}
			
			// Load aim (crosshair and ballPath)			
			aim = new Aim(Load3Dmodel("crosshair/crosshair.mfbx"),ballPath,ballPathShadow);
			

			// creates a list of ants 
			for(int i = 0; i < 10; i++)
			{
				// create ant geometry
				ant = new Ant(Load3Dmodel("ant/aniAnt2.mfbx"), Load3Dmodel("ant/markers/boxRed.mfbx"), false);
				GameState.getState().ants.add(ant);
			}
			
			// creates a list of paint balls
			for (int i = 0; i < 20; i++) {
				// add paint ball to list of paint balls
				GameState.getState().exsisting_paint_balls.add(
						new PaintBall(i,Load3Dmodel("paintball/paintball/ballRed.mfbx"),
									  Load3Dmodel("paintball/splash/splashRed.mfbx"),
									  Load3Dmodel("paintball/paintballShadow.mfbx")));
			}
		} catch (Exception e) {
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
		for ( int i = 0; i < 10 ; i++)
		{
			if(!GameState.getState().ants.get(i).isActive())

			{
				// if not already spawned, spawn at random 
				GameState.getState().ants.get(i).spawnAnt();
			}
			
			
			//if ant is hit move to tower else move at random 
			if(GameState.getState().ants.get(i).getIsHit() == true)
			{
				GameState.getState().ants.get(i).movementToTower(new Vector3d(player.getPosition()));
			}
			else
				GameState.getState().ants.get(i).randomMovement();
					

		}
		
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
    	for(PaintBall obj : GameState.getState().exsisting_paint_balls)
    	{
    		if (!(obj.getGeometry().isVisible()))
    			return obj;
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
        		
            	PaintBall ball = getAvailableBall(1);
        		if(ball != null)
        		{
            		Vector3d pos = player.getPosition();
            		
        			//Vector3d vel = new Vector3d((float)(touchVec.getX()/3* Math.cos(angleForCanon)), (float)(touchVec.getY()/3* Math.cos(angleForCanon)), (float)(Math.abs(touchVec.getX()/5)* Math.sin(angleForCanon)+ Math.abs(touchVec.getY()/5)*Math.sin(angleForCanon)));
        			DataPackage data = new DataPackage(ball.getId(), vel, pos);
        			mService.mConnection.sendData(data);
        			
        			//check if touched outside sphere -> do nothing
        			if(!(Math.abs(touchVec.getX()) < 0.1f))
        			{
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
				displayPoints.setText("Score: " + Player.getScore());
			}
		});
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
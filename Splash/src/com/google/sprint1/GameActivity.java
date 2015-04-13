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
	private IGeometry towerGeometry1;
	private IGeometry canonGeometry1;
	private IGeometry towerGeometry2;
	private IGeometry canonGeometry2;
	private IGeometry towerGeometry3;
	private IGeometry canonGeometry3;
	private IGeometry towerGeometry4;
	private IGeometry canonGeometry4;
	private IGeometry crosshair;
	private IGeometry arrowAim;
	private IGeometry aimPowerUp;

	private IGeometry ball;
	private IGeometry ballShadow;
	private ArrayList<IGeometry> ballPath; // lista med bollar som visar parabeln för den flygande färgbollen
	private ArrayList<IGeometry> ballPathShadow; // skuggor till parabelsiktet

	GameState gameState;
	
	//Gesture handler
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;

	Ant ant;
	
	private double angleForCanon;
	private float prevAng;
	private ArrayList<Float> prevAngle;
	
	private Vector3d startTouch;
	private Vector3d currentTouch;
	private Vector3d endTouch;

	private Vector3d touchVec; // endTouch-startTouch

	Player player;

	// point count
	protected int point;
	TextView displayPoints;

	float temp;
	float scaleStart; // skalning av pilen för siktet

	// Variables for Service handling
	private NetworkService mService;
	boolean mBound = false;

	// FPS specific variables
	private int frameCounter = 0;
	private double lastTime;

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
		
		ballPath = new ArrayList<IGeometry>(10);
		ballPathShadow = new ArrayList<IGeometry>(10);
		
		prevAngle = new ArrayList<Float>(10);

		// displayPoints = (TextView) findViewById(R.id.myPoints);

		touchVec = new Vector3d(0f, 0f, 0f);

		currentTouch = new Vector3d(0f, 0f, 0f);
		startTouch = new Vector3d(0f, 0f, 0f);
		endTouch = new Vector3d(0f, 0f, 0f);
		
		player = new Player(1);
		
		angleForCanon = Math.PI/4;


		
		player = GameState.getState().players.get(1);
		
		temp = 20f;
		point = 0;

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
		mService.mConnection.sendData(test);
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

			//Gesture handler
			//creates the tower
			towerGeometry1 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry1, 2f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry1 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry1, 2f, new Vector3d(-650f, -520f, 165f), new Rotation(0f, 0f, 0f));
			mGestureHandler.addObject(canonGeometry1, 1);			
			
			towerGeometry2 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry2, 2f,
					new Vector3d(650f, 520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry2 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry2, 2f, new Vector3d(650f, 520f,
					165f), new Rotation(0f, 0f, 0f));
			towerGeometry3 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry3, 2f,
					new Vector3d(-650f, 520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry3 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry3, 2f, new Vector3d(-650f, 520f,
					165f), new Rotation(0f, 0f, 0f));
			towerGeometry4 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry4, 2f,
					new Vector3d(650f, -520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry4 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry4, 2f, new Vector3d(650f, -520f,
					165f), new Rotation(0f, 0f, 0f));

			// Load crosshair
			crosshair = Load3Dmodel("crosshair/crosshair.mfbx");
			geometryProperties(crosshair, 1f, new Vector3d(0f, 0f, 0f),
					new Rotation(0f, 0f, 0f));
			crosshair.setVisible(false);

			arrowAim = Load3Dmodel("crosshair/arrow.obj");
			geometryProperties(arrowAim, 2f, new Vector3d(-550, -450, 200f),
					new Rotation((float) (3 * Math.PI / 2), 0f, 0f));
			arrowAim.setVisible(false);

			// Load powerUps
			PowerUp power = new PowerUp(Load3Dmodel("powerUps/aimPowerUp.mfbx"));
			power.setGeometryProperties(power.geometry, 2.1f, new Vector3d(0f, 0f, 0f),
					new Rotation(0f, 0f, 0f));
			GameState.getState().powerUps.add(power);
			
			// creates the aim path
			for (int i = 0; i < 10; i++) {
				// create new paint ball

				ball = Load3Dmodel("paintball/paintball/ballBlue.mfbx");
				ballShadow = Load3Dmodel("paintball/paintballShadow.mfbx");
				geometryProperties(ball, 0.5f, new Vector3d(-550, -450, 200f), new Rotation(0f, 0f, 0f));
				geometryProperties(ballShadow, 0.2f, new Vector3d(-550, -450, 0), new Rotation(0f, 0f, 0f));
				ballPath.add(ball);
				ballPathShadow.add(ballShadow);
				ball.setVisible(false);
				ballShadow.setVisible(false);

			}
			
			// creates a list of ants 
			for(int i = 0; i < 10; i++)
			{
				// create ant geometry
				ant = new Ant(Load3Dmodel("ant/formicaRufa.mfbx"), false);
				GameState.getState().ants.add(ant);
			}
			
			// creates a list of paint balls
			for (int i = 0; i < 20; i++) {
				// add paint ball to list of paint balls
				GameState.getState().exsisting_paint_balls.add(
						new PaintBall(i,Load3Dmodel("paintball/paintball/ballBlue.mfbx"),
									  Load3Dmodel("paintball/splash/splashYellow.mfbx"),
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
		if ( towerGeometry4== null || GameState.getState().exsisting_paint_balls.isEmpty())
			return;

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
				GameState.getState().ants.get(i).movementToTower(new Vector3d(300f, 0f, 0f)); // TODO insert the player tower position
			}
			else
				GameState.getState().ants.get(i).movement();			

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
		
		updateFps();
	}

	/** function that activates when an object is being touched */
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// Only implemented because its required by the parent class
	}
    
	/** Function to draw the path of the ball (aim) */
	private void drawBallPath(Vector3d touchVec) {
		float velocity =(float)(Math.abs(touchVec.getX()/4)* Math.sin(Math.PI/4)+ Math.abs(touchVec.getY()/4)*Math.sin(Math.PI/4));//(Math.abs(currentTouch.getX()) + Math.abs(currentTouch.getY())) / (4f * (float) Math.sqrt(2));
		float timeToLanding = (float) (velocity / (2 * (float) Math.sqrt(2) * 9.8f) + Math.sqrt(Math.pow( velocity / (2 * Math.sqrt(2) * 9.8), 2) + 165 / 9.8));
		// Log.d(TAG, "time to landing : " + timeToLanding);

		for (int i = 0; i < 10; i++) {
			ballPath.get(i).setTranslation( new Vector3d(player.position.getX() + (float) ((double) (i) / 5) * touchVec.getX(),
														 player.position.getY() + (float) ((double) (i) / 5) * touchVec.getY(),
														 getPathZPos( velocity, (i * timeToLanding / 10))));

			ballPathShadow.get(i).setTranslation( new Vector3d(player.position.getX() + (float) ((double) (i) / 5) * touchVec.getX(),
															   player.position.getY() + (float) ((double) (i) / 5) * touchVec.getY(),
															   0f));
		}
	/*	float Zpos = 165f;
		float Zvel = 0f;
		float timeStep = 0.2f;		
		while (Zpos!=0)
		{
			Zvel = Zvel + timeStep*9.82f;
			Zpos = Zpos + timeStep*Zvel; 
			stepcount ++;
		} */
	}

	/** Function to get ballpath position in Z */
	private float getPathZPos(float velocity, float time) 
	{
		float pos = 0;
		pos = (float) (165 - 9.82 * Math.pow(time, 2) + velocity * time / Math.sqrt(2));

		return pos;
	}

    private PaintBall getAvailableBall(int id)
    {
    	for(PaintBall obj : GameState.getState().exsisting_paint_balls)
    	{
    		if (!(obj.geometry.isVisible()))
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
		touchVec = new Vector3d(-(canonGeometry1.getTranslation().getX()-towerGeometry1.getTranslation().getX()),
									-(canonGeometry1.getTranslation().getY()-towerGeometry1.getTranslation().getY()),
									0f);   

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:                
                if(player.superPower == true)
                	{
                		crosshair.setVisible(true);
                		
                	}
                else //if(player.superPower == false)
                {                	
                    for(int i = 0; i < 10; i++)
                    {
                    	ballPath.get(i).setVisible(true);
                    	ballPathShadow.get(i).setVisible(true);
                    }                       
                }
                break;
            case MotionEvent.ACTION_MOVE:            	
            	if(player.superPower == true)
            	{
                	crosshair.setTranslation(new Vector3d( player.position.getX()+2.2f*touchVec.getX(),
							   player.position.getY()+2.2f*touchVec.getY(),
							   0f));
            	}
            	else //if(player.superPower == false)
            	{
            		drawBallPath(touchVec);
            	}            
                break;
            case MotionEvent.ACTION_UP:
            	crosshair.setVisible(false);
        		for(int i = 0; i < 10; i++)
        		{
        			ballPath.get(i).setVisible(false);
        			ballPathShadow.get(i).setVisible(false);
        		}
            	// move "slangbella" to original position
        		canonGeometry1.setTranslation(towerGeometry1.getTranslation());
        		canonGeometry1.setTranslation(new Vector3d(0f, 0f, 165f), true);
        		
            	PaintBall ball = getAvailableBall(1);
        		if(ball != null)
        		{
            		Vector3d pos = player.position;
            		
            		// Math.sin(Math.PI/6) angle PI/6 = 30' => sin(pi/6) = 0.5 && Math.cos(Math.PI/6) angle PI/6 = 30' => cos(pi/6) = 0.5
        			Vector3d vel = new Vector3d((float)(touchVec.getX()/4* Math.cos(angleForCanon)), (float)(touchVec.getY()/4* Math.cos(angleForCanon)), (float)(Math.abs(touchVec.getX()/4)* Math.sin(angleForCanon)+ Math.abs(touchVec.getY()/4)*Math.sin(angleForCanon)));
        			DataPackage data = new DataPackage(ball.id, vel, pos);
        			mService.mConnection.sendData(data);
        			ball.fire(vel, pos);            	
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
	
	/**Updates Fps each frame and display it to user once every second*/ 
	private void updateFps() {
		
		//Adds one each frame
		frameCounter++;
		
		//Uses internal clock to calculate the difference between current time and last time
		double currentTime = System.currentTimeMillis() - lastTime;
		//calculates the fps (*1000 due to milliseconds)
		final int fps = (int) (((double) frameCounter / currentTime) * 1000);
		
		//Only displays if current time is over one second
		if (currentTime > 1.0) {
			lastTime = System.currentTimeMillis();
			frameCounter = 0;
			
			//Necessary to run on UI thread to be able to edit the TextView
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					TextView displayPoints = (TextView) findViewById(R.id.myPoints);
					displayPoints.setText("FPS: " + fps);
				}
			});
		}
	}
}
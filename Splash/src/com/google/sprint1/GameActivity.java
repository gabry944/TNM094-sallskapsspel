package com.google.sprint1;

import java.io.File;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.sprint1.NetworkService.LocalBinder;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
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

	// private IGeometry wallGeometry1;
	// private IGeometry wallGeometry2;
	// private IGeometry wallGeometry3;
	// private IGeometry wallGeometry4;
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

	PaintBall paint_ball_object;
	private ArrayList<PaintBall> exsisting_paint_balls;
	
	Ant ant;
	private ArrayList<Ant> ants;

	private Vector3d startTouch;
	private Vector3d currentTouch;
	private Vector3d endTouch;
	private Vector3d touchVec; // endTouch-startTouch

	// to enable gesture tracking
	// protected GridView surfaceView;
	// protected GestureOverlayView gestureOverlayView;
	// protected FrameLayout frameLayout;

	Player player;
	// private ArrayList<Player> players;

	// point count
	protected int point;
	TextView displayPoints;

	// Variables for physics calibration
	Vector3d acceleration;
	Vector3d velocity;
	Vector3d totalForce;
	Vector3d gravity;
	float timeStep;
	float mass;

	float temp;
	float scaleStart; // skalning av pilen för siktet

	// Variables for Service handling
	private NetworkService mService;
	boolean mBound = false;

	/* delkaration av variabler som används i renderingsloopen */
	float SphereMoveX = 2f;

	// FPS specific variables
	int frameCounter = 0;
	double lastTime;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//arraylists
		exsisting_paint_balls = new ArrayList<PaintBall>(20);
		ants = new ArrayList<Ant>(10);
		ballPath = new ArrayList<IGeometry>(10);
		ballPathShadow = new ArrayList<IGeometry>(10);

		// displayPoints = (TextView) findViewById(R.id.myPoints);

		// velocity and direction of outgoing paintball
		acceleration = new Vector3d(0f, 0f, 0f);
		velocity = new Vector3d(0f, 0f, 0f);
		totalForce = new Vector3d(0f, 0f, 0f);
		gravity = new Vector3d(0f, 0f, -9.82f);
		timeStep = 0.2f; // 0.1s
		mass = 0.1f; // 0.1kg

		touchVec = new Vector3d(0f, 0f, 0f);
		currentTouch = new Vector3d(0f, 0f, 0f);
		startTouch = new Vector3d(0f, 0f, 0f);
		endTouch = new Vector3d(0f, 0f, 0f);

		player = new Player(1);

		temp = 20f;
		point = 0;

		scaleStart = 0f;

	}

	/** Called when the user clicks the Exit button (krysset) */
	public void onExitButtonClick(View v) {
		stopService(new Intent(this, NetworkService.class));
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
	protected void loadContents() {
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

			// creates the walls around the game area
			// wallGeometry1 = Load3Dmodel("wall/wall.mfbx");
			// geometryProperties(wallGeometry1, 20f, new Vector3d(850f, 0f,
			// 0f), new Rotation(0f, 0f, (float) (3*Math.PI/2)));
			// wallGeometry2 = Load3Dmodel("wall/wall.mfbx");
			// geometryProperties(wallGeometry2, 20f, new Vector3d(-850f, 0f,
			// 0f), new Rotation(0f, 0f, (float) (3*Math.PI/2)));
			// wallGeometry3 = Load3Dmodel("wall/wall.mfbx");
			// geometryProperties(wallGeometry3, 20f, new Vector3d(0f, 720f,
			// 0f), new Rotation(0f, 0f, 0f));
			// wallGeometry4 = Load3Dmodel("wall/wall.mfbx");
			// geometryProperties(wallGeometry4, 20f, new Vector3d(0f, -720f,
			// 0f), new Rotation(0f, 0f, 0f));

			// creates the tower
			towerGeometry1 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry1, 2f, new Vector3d(-650f, -520f,
					0f), new Rotation(0f, 0f, 0f));
			canonGeometry1 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry1, 2f, new Vector3d(-650f, -520f,
					165f), new Rotation(0f, 0f, 0f));
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
			aimPowerUp = Load3Dmodel("powerUps/aimPowerUp.mfbx");
			geometryProperties(aimPowerUp, 2.1f, new Vector3d(0f, 0f, 0f),
					new Rotation(0f, 0f, 0f));

			// creates the aim path
			for (int i = 0; i < 10; i++) {
				// create new paint ball

				ball = Load3Dmodel("tower/paintball.obj");
				ballShadow = Load3Dmodel("tower/paintballShadow.mfbx");
				geometryProperties(ball, 0.5f, new Vector3d(-550, -450, 200f),
						new Rotation(0f, 0f, 0f));
				geometryProperties(ballShadow, 0.2f,
						new Vector3d(-550, -450, 0), new Rotation(0f, 0f, 0f));
				ballPath.add(ball);
				ballPathShadow.add(ballShadow);
				ball.setVisible(false);
				ballShadow.setVisible(false);

			}
			
			// creates a list of ants 
			for(int i = 0; i < 10; i++)
			{
				// create ant geometry
				ant = new Ant(Load3Dmodel("ant/formicaRufa.mfbx"));
				ants.add(ant);
			}
			
			// creates a list of paint balls
			for (int i = 0; i < 20; i++) {
				// create new paint ball
				paint_ball_object = new PaintBall(
						Load3Dmodel("tower/paintball.obj"),
						Load3Dmodel("tower/splash.mfbx"),
						Load3Dmodel("tower/paintballShadow.mfbx"));

				// add paint ball to list of paint balls
				exsisting_paint_balls.add(paint_ball_object);
			}
		} catch (Exception e) {
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}

	// function to set the properties for the geometry
	public void geometryProperties(IGeometry geometry, float scale,
			Vector3d translationVec, Rotation rotation) {
		geometry.setScale(scale);
		geometry.setTranslation(translationVec, true);
		geometry.setRotation(rotation, true);
	}

	/** Render Loop */
	@Override
	public void onDrawFrame() {
		super.onDrawFrame();

		// If content not loaded yet, do nothing

		if (towerGeometry4 == null || exsisting_paint_balls.isEmpty())
			return;

		// Log.d(TAG, "touchVec = "+ touchVec);
		// antGeometry.setTranslation(touchVec, true);

		//spawn ant at random and move ants
		for ( int i = 0; i < 10 ; i++)
		{
			if(!ants.get(i).isActive())
			{
				// if not already spawned, spawn at random 
			}
			
			//move ants
			ants.get(i).movement();
		}

		powerUpAnimation(aimPowerUp);

		if (!exsisting_paint_balls.isEmpty()) {
			for (PaintBall obj : exsisting_paint_balls) {
				if (obj.isActive()) {
					obj.update();
					
					for(int i = 0; i < 10 ; i++)
					{
						if (checkCollision(obj, ants.get(i).ant)) {
							 ants.get(i).ant.setRotation(new Rotation(
									(float) (3 * Math.PI / 4), 0f, 0f), true);
							obj.splashGeometry.setTranslation(obj.geometry
									.getTranslation());
							obj.splashGeometry.setVisible(true);
							obj.velocity = new Vector3d(0f, 0f, 0f);
							obj.geometry.setVisible(false);
							obj.paintballShadow.setVisible(false);
							point++;
							
							// displayPoints =
							// (TextView)findViewById(R.id.myPoints);
							// displayPoints.setText("Ponts:" + point);
							// displayPoints =
							// (TextView)findViewById(R.id.editText1);
							// (TextView)findViewById(R.id.editText1).setText("Ponts:"
							// + point);
						}
					}

					if (checkCollision(obj, aimPowerUp)) {
						player.superPower = true;
						aimPowerUp.setVisible(false);
					}
				}
			}
		}
		// onTouchEvent(null);

		updateFps();

	}

	public boolean checkCollision(PaintBall obj, IGeometry obj2) {
		Vector3d min = obj2.getBoundingBox(true).getMin();
		Vector3d max = obj2.getBoundingBox(true).getMax();

		if (obj.geometry.getTranslation().getX()
				+ obj.geometry.getBoundingBox().getMax().getX() > obj2
				.getTranslation().getX() + 2 * min.getX() - 100
				&& obj.geometry.getTranslation().getX()
						+ obj.geometry.getBoundingBox().getMin().getX() < obj2
						.getTranslation().getX() + 2 * max.getX() + 100
				&& obj.geometry.getTranslation().getY()
						+ obj.geometry.getBoundingBox().getMax().getY() > obj2
						.getTranslation().getY() + 2 * min.getY() - 100
				&& obj.geometry.getTranslation().getY()
						+ obj.geometry.getBoundingBox().getMin().getY() < obj2
						.getTranslation().getY() + 2 * max.getY() + 100
				&& obj.geometry.getTranslation().getZ()
						+ obj.geometry.getBoundingBox().getMax().getZ() > obj2
						.getTranslation().getZ() + 2 * min.getZ() - 100
				&& obj.geometry.getTranslation().getZ()
						+ obj.geometry.getBoundingBox().getMin().getZ() < obj2
						.getTranslation().getZ() + 2 * max.getZ() + 100)

			return true;
		else
			return false;
	}

	/** function that activates when an object is being touched */
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// Only implemented because its required by the parent class
		if (geometry == canonGeometry1) {
			Log.d(TAG, "hej");
		}

	}

	@Override
	/** Function to handle touch input */
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// screen coordinates for first touch
			startTouch = new Vector3d((event.getX()), event.getY(), 0f);

			// Log.d(TAG, "startTouch ="+ startTouch);

			if (player.superPower == true) {
				crosshair.setVisible(true);

			} else if (player.superPower == false) {
				// arrowAim.setVisible(true);
				// arrowAim.setScale(new Vector3d( 0f, 2f, 2f));

				for (int i = 0; i < 10; i++) {
					ballPath.get(i).setVisible(true);
					ballPathShadow.get(i).setVisible(true);
				}

			}

			break;
		case MotionEvent.ACTION_MOVE:
			// gives the coordinates of your finger touch all the time to be
			// able to calculate a crosshair
			currentTouch = new Vector3d(-(event.getX() - startTouch.getX()),
					event.getY() - startTouch.getY(), 0f);

			if (player.superPower == true) {
				crosshair.setTranslation(new Vector3d(player.position.getX()
						+ 2.2f * currentTouch.getX(), player.position.getY()
						+ 2.2f * currentTouch.getY(), 0f));

			}

			else if (player.superPower == false) {
				drawBallPath(currentTouch);

				// arrowAim.setScale(new Vector3d(
				// Math.abs(currentTouch.getX()+currentTouch.getY())*0.01f, 2f,
				// 2f));
				// setArrowRotation(currentTouch);
			}

			// Log.d(TAG, "currentTouch = " + currentTouch);

			break;
		case MotionEvent.ACTION_UP:
			// coordinates for the last touch
			endTouch = new Vector3d((event.getX()), event.getY(), 0f);
			touchVec = new Vector3d(-(endTouch.getX() - startTouch.getX()),
					endTouch.getY() - startTouch.getY(), 0f);
			crosshair.setVisible(false);
			arrowAim.setVisible(false);
			for (int i = 0; i < 10; i++) {
				ballPath.get(i).setVisible(false);
				ballPathShadow.get(i).setVisible(false);
			}
			// Log.d(TAG, "endTouch ="+ endTouch);
			// Log.d(TAG, "touchVec = " + touchVec);
			PaintBall ball = getAvailableBall(1);
			if (ball != null) {

				ball.geometry.setTranslation(player.position);
				ball.velocity = new Vector3d(touchVec.getX() / 2,
						touchVec.getY() / 2, (Math.abs(touchVec.getX()
								+ touchVec.getY()) / 2));
				// Log.d(TAG, "vel = " + paint_ball_object.velocity);
				ball.activate();
				break;
			}
		}
		return true;
	}

	/** Function to draw the path of the ball (aim) */
	private void drawBallPath(Vector3d currentTouch) {
		float velocity = (Math.abs(currentTouch.getX()) + Math.abs(currentTouch.getY())) / (4f * (float) Math.sqrt(2));
		float timeToLanding = (float) (velocity / (2 * (float) Math.sqrt(2) * 9.8f) + Math.sqrt(Math.pow( velocity / (2 * Math.sqrt(2) * 9.8), 2) + 165 / 9.8));
		// Log.d(TAG, "time to landing : " + timeToLanding);

		for (int i = 0; i < 10; i++) {
			ballPath.get(i).setTranslation( new Vector3d(player.position.getX() + (float) ((double) (i) / 5) * currentTouch.getX(),
														 player.position.getY() + (float) ((double) (i) / 5) * currentTouch.getY(),
														 getPathZPos( velocity, (i * timeToLanding / 10))));

			ballPathShadow.get(i).setTranslation( new Vector3d(player.position.getX() + (float) ((double) (i) / 5) * currentTouch.getX(),
															   player.position.getY() + (float) ((double) (i) / 5) * currentTouch.getY(),
															   0f));
		}

	}

	/** Function to get ballpath position in Z */
	private float getPathZPos(float velocity, float time) 
	{
		float pos = 0;

		pos = (float) (165 - 9.8 * Math.pow(time, 2) + velocity * time / Math.sqrt(2));
		// Log.d(TAG, "pos " + pos);

		return pos;
	}

	/** Function to set the rotation of the arrow aim */
	private void setArrowRotation(Vector3d deltaTouch) {
		float deltaX = deltaTouch.getX();
		float deltaY = -deltaTouch.getY();

		float theta = (float) Math.tanh((deltaY / deltaX)); // * (Math.PI/180)
		arrowAim.setRotation(new Rotation(0f, theta, 0f));

	}



	private PaintBall getAvailableBall(int id) {
		for (PaintBall obj : exsisting_paint_balls) {
			if (!(obj.geometry.isVisible()))
				return obj;
		}

		return null;
	}

	/** Function for animation on the powerup */
	private void powerUpAnimation(IGeometry powerUp)
	{
		if (powerUp.getScale().getX() > 2.0f) {
			scaleStart = -0.02f;
		}
		if (powerUp.getScale().getX() < 1.0f) {
			scaleStart = 0.02f;
		}
		powerUp.setScale(powerUp.getScale().add(
				new Vector3d(scaleStart, scaleStart, scaleStart)));
		// Log.d(TAG, "scale = " + powerUp.getScale());

	}

	/** Pause function, makes you return to the main menu when pressing "back" */
	@Override
	public void onPause() {
		super.onPause();
		// creates a fade between scenes
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	/** Not used at the moment */
	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// No callbacks needed
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

	private void updateFps() {

		frameCounter++;

		double currentTime = System.currentTimeMillis() - lastTime;
		final int fps = (int) (((double) frameCounter / currentTime) * 1000);

		if (currentTime > 1.0) {
			lastTime = System.currentTimeMillis();
			frameCounter = 0;

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					TextView displayPoints = (TextView) findViewById(R.id.myPoints);
					;

					displayPoints.setText("FPS: " + fps);
				}
			});
		}

	}
}
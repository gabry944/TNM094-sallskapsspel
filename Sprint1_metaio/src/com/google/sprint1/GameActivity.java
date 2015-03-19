package com.google.sprint1;


import java.io.File;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
 

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
public class GameActivity extends ARViewActivity 
{

	/*Variables for objects in the game*/
	private IGeometry antGeometry;
	private IGeometry wallGeometry1;
	private IGeometry wallGeometry2;
	private IGeometry wallGeometry3;
	private IGeometry wallGeometry4;
	private IGeometry towerGeometry1;
	private IGeometry canonGeometry1;
	private IGeometry towerGeometry2;
	private IGeometry canonGeometry2;
	private IGeometry towerGeometry3;
	private IGeometry canonGeometry3;
	private IGeometry towerGeometry4;
	private IGeometry canonGeometry4;
	private IGeometry paintballGeometry;
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;

	//Variables for physics calibration
	Vector3d acceleration;
	Vector3d velocity;
	Vector3d totalForce;
	Vector3d gravity;
	float timestep;
	float mass;
	
	/*delkaration av variabler som anv�nds i renderingsloopen*/
	float SphereMoveX = 2f;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mGestureMask  = GestureHandler.GESTURE_ALL;
		mGestureHandler = new GestureHandlerAndroid(metaioSDK,mGestureMask);
		
		//velocity and direction of outgoing paintball, will later be based on how you interact with the screen (TODO).
		acceleration =  new Vector3d(0f, 0f, 0f);
		velocity =  new Vector3d(0f, 0f, 0f);
		totalForce =  new Vector3d(0f, 0f, 0f);
		gravity = new Vector3d(0f, 0f, -9.82f);
		timestep = 0.1f;								//0.1s
		mass = 0.1f;		   							//0.1kg
	}
	
	
	/** Attaching layout to the activity */
	@Override
	public int getGUILayout()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return R.layout.activity_game;
	}

	/** Called when the user clicks the Exit button (krysset) */
	public void onExitButtonClick(View v) {
		finish();
	}
	
	/** Create a geometry, the string input gives the filepach (relative from the asset folder) to the geometry 
	 * First check if model is found ->Load and create 3D geometry ->check if model was loaded successfully
	 * Returns the loaded model if success, otherwise null*/
	private IGeometry Load3Dmodel(String filePath)

	{
		//Getting the full file path for a 3D geometry
		final File modelPath = AssetsManager.getAssetPathAsFile(getApplicationContext(), filePath);
		//First check if model is found
		if (modelPath != null)
		{
			//Load and create 3D geometry
			IGeometry model = metaioSDK.createGeometry(modelPath);
			
			//check if model was loaded successfully
			if (model != null)
				return model;
			else
				MetaioDebug.log(Log.ERROR, "Error loading geometry: " + modelPath);
		}
		else
			MetaioDebug.log(Log.ERROR, "Could not find 3D model");

		return null;
	}

	/** Loads the marker and the 3D-models to the game */
	@Override
	protected void loadContents()
	{
		try 
		{
			/** Load Marker */
			// Getting a file path for tracking configuration XML file
			File trackingConfigFile = AssetsManager.getAssetPathAsFile(
					getApplicationContext(), "TrackingData_MarkerlessFast.xml"); 
			
			// Assigning tracking configuration
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile); 
			
			MetaioDebug.log("Tracking data loaded: " + result);
			
			
			/** Load Object */

			//create ant geometry
			antGeometry = Load3Dmodel("ant/formicaRufa.mfbx");
			geometryProperties(antGeometry, 10f, new Vector3d(-100.0f, 100.0f, 0.0f), new Rotation((float) (3*Math.PI/2), 0f, 0f) );
			
			//creates the walls around the game area
			wallGeometry1 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry1, 20f, new Vector3d(850f, 0f, 0f), new Rotation(0f, 0f, (float) (3*Math.PI/2)));
			wallGeometry2 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry2, 20f, new Vector3d(-850f, 0f, 0f), new Rotation(0f, 0f, (float) (3*Math.PI/2)));
			wallGeometry3 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry3, 20f, new Vector3d(0f, 720f, 0f), new Rotation(0f, 0f, 0f));
			wallGeometry4 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry4, 20f, new Vector3d(0f, -720f, 0f), new Rotation(0f, 0f, 0f));
			
			//creates the tower
			towerGeometry1 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry1, 4f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry1 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry1, 4f, new Vector3d(-650f, -520f, 330f), new Rotation(0f, 0f, 0f));
			towerGeometry2 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry2, 4f, new Vector3d(650f, 520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry2 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry2, 4f, new Vector3d(650f, 520f, 330f), new Rotation(0f, 0f, 0f));
			towerGeometry3 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry3, 4f, new Vector3d(-650f, 520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry3 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry3, 4f, new Vector3d(-650f, 520f, 330f), new Rotation(0f, 0f, 0f));
			towerGeometry4 = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry4, 4f, new Vector3d(650f, -520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry4 = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry4, 4f, new Vector3d(650f, -520f, 330f), new Rotation(0f, 0f, 0f));
			
			paintballGeometry = Load3Dmodel("tower/paintball.obj");
			geometryProperties(paintballGeometry, 2f, new Vector3d(-600f, -450f, 370f), new Rotation(0f, 0f, 0f));
			paintballGeometry.setVisible(false);
			

		}
		catch (Exception e)
		{
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}
	
	//function to set the properties for the geometry
	public IGeometry geometryProperties(IGeometry geometry, float scale, Vector3d translationVec, Rotation rotation)
	{
		geometry.setScale(scale);
		geometry.setTranslation(translationVec, true);
		geometry.setRotation(rotation, true);
		
		return geometry;	
	}
	
	/** Render Loop */
	@Override
	public void onDrawFrame()
	{
		super.onDrawFrame();

		// If content not loaded yet, do nothing

		if ( wallGeometry4 == null || towerGeometry4== null || paintballGeometry == null)
			return;
				

		//add movement to paintball until it reaches groundlevel. When the ball reaches groundlevel
		//it becomes invisible and returns to start position and can be triggered again.
		if(paintballGeometry.isVisible())
		{

			// physics calculated with Euler model
			totalForce.setX(gravity.getX());
			totalForce.setY(gravity.getY());
			totalForce.setZ(gravity.getZ());
			
			acceleration.setX(totalForce.getX() / mass);
			acceleration.setY(totalForce.getY() / mass);
			acceleration.setZ(totalForce.getZ() / mass);
			
			velocity.setX(velocity.getX()+timestep*acceleration.getX());
			velocity.setY(velocity.getY()+timestep*acceleration.getY());
			velocity.setZ(velocity.getZ()+timestep*acceleration.getZ());
			
			Vector3d possition = paintballGeometry.getTranslation();
			possition.setX(possition.getX()+timestep*velocity.getX());
			possition.setY(possition.getY()+timestep*velocity.getY());
			possition.setZ(possition.getZ()+timestep*velocity.getZ());
			
			/*possition.setX(velocity.getX());
			possition.setY(velocity.getY());
			possition.setZ(velocity.getZ());*/
			paintballGeometry.setTranslation(possition);
			//paintballGeometry.setTranslation(paintballGeometry.getTranslation().add(velocity));
			if(paintballGeometry.getTranslation().getZ() <= 0f)
			{
				paintballGeometry.setVisible(false);
			}

		}

		// add rotation relative current angel 
		//flowerGeometry.setRotation(new Rotation(0.0f, 0.0f ,0.01f),true);
		
		//onTouchEvent(null);
		
		return;
	}
	
	/** function that activates when an object is being touched*/
	@Override
	protected void onGeometryTouched(IGeometry geometry) 
	{	
		//check if the touched geometry is the canon geometry, if true shot a paintball else do nothing
		if(geometry.equals(canonGeometry1))
		{	
			if(!paintballGeometry.isVisible())
			{
				paintballGeometry.setTranslation(new Vector3d(-600f, -450f, 370f));
				velocity = new Vector3d(200f, 200f, 0f);
				paintballGeometry.setVisible(true);
			}

		}
	}
	
	
	/** function to handle actions when touching the screen */
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		int eventaction = event.getAction();
		
		switch(eventaction)
		{
		case MotionEvent.ACTION_DOWN:
			antGeometry.setTranslation(new Vector3d(100.0f, 0.0f, 0.0f), true);
			break;
			
		case MotionEvent.ACTION_MOVE:
			antGeometry.setTranslation(new Vector3d(100.0f, 0.0f, 0.0f), true);
			break;
			
		case MotionEvent.ACTION_UP:
			antGeometry.setTranslation(new Vector3d(0f,0f,0f), true);
			break;
		}
		
		return true;
	}
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		super.onTouch(v, event);

		mGestureHandler.onTouch(v, event);

		return true;
	}
	

	/** Not used at the moment*/
	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler()
	{
		// No callbacks needed 
		return null;
	}
}
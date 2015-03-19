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

	/*Variabler f�r objekten i spelet*/
	private IGeometry antGeometry;
	private IGeometry sphereGeometry;
	private IGeometry flowerGeometry;
	private IGeometry wallGeometry1;
	private IGeometry wallGeometry2;
	private IGeometry wallGeometry3;
	private IGeometry wallGeometry4;
	private IGeometry towerGeometry;
	private IGeometry canonGeometry;
	private IGeometry paintballGeometry;
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;
	
	Vector3d direction;

	
	/*delkaration av variabler som anv�nds i renderingsloopen*/
	float SphereMoveX = 2f;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mGestureMask  = GestureHandler.GESTURE_ALL;
		mGestureHandler = new GestureHandlerAndroid(metaioSDK,mGestureMask);
		
		//direction of outgoing paintball, will later be based on how you interact with the screen (TODO).
		Vector3d direction =  new Vector3d(0f, 0f, 0f);
		
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
			
			//create a sphere Geometry
			//sphereGeometry = Load3Dmodel("sphere/sphere_10mm.obj");
			//geometryProperties(sphereGeometry, 2f, new Vector3d(100.0f, 0.0f, 0.0f), new Rotation(0.0f, 0.0f ,0.1f));
			//sphereGeometry.setCoordinateSystemID(sphereGeometry.getCoordinateSystemID());
			
			//flowerGeometry = Load3Dmodel("plumBlossom/plum blossom in glass cup_fbx.mfbx");
			//geometryProperties(flowerGeometry, 0.08f, new Vector3d(0.0f, 0.0f, 200.0f), new Rotation(0.0f, 0.0f ,0.1f));
			
			
			//creates the walls around the game area
			wallGeometry1 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry1, 20f, new Vector3d(850f, 0f, 0f), new Rotation(0f, 0f, (float) (3*Math.PI/2)));
			wallGeometry1 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry1, 20f, new Vector3d(-850f, 0f, 0f), new Rotation(0f, 0f, (float) (3*Math.PI/2)));
			wallGeometry1 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry1, 20f, new Vector3d(0f, 720f, 0f), new Rotation(0f, 0f, 0f));
			wallGeometry1 = Load3Dmodel("wall/wall.mfbx");
			geometryProperties(wallGeometry1, 20f, new Vector3d(0f, -720f, 0f), new Rotation(0f, 0f, 0f));
			
			//creates the tower
			towerGeometry = Load3Dmodel("tower/tower.mfbx");
			geometryProperties(towerGeometry, 4f, new Vector3d(-650f, -520f, 0f), new Rotation(0f, 0f, 0f));
			canonGeometry = Load3Dmodel("tower/canon.mfbx");
			geometryProperties(canonGeometry, 4f, new Vector3d(-650f, -520f, 330f), new Rotation(0f, 0f, 0f));
			
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
		if ( wallGeometry1 == null || towerGeometry == null)
			return;
		
		//add movement to paintball until it reaches groundlevel. When the ball reaches groundlevel
		//it becomes invisible and returns to start position and can be triggered again.
		paintballGeometry.setTranslation(paintballGeometry.getTranslation().add(direction));
		if(paintballGeometry.getTranslation().getZ() <= 0f)
		{
			paintballGeometry.setVisible(false);
			paintballGeometry.setTranslation(new Vector3d(-600f, -450f, 370f));
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
		if(geometry.equals(canonGeometry))
		{	
			paintballGeometry.setVisible(true);
			direction = new Vector3d(30f, 30f, -20f);
			
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
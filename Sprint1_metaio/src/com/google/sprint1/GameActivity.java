package com.google.sprint1;

import java.io.File;

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
public class GameActivity extends ARViewActivity
{

	/*Variabler för objekten i spelet*/
	private IGeometry antGeometry;
	private IGeometry sphereGeometry;
	private GestureHandlerAndroid mGestureHandler;
	private int mGestureMask;
	private IGeometry flowerGeometry;
	
	//Variables for Service handling
	NetworkService mService;
	boolean mBound = false;
	
	/*delkaration av variabler som används i renderingsloopen*/
	float SphereMoveX = 2f;
	
	public static final String TAG = "GameActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mGestureMask  = GestureHandler.GESTURE_ALL;
		mGestureHandler = new GestureHandlerAndroid(metaioSDK,mGestureMask);
		
	}
	
	@Override
	protected void onStop() {		
		super.onStop();
		
		//Unbind from service
		if(mBound){
			unbindService(mServiceConnection);
			mBound=false;
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		//Bind to NetworkService
		Log.d(TAG, "Binder");

		Intent intent = new Intent(this, NetworkService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
		stopService(new Intent(this, NetworkService.class));
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
					getApplicationContext(), "marker.zip"); 
			
			// Assigning tracking configuration
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile); 
			
			MetaioDebug.log("Tracking data loaded: " + result);
			
			
			/** Load Object */

			//create ant geometry
			antGeometry = Load3Dmodel("ant/formicaRufa.mfbx");
			geometryProperties(antGeometry, 10f, new Vector3d(-100.0f, 100.0f, 0.0f), new Rotation((float) (3*Math.PI/2), 0f, 0f) );
			
			//create a sphere Geometry
			sphereGeometry = Load3Dmodel("sphere/sphere_10mm.obj");
			geometryProperties(sphereGeometry, 2f, new Vector3d(100.0f, 0.0f, 0.0f), new Rotation(0.0f, 0.0f ,0.1f));
			
			flowerGeometry = Load3Dmodel("plumBlossom/plum blossom in glass cup_fbx.zip");
			geometryProperties(flowerGeometry, 0.08f, new Vector3d(0.0f, 0.0f, 200.0f), new Rotation(0.0f, 0.0f ,0.1f));
			
			//sphereGeometry.setCoordinateSystemID(sphereGeometry.getCoordinateSystemID());			
			
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
		if (sphereGeometry == null || flowerGeometry == null || flowerGeometry == null)
			return;
				
		Vector3d SpherePosition = sphereGeometry.getTranslation();
		if (SpherePosition.getX() >= 300)
		{
			SphereMoveX = -2f;
		}
		else if  (SpherePosition.getX() <= -300)
		{
			SphereMoveX = 2f;
		}
		// add translation relative current position 
		sphereGeometry.setTranslation(new Vector3d(SphereMoveX , 0.0f, 0.0f), true);
		
		// add rotation relative current angel 
		flowerGeometry.setRotation(new Rotation(0.0f, 0.0f ,0.01f),true);
		
		//onTouchEvent(null);
		
		return;
	}
	
	/** function that activates when an object is being touched*/
	@Override
	protected void onGeometryTouched(IGeometry geometry) 
	{
		geometry.setTranslation(new Vector3d(100.0f, 0.0f, 0.0f), true);
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
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
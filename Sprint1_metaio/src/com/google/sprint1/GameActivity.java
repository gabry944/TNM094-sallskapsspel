package com.google.sprint1;

import java.io.File;

import android.util.Log;
import android.view.View;

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
public class GameActivity extends ARViewActivity {


	/*Variabler för objekten i spelet*/
	private IGeometry antGeometry;
	private IGeometry sphereGeometry;
	
	/*delkaration av variabler som används i renderingsloopen*/
	float SphereMoveX = 2f;
	
	/** create a sphere */
	private IGeometry createSphereGeometry()
	{
		final File modelPath = AssetsManager.getAssetPathAsFile(getApplicationContext(), "sphere/sphere_10mm.obj");
		if (modelPath != null)
		{
			IGeometry model = metaioSDK.createGeometry(modelPath);
			if (model != null)
				return model;
			else
				MetaioDebug.log(Log.ERROR, "Error loading geometry: " + modelPath);
		}
		else
			MetaioDebug.log(Log.ERROR, "Could not find 3D model to use as light indicator");

		return null;
	}
	

	
	/** Attaching layout to the activity */
	@Override
	protected int getGUILayout() {
		return R.layout.activity_game;
	}

	/** Called when the user clicks the Exist button (krysset) */
	public void onExitButtonClick(View v) {
		finish();
	}

	/** Loads the marker and the 3D-models to the game */
	@Override
	protected void loadContents() {
		try {
			/** Load Marker */
			// Getting a file path for tracking configuration XML file
			File trackingConfigFile = AssetsManager.getAssetPathAsFile(
					getApplicationContext(), "TrackingData_MarkerlessFast.xml");
			// man kan zippa filerna om man vill att appen ska ta mindre plats! :) 
			
			// Assigning tracking configuration
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile); 
			
			MetaioDebug.log("Tracking data loaded: " + result);
			
			
			/** Load Object */
			// Getting a file path for a 3D geometry
			File antModelFile = AssetsManager.getAssetPathAsFile(
					getApplicationContext(), "ant/formicaRufa.mfbx");
			if (antModelFile != null) {
				// Loading 3D geometry
				antGeometry = metaioSDK.createGeometry(antModelFile);
				if (antGeometry != null) {
					// Set geometry properties

					antGeometry.setScale(20f);
					//antGeometry.setTranslation(new Vector3d(-200.0f, 100.0f, 0.0f), true);
					antGeometry.setRotation(new Rotation((float) (3*Math.PI/2), 0f, 0f), true);
				} else{

					MetaioDebug.log(Log.ERROR, "Error loading geometry: "
							+ antModelFile);
				}
			}
			sphereGeometry = createSphereGeometry();
			sphereGeometry.setTranslation(new Vector3d(100.0f, 0.0f, 0.0f), true);
			//sphereGeometry.setCoordinateSystemID(sphereGeometry.getCoordinateSystemID());			
			
		} catch (Exception e) {
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}
	
	/** Render Loop */
	@Override
	public void onDrawFrame()
	{
		super.onDrawFrame();

		// If content not loaded yet, do nothing
		if (antGeometry == null)
			return;
		
		
		Vector3d SpherePossition = sphereGeometry.getTranslation();
		if (SpherePossition.getX() >= 300)
		{
			SphereMoveX = -2f;
		}
		else if  (SpherePossition.getX() <= -300)
		{
			SphereMoveX = 2f;
		}
		// add translation relative current possition 
		sphereGeometry.setTranslation(new Vector3d(SphereMoveX , 0.0f, 0.0f), true);
		
		// add rotation relative current angel 
		antGeometry.setRotation(new Rotation(0.0f, 0.0f ,0.1f),true);
		
		
		return;
	}
	/** Not used at the moment*/
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
	}

	/** Not used at the moment*/
	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// No callbacks needed 
		return null;
	}
}
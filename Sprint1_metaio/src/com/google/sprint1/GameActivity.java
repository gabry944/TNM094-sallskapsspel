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
	
	//Vector3d vec = new Vector3d(0f, 90f, 0f);
//	Rotation geometryRot = new Rotation(vec);
	
	float[] rotMat = {1.0f, 0.0f, 0.0f,
			  0.0f, 1.0f, 1.0f,
			  0.0f, 0.0f, 1.0f};
	
//	geometryRot.setFromRotationMatrix(rotMat);
	
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
			
			
			/** Load Objects */
			// Getting a file path for a 3D geometry
			File antModel = AssetsManager.getAssetPathAsFile(
					getApplicationContext(), "myra/formicaRufa.mfbx");
			if (antModel != null) {
				// Loading 3D geometry
				IGeometry geometry = metaioSDK.createGeometry(antModel);
				if (geometry != null) {
					// Set geometry properties
					geometry.setScale(10f);
					geometry.setRotation(new Rotation((float) (3*Math.PI/2), 0f, 0f), true);
					//geometry.setTranslation(new Vector3d(100f, 23f, 0f), true);
					
				} else
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "
							+ antModel);
			}
			
		} catch (Exception e) {
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
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
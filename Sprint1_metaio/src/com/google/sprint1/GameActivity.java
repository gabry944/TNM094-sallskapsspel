package com.google.sprint1;

import java.io.File;

import android.util.Log;
import android.view.View;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.tools.io.AssetsManager;

public class GameActivity extends ARViewActivity 
{
	
	@Override
	protected int getGUILayout() 
	{
		// Attaching layout to the activity
		return R.layout.activity_game; 
	}


	public void onButtonClick(View v)
	{
		finish();
	}
	
	
	@Override
	protected void loadContents() 
	{
		try
		{ 
			// Getting a file path for tracking configuration XML file
			File trackingConfigFile = AssetsManager.getAssetPathAsFile(getApplicationContext(), "TrackingData_MarkerlessFast.xml");
			
			// Assigning tracking configuration
			boolean result = metaioSDK.setTrackingConfiguration(trackingConfigFile); // man kan zippa filerna om man vill att apen sk ta mindre plats! :)
			MetaioDebug.log("Tracking data loaded: " + result); 
	        
			// Getting a file path for a 3D geometry
			//File metaioManModel = AssetsManager.getAssetPathAsFile(getApplicationContext(), "metaioman.md2");	
			//File metaioManModel = AssetsManager.getAssetPathAsFile(getApplicationContext(), "SH20_1.obj");
			File metaioManModel = AssetsManager.getAssetPathAsFile(getApplicationContext(), "myra/formicaRufa.mfbx");
			if (metaioManModel != null) 
			{
				// Loading 3D geometry
				IGeometry geometry = metaioSDK.createGeometry(metaioManModel);
				if (geometry != null) 
				{
					// Set geometry properties
					geometry.setScale(4f);
				}
				else
					MetaioDebug.log(Log.ERROR, "Error loading geometry: "+metaioManModel);
			}
		}
		catch (Exception e)
		{
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}


	@Override
	protected void onGeometryTouched(IGeometry geometry)
	{
		// Not used in this tutorial
	}


	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler()
	{
		// No callbacks needed in this tutorial
		return null;
	}	
}
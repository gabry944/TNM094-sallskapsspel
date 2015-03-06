/*package com.google.sprint1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}*/
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
			boolean result = metaioSDK.setTrackingConfiguration("TrackingData_MarkerlessFast.xml"); // man kan zippa filerna om man vill att apen sk ta mindre plats! :)
			MetaioDebug.log("Tracking data loaded: " + result); 
	        
			// Getting a file path for a 3D geometry
			File metaioManModel = AssetsManager.getAssetPathAsFile(getApplicationContext(), "metaioman.md2");			
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
package com.google.sprint1;

import java.io.IOException;

import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.sprint1.R;
import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
	}
	
	/** Called when the user clicks the start Game button (starta spel) */
	public void startNetwork(View view)
	{
		Intent intentNetwork = new Intent(this, NetworkActivity.class);
		startActivity(intentNetwork);
	}

	/** Called when the user clicks the settings button (spelinställningar) */
	public void startSettings(View view)
	{
		Intent intentSettings = new Intent(this, SettingsActivity.class);
		startActivity(intentSettings);
	}
	
}

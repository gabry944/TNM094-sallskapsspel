package com.google.sprint1;

import java.io.IOException;
import java.util.ArrayList;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Activity to handle the screen between network and the gamescreen
 * 
 * where players should connect to each other before entering gamemode.
 */

public class LobbyActivity extends Activity {

	private AssetsExtracter startGame; // a variable used to start the
										// AssetExtraxter class
	private ListView playerListView;
	
	private Button startGameBtn;
	
	public static final String TAG = "LobbyActivity";
	
	private ProgressDialog progressDialog;

	// Function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_lobby);
				
		/* Start game */
		startGame = new AssetsExtracter();
		
		NetworkState.getState().getMobileConnection().initPlayerAdapter(this);
		
		playerListView = (ListView) findViewById(R.id.playerListView);
		
		playerListView.setAdapter(NetworkState.getState().getMobileConnection().getPlayerAdapter());
		
		startGameBtn = (Button) findViewById(R.id.startGame);
		
		//Set progressDialog properties
		progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		progressDialog.setTitle("Loading resources...");
		progressDialog.setCancelable(false);
		
	}

	/** Called when the user clicks the start Game button */
	public void startGame(View view) {
		//Unregister if the registration state is true. 
		//Set mNsdHelper to null;
		//TODO: show dialog that says that you will unregister your game.
		if(NetworkState.getState().getNsdHelper().getRegistrationState()
				&& NetworkState.getState().getNsdHelper() != null){
			NetworkState.getState().getNsdHelper().unregisterService();
        }
		NetworkState.getState().setNsdHelperToNull();
		
		//Start progressDialog
		progressDialog.show();
		//Set button properties
		startGameBtn.setClickable(false);
		startGameBtn.setBackgroundColor(getResources().getColor(R.color.grey));
		// In order to start the game we need to extract our assets to the
		// metaio SDK
		startGame.execute(0); // Starts the assetsExtracter class
	}
	
	/**
	 *  Called when the user clicks the back arrow button 
	 */
	public void backArrow(View view) {
		//Unregister if the registration state is true. 
		//Set mNsdHelper to null;
		//TODO: show dialog that says that you will unregister your game.
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		
		builder.setMessage("If you continue people will no longer be able to connect")
				.setTitle("Go back")
				.setPositiveButton(R.string.BTN_CONTINUE,
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Intent intentmenu = new Intent(LobbyActivity.this, NetworkActivity.class);
						startActivity(intentmenu);
						
					}
				})
				.setNegativeButton(R.string.BTN_CANCEL,
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).create().show();;
		
	}

	/** Called when user minimize the window or clicks home button */
	@Override
	protected void onPause(){	
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		if(NetworkState.getState().getNsdHelper().getRegistrationState()
				&& NetworkState.getState().getNsdHelper() != null){
			NetworkState.getState().getNsdHelper().unregisterService();
        }
		NetworkState.getState().setNsdHelperToNull();
		
		super.onPause();
	}
	
	@Override
	protected void onDestroy(){
		if(progressDialog.isShowing())
			progressDialog.cancel();

		super.onDestroy();
	}


	/**
	 * This task extracts all the assets to an external or internal location to
	 * make them accessible to Metaio SDK.
	 */
	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> 
	{
		/** Extract all assets to make them accessible to Metaio SDK */
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				// Extract all assets except Menu. Overwrite existing files for
				// debug build only.
				final String[] ignoreList = { "Menu", "webkit", "sounds",
						"images", "webkitsec" };
				
				AssetsManager.extractAllAssets(getApplicationContext(), "",
						ignoreList, BuildConfig.DEBUG);
				
			} catch (IOException e) {
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}

		/** when extraction is done, we load the game activity */
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent intent = new Intent(getApplicationContext(),
						GameActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}

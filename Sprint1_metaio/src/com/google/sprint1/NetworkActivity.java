package com.google.sprint1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity to handle the screen between mainmenu and the gamescreen
 * 
 * where players should connect to each other before entering gamemode.
 * 
 * 
 */

public class NetworkActivity extends Activity {

	private AssetsExtracter startGame; // a variable used to start the
										// AssetExtraxter class

	private Handler mUpdateHandler;
	NsdHelper mNsdHelper;
	MobileConnection mConnection;

	ArrayAdapter<NsdServiceInfo> listAdapter;

	public static final String TAG = "NetworkActivity";

	// function to set up layout of activity
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_network);

		/* Start game */
		startGame = new AssetsExtracter();

		mUpdateHandler = new Handler();
		mConnection = new MobileConnection(mUpdateHandler);
		mNsdHelper = new NsdHelper(this);

		mNsdHelper.initializeNsd();

		ListView listView = (ListView) findViewById(R.id.serviceView);

		listAdapter = new ArrayAdapter<NsdServiceInfo>(this,
				android.R.layout.simple_list_item_1,
				new ArrayList<NsdServiceInfo>());
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView parent, View view,
					final int pos, long id) {

				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NetworkActivity.this);

				// 2. Chain together various setter methods to set the dialog
				// characteristics
				builder.setMessage(
						"Connect to "
								+ listAdapter.getItem(pos).getServiceName()
								+ "?")
						.setTitle("Connect")
						.setPositiveButton(R.string.BTN_OK,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										NsdServiceInfo service = listAdapter
												.getItem(pos);
										service = mNsdHelper
												.resolveService(service);
										if (service != null) {
											Log.d(TAG, "Connecting to: "
													+ service.getServiceName());
											mConnection.connectToServer(
													service.getHost(),
													service.getPort());
										} else {
											Log.d(TAG,
													"No service to connect to!");
										}

									}
								})
						.setNegativeButton(R.string.BTN_CANCEL,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								});
				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
			}

		});

	}


	public void clickDiscover(View v) {

		mNsdHelper.discoverServices();
		updateAdapter(v);
	}

	/** Called when the user clicks the start Game button (starta spel) */
	public void startGame(View view) {
		// in order to start the game we need to extract our assets to the
		// metaio SDK
		startGame.execute(0); // Starts the assetsExtracter class
	}

	public void updateAdapter(View view) {
		listAdapter.clear();
		for (int i = 0; i < mNsdHelper.getFoundServices().size(); i++) {
			listAdapter.add(mNsdHelper.getFoundServices().get(i));
		}
		listAdapter.notifyDataSetChanged();

	}

	/** Called when the user clicks the mainMenu button (huvudmeny) */
	public void mainMenu(View view) {
		Intent intentmenu = new Intent(this, MainActivity.class);
		startActivity(intentmenu);
	}

	public void sendData(View view) {
		TestClass test = new TestClass(5, "hej");
		Log.d(TAG, "sendData clicked!");
		mConnection.sendData(test);

	}

	@Override
	protected void onPause() {

		if (mNsdHelper != null) {

			Log.d(TAG, "Pausad");
			mNsdHelper.tearDown();
			mNsdHelper = null;
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mNsdHelper = new NsdHelper(this);
		mNsdHelper.initializeNsd();
		if (mNsdHelper != null) {
			Log.d(TAG, "Resumed");

			mNsdHelper.registerService(mConnection.getLocalPort());
			mNsdHelper.discoverServices();

		}

	}

	protected void onDestroy() {

		Log.d(TAG, "Destroyed");
		if (mNsdHelper != null) {
			mNsdHelper.tearDown();
			mNsdHelper = null;
		}
		mConnection.tearDown();
		super.onDestroy();
	}

	/**
	 * This task extracts all the assets to an external or internal location to
	 * make them accessible to Metaio SDK.
	 */

	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean> {
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
				// AssetsManager.extractAllAssets(getApplicationContext(),
				// BuildConfig.DEBUG);
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

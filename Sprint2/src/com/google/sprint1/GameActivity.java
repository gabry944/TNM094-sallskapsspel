package com.google.sprint1;

import java.io.File;
import java.util.ArrayList;

import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

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
public class GameActivity extends ARViewActivity //implements OnGesturePerformedListener
{

	private static final String TAG = "myLog";
	

	/*Variables for objects in the game*/
	private IGeometry antGeometry1;
	private IGeometry antGeometry2;
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
	private IGeometry crosshair;

	PaintBall paint_ball_object;
	private ArrayList<PaintBall> exsisting_paint_balls;
	
	private Vector3d startTouch;
	private Vector3d currentTouch;
	private Vector3d endTouch;
	private Vector3d touchVec; 		//endTouch-startTouch
	
	//to enable gesture tracking
	protected GridView surfaceView;
    protected GestureOverlayView gestureOverlayView;
    protected FrameLayout frameLayout;
	
	// point count
    protected int point; 
    TextView displayPoints;

	//Variables for physics calibration
	Vector3d acceleration;
	Vector3d velocity;
	Vector3d totalForce;
	Vector3d gravity;
	float timeStep;
	float mass;
	
	float temp;
	
	
	/** Attaching layout to the activity */
	@Override
	public int getGUILayout()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return R.layout.activity_game;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		
		exsisting_paint_balls = new ArrayList<PaintBall>(20);

		displayPoints = (TextView) findViewById(R.id.myPoints);
		
		//velocity and direction of outgoing paintball, will later be based on how you interact with the screen (TODO).
		acceleration =  new Vector3d(0f, 0f, 0f);
		velocity =  new Vector3d(0f, 0f, 0f);
		totalForce =  new Vector3d(0f, 0f, 0f);
		gravity = new Vector3d(0f, 0f, -9.82f);
		timeStep = 0.2f;								//0.1s
		mass = 0.1f;		   							//0.1kg

		touchVec =  new Vector3d(0f, 0f, 0f);
		currentTouch = new Vector3d(0f, 0f, 0f);
		startTouch =  new Vector3d(0f, 0f, 0f);
		endTouch =  new Vector3d(0f, 0f, 0f);
		
		temp = 20f;
		point = 0;
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

	/** move an object depending on physics calculated with Euler model*/
	private void physicPositionCalibration(PaintBall object)
	{
		// right now we only have gravity as force
		totalForce.setX(gravity.getX() * mass);
		totalForce.setY(gravity.getY() * mass);
		totalForce.setZ(gravity.getZ() * mass);
		
		// Newtons second law says that: F=ma => a= F/m
		acceleration.setX(totalForce.getX() / mass);
		acceleration.setY(totalForce.getY() / mass);
		acceleration.setZ(totalForce.getZ() / mass);
		
		// Euler method gives that Vnew=V+A*dt;
		object.velocity.setX(object.velocity.getX()+timeStep*acceleration.getX());
		object.velocity.setY(object.velocity.getY()+timeStep*acceleration.getY());
		object.velocity.setZ(object.velocity.getZ()+timeStep*acceleration.getZ());
		
		// Euler method gives that PositionNew=Position+V*dt;
		Vector3d position = object.geometry.getTranslation();
		position.setX(position.getX()+timeStep*object.velocity.getX());
		position.setY(position.getY()+timeStep*object.velocity.getY());
		position.setZ(position.getZ()+timeStep*object.velocity.getZ());
		
		// move object to the new position
		object.geometry.setTranslation(position);
		//object.setTranslation(object.getTranslation().add(velocity*timeStep));
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
					getApplicationContext(), "marker/TrackingData_PictureMarker.xml"); 
			
			// Assigning tracking configuration
			boolean result = metaioSDK
					.setTrackingConfiguration(trackingConfigFile); 
			
			MetaioDebug.log("Tracking data loaded: " + result);				
			
			/** Load Object */

			//create ant geometry
			antGeometry1 = Load3Dmodel("ant/formicaRufa.mfbx");
			geometryProperties(antGeometry1, 10f, new Vector3d(-100.0f, 100.0f, 0.0f), new Rotation(0f, 0f, 0f) );	
			antGeometry2 = Load3Dmodel("ant/formicaRufa.mfbx");
			geometryProperties(antGeometry2, 10f, new Vector3d(100.0f, -100.0f, 0.0f), new Rotation(0f, 0f, 0f) );
			
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
			
			//Load crosshair
			crosshair = Load3Dmodel("crosshair/crosshair.mfbx");
			geometryProperties(crosshair, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
			crosshair.setVisible(false);
			//creates a list of paint balls
			for(int i = 0; i < 20; i++)
			{
				// create new paint ball
				paint_ball_object = new PaintBall();
				
				// add properties to the paint ball
				paint_ball_object.geometry = Load3Dmodel("tower/paintball.obj");
				paint_ball_object.splashGeometry = Load3Dmodel("tower/splash.mfbx");
				paint_ball_object.velocity = new Vector3d(0f, 0f, 0f);
				geometryProperties(paint_ball_object.geometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
				geometryProperties(paint_ball_object.splashGeometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
				paint_ball_object.geometry.setVisible(false);	
				paint_ball_object.splashGeometry.setVisible(false);
				
				// add paint ball to list of paint balls
				exsisting_paint_balls.add(paint_ball_object);
			}
		}
		catch (Exception e)
		{
			MetaioDebug.printStackTrace(Log.ERROR, e);
		}
	}
	
	//function to set the properties for the geometry
	public void geometryProperties(IGeometry geometry, float scale, Vector3d translationVec, Rotation rotation)
	{
		geometry.setScale(scale);
		geometry.setTranslation(translationVec, true);
		geometry.setRotation(rotation, true);
	}
	
	/** Render Loop */
	@Override
	public void onDrawFrame()
	{
		super.onDrawFrame();

		// If content not loaded yet, do nothing

		if ( wallGeometry4 == null || towerGeometry4== null || exsisting_paint_balls.isEmpty())
			return;
		
		//Log.d(TAG, "touchVec = "+ touchVec);
		//antGeometry.setTranslation(touchVec, true);
		
		//move ant
		if (antGeometry1.getTranslation().getX() < -350f)
			temp = 2f;
		else if (antGeometry1.getTranslation().getX() > 350f)
			temp = -2f;
		
		antGeometry1.setTranslation(new Vector3d(temp, temp, 0.0f), true);
		antGeometry2.setTranslation(new Vector3d(-temp, -temp, 0.0f), true);

		if (!exsisting_paint_balls.isEmpty())
		{
			for(PaintBall obj : exsisting_paint_balls)
			{
				if(obj.geometry.isVisible()) 
				{
					// move object one frame
					physicPositionCalibration(obj);
					//Log.d(TAG, "Zvalue =" + obj.geometry.getTranslation().getZ());
					
						
					if(checkCollision( obj, antGeometry1))
					{
						antGeometry1.setRotation(new Rotation((float) (3*Math.PI/4), 0f, 0f),true);
						obj.splashGeometry.setTranslation(obj.geometry.getTranslation());
						obj.splashGeometry.setVisible(true);
						obj.velocity = new Vector3d(0f, 0f, 0f);
						obj.geometry.setVisible(false);
						point++;
						//displayPoints = (TextView)findViewById(R.id.myPoints);
						//displayPoints.setText("Ponts:" + point);
						//displayPoints = (TextView)findViewById(R.id.editText1);
						//(TextView)findViewById(R.id.editText1).setText("Ponts:" + point);
					}
					
					if(checkCollision( obj, antGeometry2))
					{
						antGeometry2.setRotation(new Rotation((float) (3*Math.PI/4), 0f, 0f),true);
						obj.splashGeometry.setTranslation(obj.geometry.getTranslation());
						obj.splashGeometry.setVisible(true);
						obj.velocity = new Vector3d(0f, 0f, 0f);
						obj.geometry.setVisible(false);
						point++;		
					}
									
					// checks for collision with ground 	
					if(obj.geometry.getTranslation().getZ() <= 0f)
					{	
						obj.splashGeometry.setTranslation(obj.geometry.getTranslation());
						obj.velocity = new Vector3d(0.0f, 0.0f, 0.0f);
						obj.geometry.setTranslation(new Vector3d(0f,0f,0f));
						obj.splashGeometry.setVisible(true);
						obj.velocity = new Vector3d(0f, 0f, 0f);
						obj.geometry.setVisible(false);
						
						Log.d(TAG, "before display =");
						displayPoints.setText("Test"); //måste laga detta fungerar ej (TODO)
						Log.d(TAG, "after display =");

						//TextView newtext = (TextView) findViewById(R.id.editText1);
					}
				}
			}
		}
		//onTouchEvent(null);
	}
	
	public boolean checkCollision(PaintBall obj, IGeometry obj2)
	{
		Vector3d min = obj2.getBoundingBox(true).getMin();
		Vector3d max = obj2.getBoundingBox(true).getMax();

		if (obj.geometry.getTranslation().getX() + obj.geometry.getBoundingBox().getMax().getX() > obj2.getTranslation().getX() + 2 * min.getX() -100 && 
			obj.geometry.getTranslation().getX() + obj.geometry.getBoundingBox().getMin().getX() < obj2.getTranslation().getX() + 2 * max.getX() +100 &&
			obj.geometry.getTranslation().getY() + obj.geometry.getBoundingBox().getMax().getY() > obj2.getTranslation().getY() + 2 * min.getY() -100 && 
			obj.geometry.getTranslation().getY() + obj.geometry.getBoundingBox().getMin().getY() < obj2.getTranslation().getY() + 2 * max.getY() +100 &&
			obj.geometry.getTranslation().getZ() + obj.geometry.getBoundingBox().getMax().getZ() > obj2.getTranslation().getZ() + 2 * min.getZ() -100 && 
			obj.geometry.getTranslation().getZ() + obj.geometry.getBoundingBox().getMin().getZ() < obj2.getTranslation().getZ() + 2 * max.getZ() +100 )

			return true;
		else 
			return false;
	}
	
	/** function that activates when an object is being touched*/
	@Override
	protected void onGeometryTouched(IGeometry geometry) 
	{	
		// Only implemented because its required by the parent class
	}

    @Override
    /** Function to handle touch input */
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        int action = event.getActionMasked();      

        switch(action) {
            case MotionEvent.ACTION_DOWN:

                startTouch = new Vector3d((event.getX()), event.getY(), 0f);
                //Log.d(TAG, "startTouch ="+ startTouch);
                crosshair.setVisible(true);
                break;
            case MotionEvent.ACTION_MOVE:
            	//gives the coordinates of your finger touch all the time to be able to calculate a crosshair 
            	currentTouch = new Vector3d(-(event.getX()-startTouch.getX()),
						  event.getY() -startTouch.getY(),
						  0f);
            	
            	crosshair.setTranslation(new Vector3d( -600f+8.4f*currentTouch.getX(),
            										   -450f+8.4f*currentTouch.getY(),
            										   0f));
            	
            	
            	Log.d(TAG, "currentTouch = " + currentTouch);
            
                break;
            case MotionEvent.ACTION_UP:
            	endTouch = new Vector3d((event.getX()), event.getY(), 0f);
            	touchVec = new Vector3d(-(endTouch.getX()-startTouch.getX()),
            							  endTouch.getY() -startTouch.getY(),
            							  0f);
            	crosshair.setVisible(false);

            	//Log.d(TAG, "endTouch ="+ endTouch);
            	//Log.d(TAG, "touchVec = " + touchVec);
        		if(!paint_ball_object.geometry.isVisible())
        		{
            		paint_ball_object.geometry.setTranslation(new Vector3d(-600f, -450f, 370f));
        			paint_ball_object.velocity = touchVec;
                	//Log.d(TAG, "vel = " + paint_ball_object.velocity);
        			
        			paint_ball_object.geometry.setVisible(true);
            	break;
        		}
        }
        return true;
    }

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	/** Not used at the moment*/
	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler()
	{
		// No callbacks needed 
		return null;
	}

}
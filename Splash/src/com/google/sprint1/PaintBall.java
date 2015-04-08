package com.google.sprint1;


import android.app.Activity;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.tools.io.AssetsManager;

public class PaintBall extends Drawable
{
		public Vector3d velocity;
		public Vector3d direction;
		public IGeometry geometry; 
		public IGeometry splashGeometry;
		public IGeometry paintballShadow;
		public int ownerID;
		
		public PaintBall(Activity act, IGeometry geo, IGeometry splGeo, IGeometry pbShad) {
			super(act);
			geometry = geo;
			splashGeometry = splGeo;
			paintballShadow = pbShad;
			
			velocity= new Vector3d(0.0f,0.0f,0.0f);
			direction = new Vector3d(0.0f, 0.0f, 0.0f);
			
			setGeometryProperties(geometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
			setGeometryProperties(splashGeometry, 2f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
			setGeometryProperties(paintballShadow, 0.7f, new Vector3d(0f, 0f, 0f), new Rotation((float) (3*Math.PI/2), 0f, 0f));
			geometry.setVisible(false);	
			splashGeometry.setVisible(false);
			paintballShadow.setVisible(false);
		}
		
		
		
		
}	

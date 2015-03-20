package com.google.sprint1;


import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Vector3d;

public class PaintBall 
{
		public Vector3d velocity;
		public Vector3d direction;
		public IGeometry geometry; 
		
		public PaintBall() {
			geometry = null;
			velocity= new Vector3d(0.0f,0.0f,0.0f);
			direction = new Vector3d(0.0f, 0.0f, 0.0f);
		}
}	

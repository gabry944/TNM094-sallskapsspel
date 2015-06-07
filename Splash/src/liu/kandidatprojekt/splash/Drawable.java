package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;


public class Drawable {

	protected Vector3d startPosition;
	protected Vector3d startVelocity;
	protected long startTime;
	
	public Drawable(){
		
	}
	
	//function to set the properties for the geometry
	public void setGeometryProperties(IGeometry geometry, float scale, Vector3d translationVec, Rotation rotation)
	{
		geometry.setScale(scale);
		geometry.setTranslation(translationVec, true);
		geometry.setRotation(rotation, true);
	}
	
	
	
}

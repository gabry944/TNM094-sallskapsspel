package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Vector3d;

public class PowerUp extends Drawable {
	
	public IGeometry geometry;
	
	public PowerUp(IGeometry geo){
		geometry = geo;
	}
	
	public void update(){
		
		float scaleStart = 0f;
		/** Function for animation on the powerup */
			if (geometry.getScale().getX() > 2.0f) {
				scaleStart = -0.02f;
			}
			if (geometry.getScale().getX() < 1.0f) {
				scaleStart = 0.02f;
			}
			geometry.setScale(geometry.getScale().add(
					new Vector3d(scaleStart, scaleStart, scaleStart)));
			// Log.d(TAG, "scale = " + powerUp.getScale());
	}
	
	
}

package com.google.sprint1;

import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.Rotation;
import com.metaio.sdk.jni.Vector3d;

public class PowerUp extends Drawable {
	
	private IGeometry geometry;
	private boolean visible;
	
	public PowerUp(IGeometry geo){
		geometry = geo;
		visible = true;
		setGeometryProperties(geometry, 2.1f, new Vector3d(0f, 0f, 0f), new Rotation(0f, 0f, 0f));
	}
	
	public IGeometry getGeometry()
	{
		return geometry;
	}
	
	public void setHit()
	{
		visible = false;
	}
	
	public boolean isHit()
	{
		if (visible)
			return false;
		else 
			return true;
	}
	
	public void update()
	{	
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
			if(visible == false)
				geometry.setVisible(false);
	}
	
	
}

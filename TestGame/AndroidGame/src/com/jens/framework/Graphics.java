package com.jens.framework;

import android.graphics.Paint;

//Contains methods that will be used to draw images to the screen
public interface Graphics {
	public static enum ImageFormat{
		ARGB8888, ARGB4444, RGB565
	}
	
	public Image newImage(String fileName, ImageFormat format);
	
	//Fill entire screen with color
	public void clearScreen(int color);
	
	public void drawLine(int x, int y, int x2, int y2, int color);
	
	public void drawRect(int x, int y, int width, int height, int color);
	
	public void drawImage(Image image, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight);
	
	public void drawImage(Image Image, int x, int y);
	
	void drawString(String text, int x, int y, Paint paint);
	
	public int getWidth();
	
	public int getHeight();
	
	//ARGB = Alpha, Red, Green och Blue
	public void drawARGB(int i, int j, int k, int l);
}

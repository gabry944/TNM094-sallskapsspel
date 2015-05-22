package com.google.sprint1;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class SoundEffect {
	
	static AssetManager assets;
	static boolean loaded = false;
	protected Context context;
	static int soundId;
	static SoundPool soundPool;
	private static HashMap<Integer, Integer> soundsMap;
	
	public static void playSound(Context context, int sID){
		
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		//final int soundIds[] = new int[10];
		soundsMap = new HashMap<Integer, Integer>();
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){

		@Override
		public void onLoadComplete(SoundPool soundPool, int sampleId,
				int status) {
				loaded = true;
				Log.e("Loaded", "Loaded");
				}
			
		});
		soundsMap.put(1, soundPool.load(context, R.raw.splash, 1));
		//soundsMap.put(2, soundPool.load(context, R.raw.bubble, 1));
		
		if(loaded){
		//soundPool.play(soundIds[sID], 1, 1, 1, 0, 1.0f);
		//soundPool.unload(soundIds[sID]);
		soundPool.play(soundsMap.get(sID), 1, 1, 1, 0, 1.0f);
		soundPool.unload(soundsMap.get(sID));
		}
		}

}

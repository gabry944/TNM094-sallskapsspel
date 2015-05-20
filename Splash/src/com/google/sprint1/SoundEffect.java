package com.google.sprint1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class SoundEffect {
	
	static boolean loaded = false;
	protected Context context;
	
	public static void playSound(Context context){
		
		SoundPool soundPool;
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		final int soundIds[] = new int[10];
		
		//Test test = new Test(this);	
		soundIds[0] = soundPool.load(context, R.raw.splash, 1);
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				soundPool.play(soundIds[0], 1, 1, 1, 0, 1.0f);
				soundPool.unload(soundIds[0]);
				
			}
			
		});
		
		}

}

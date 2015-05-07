package com.google.sprint1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class Sound {
	
	static boolean loaded = false;
	protected Context context;
	
	public static void playSound(Context context){
		
		SoundPool soundPool;
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		final int soundIds[] = new int[10];
		
		//Test test = new Test(this);
			
		soundIds[0] = soundPool.load(context, R.raw.ping, 1);
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				soundPool.play(soundIds[0], 1, 1, 1, 0, 1.0f);
				loaded = true;
				
			}
			
		});
			
		Log.e("Test", "soundID before = " + soundIds[0]);
		Log.e("Test", "Loaded = " + loaded);
		
		}
	
	public static void playBackgroundMusic(Context context){
	
	MediaPlayer backgroundMusic;
	backgroundMusic = MediaPlayer.create(context, R.raw.good);
	
	backgroundMusic.setLooping(true);
	backgroundMusic.setVolume(10.0f, 3.0f);
	backgroundMusic.start();
	
	}
	
	public static void playsoundEffect(int soundID){
		
		SoundPool soundPool;
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		
		Log.e("Test", "soundID play = " + soundID);
		
		soundPool.play(soundID, 1, 1, 1, 0, 1.0f);
	}

}

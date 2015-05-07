package com.google.sprint1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class Sound {
	
	static boolean loaded = false;
	
	public static int[] loadSound(Context context){
		
		SoundPool soundPool;
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		int soundIds[] = new int[10];
		soundIds[0] = soundPool.load(context, R.raw.ping, 1);
		
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
				
			}
			
		});
		
		Log.e("Test", "soundID before = " + soundIds[0]);
		Log.e("Test", "Loaded = " + loaded);
		
		return soundIds;
		
		}
	
	public static void playBackgroundMusic(Context context){
	
	MediaPlayer backgroundMusic;
	backgroundMusic = MediaPlayer.create(context, R.raw.good);
	
	backgroundMusic.setLooping(true);
	backgroundMusic.setVolume(10.0f, 3.0f);
	backgroundMusic.start();
	
	}
	
	public static void playsoundEffect(int soundID){
		
		
		//Vill använda SoundPool istället för mediaplayer
		/*SoundPool sp;
		
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		
		int soundIds[] = new int[10];
		soundIds[0] = sp.load(context, R.raw.ping, 1);
		
		sp.play(soundIds[0], 1, 1, 1, 0, (float) 1.0);*/
		
		/*MediaPlayer backgroundMusic;
		backgroundMusic = MediaPlayer.create(context, R.raw.ping);
		
		backgroundMusic.setLooping(false);
		backgroundMusic.setVolume(10.0f, 3.0f);
		backgroundMusic.start();*/
		
		SoundPool soundPool;
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		
		Log.e("Test", "soundID play = " + soundID);
		
		soundPool.play(soundID, 1, 1, 1, 0, 1.0f);
	}

}

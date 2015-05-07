package com.google.sprint1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Sound {
	
	
	public static void playBackgroundMusic(Context context){
	
	MediaPlayer backgroundMusic;
	backgroundMusic = MediaPlayer.create(context, R.raw.good);
	
	backgroundMusic.setLooping(true);
	backgroundMusic.setVolume(10.0f, 3.0f);
	backgroundMusic.start();
	
	}
	
	public static void playsoundEffect(Context context){
		
		
		//Vill använda SoundPool istället för mediaplayer
		/*SoundPool sp;
		
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		
		int soundIds[] = new int[10];
		soundIds[0] = sp.load(context, R.raw.ping, 1);
		
		sp.play(soundIds[0], 1, 1, 1, 0, (float) 1.0);*/
		
		MediaPlayer backgroundMusic;
		backgroundMusic = MediaPlayer.create(context, R.raw.ping);
		
		backgroundMusic.setLooping(false);
		backgroundMusic.setVolume(10.0f, 3.0f);
		backgroundMusic.start();
	}

}

package com.google.sprint1;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundMusic {
	
	public static void playBackgroundMusic(Context context){
		
		MediaPlayer backgroundMusic;
		backgroundMusic = MediaPlayer.create(context, R.raw.good);
		
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(10.0f, 3.0f);
		backgroundMusic.start();
		
		}

}

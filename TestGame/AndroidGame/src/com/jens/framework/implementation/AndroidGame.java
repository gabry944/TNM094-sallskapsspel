package com.jens.framework.implementation;

/* All cred to James Cho
http://www.kilobolt.com/create-an-android-game-from-scratch-or-port-your-existing-game.html
*/
//Android based classes stored in the SDK Plattform  http://developer.android.com/reference
import android.app.Activity;	//Interactive window, made up of components called Views
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;	//Lets you pass information between multiple Activities
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;	//Prevetns phone from going to sleep
import android.view.Window;
import android.view.WindowManager;

import com.jens.framework.Audio;
import com.jens.framework.FileIO;
import com.jens.framework.Game;
import com.jens.framework.Graphics;
import com.jens.framework.Input;
import com.jens.framework.Screen;

//Main file that holds everything together
public abstract class AndroidGame extends Activity implements Game {
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int frameBufferWidth = isPortrait ? 800: 1280;	//set Width of game, questionmark is a form of boolean
        int frameBufferHeight = isPortrait ? 1280: 800;	//set Height of game
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);
        
        //Scale to device's aspect ratio
        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getInitScreen();
        setContentView(renderView);
        
        //To define the wakeLock variable and require and releaser wakelock (onReusem/onPause)
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyGame");
    }

    @Override	//Belongs to the superclass
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }
    
    public Screen getCurrentScreen() {

        return screen;
    }
}
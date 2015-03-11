package com.jens.framework;

public interface Music {
    public void play();

    public void stop();

    public void pause();

    public void setLooping(boolean looping);

    public void setVolume(float volume);

    public boolean isPlaying();

    public boolean isStopped();

    public boolean isLooping();
    
    //removes music file, allows it to be removed from memory
    public void dispose();

    void seekBegin();
}

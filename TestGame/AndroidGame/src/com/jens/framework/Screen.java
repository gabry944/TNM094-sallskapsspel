package com.jens.framework;

public abstract class Screen {
	protected final Game game;
	
	public Screen(Game game){
		this.game = game;
	}
	
	//deltaTime how much time passed since the last time it
	//was called
	public abstract void update(float deltaTime);
	
	public abstract void paint(float deltaTime);
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void dispose();
	
	public abstract void backButton();
}

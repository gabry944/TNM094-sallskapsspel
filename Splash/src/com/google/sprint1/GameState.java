package com.google.sprint1;

import java.util.ArrayList;

public class GameState {
	
	private static GameState instance = null;
	
	
	public ArrayList<PaintBall> exsisting_paint_balls;
	public ArrayList<Player> players;
	
	protected GameState() {
		
	}
	
	public synchronized static GameState getState() {
		if (instance == null){
			instance = new GameState();
			instance.init();
		}
		return instance;
	}
	
	private void init(){
		players = new ArrayList<Player>();
		players.add(new Player(1));
		players.add(new Player(2));
		players.add(new Player(3));
		players.add(new Player(4));
	}
	
}

package com.google.sprint1;

import java.util.ArrayList;

public class GameState {
	
	private static GameState instance = null;
	
	public int myPlayerID = 0;
	public ArrayList<PaintBall> exsisting_paint_balls;
	public ArrayList<Player> players;
	public ArrayList<Ant> ants;
	public ArrayList<PowerUp> powerUps;
	
	protected GameState() {
		
	}
	
	public synchronized static GameState getState() {
		if (instance == null){
			instance = new GameState();
			instance.init();
		}
		return instance;
	}
	
	/** Initialize the Game State, currently called the first time getState is called*/
	private void init(){
		powerUps = new ArrayList<PowerUp>();
		
		players = new ArrayList<Player>();/*
		players.add(new Player(1));
		players.add(new Player(2));
		players.add(new Player(3));
		players.add(new Player(4));*/
	}
	
}

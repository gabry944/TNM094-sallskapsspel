package com.google.sprint1;

import java.util.ArrayList;

public class GameState {
	
	private static GameState instance = null;
	
	// Timer for game round TODO sync between units playing the game
	public long gameStartTime;
	public long gameTimeLeft;
	
	public int myPlayerID = 0;
	public int nrOfPlayers = 1;
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
		
		players = new ArrayList<Player>();
	}
	
	public void addPlayer(Player p){
		players.add(p);
	}	
	public void updateTime() {
		// 3 min game round (5 min quit long)
		gameTimeLeft = 1*60*1000 -(System.currentTimeMillis() - gameStartTime);
	}
	public String timeToString() {
		String result;
		result = ""+ (int) gameTimeLeft/60000 + ":" + (int) (gameTimeLeft%60000)/1000;
		return result;
	}
}

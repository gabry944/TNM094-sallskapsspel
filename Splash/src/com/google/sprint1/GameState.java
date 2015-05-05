package com.google.sprint1;

import java.util.ArrayList;

public class GameState {
	
	private static GameState instance = null;
	
	// Timer for game round TODO sync between units playing the game
	public long gameStartTime;
	public long gameTimeLeft;
	
	public int myPlayerID = 0;
	public int nrOfPlayers = 1;
	
	public MobileConnection connection;
	
	public ArrayList<PaintBall> paintBalls;
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
	
	public void resetGameState(){
		for(Player p : players){
			p.score = 0;
		}
		for (Ant a : ants)
		{
			a.setActive(false);
		}
		for (PowerUp pup : powerUps)
		{
			pup.setHit(false);
		}
		for (PaintBall ball : paintBalls)
		{
			ball.deactivate();
		}
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

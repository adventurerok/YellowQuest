package com.ithinkrok.yellowquest.challenge;

import java.util.ArrayList;
import java.util.HashMap;

import com.ithinkrok.yellowquest.GameData;

public class StatTracker {
	
	public static enum Stat{
		RIGHT_SIDE_FLAG, //Tracked
		JUMP_OVER_BOXES, //Tracked
		COMPLETE_LEVEL, //Tracked
		JUMPS, //Tracked
		LEFT_DISTANCE, //Tracked
		DEATHS, //Tracked
		BONUS
	}
	
	private static ArrayList<String> statNames = new ArrayList<String>();
	
	public StatTracker(GameData gameData) {
		super();
		this.gameData = gameData;
	}

	static{
		Stat[] all = Stat.values();
		for(Stat s : all){
			statNames.add(s.name());
		}
	}
	
	//Track stats since start or last challenge gained
	GameData gameData;
	
	//Track stats since the start of the game/life/level
	private HashMap<String, Integer> currentGame = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentLife = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentLevel = new HashMap<String, Integer>();
	
	public void resetLevel(){
		currentLevel.clear();
	}
	
	public void resetLife(){
		currentLife.clear();
		currentLevel.clear();
	}
	
	public void resetGame(){
		resetLife();
		currentGame.clear();
	}
	
	public void resetChallenge(){
		resetGame();
		for(String s: statNames){
			gameData.resetStatChallenge(s);
		}
	}
	
	public void addStat(Stat stat, int add){
		Integer old;
		
		old = currentGame.get(stat.name());
		if(old == null) old = 0;
		currentGame.put(stat.name(), old + add);
		
		old = currentLife.get(stat.name());
		if(old == null) old = 0;
		currentLife.put(stat.name(), old + add);
		
		old = currentLevel.get(stat.name());
		if(old == null) old = 0;
		currentLevel.put(stat.name(), old + add);
		
		gameData.addStat(stat.name(), add);
	}

}

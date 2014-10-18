package com.ithinkrok.yellowquest.challenge;

import java.util.ArrayList;
import java.util.HashMap;

import com.ithinkrok.yellowquest.GameData;

public class StatTracker {
	
	private static interface ChallengeInfo {
		
		public Challenge createChallenge(StatTracker tracker);
	}
	
	public static class BasicChallengeInfo implements ChallengeInfo{
		
		private Stat stat;
		private StatType type;
		private int target;
		
		

		public BasicChallengeInfo(Stat stat, StatType type, int target) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
		}



		@Override
		public Challenge createChallenge(StatTracker tracker) {
			return new BasicChallenge(tracker, stat, type, target);
		}
		
	}
	
	public void completeLevel(){
		if(currentChallenge == null) return;
		currentChallenge.completeLevel();
	}
	
	public void lostLife(){
		if(currentChallenge == null) return;
		currentChallenge.lostLife();
	}
	
	public void gameOver(){
		if(currentChallenge == null) return;
		currentChallenge.gameOver();
	}
	
	private static ArrayList<ChallengeInfo> challenges = new ArrayList<StatTracker.ChallengeInfo>();

	private static ArrayList<String> statNames = new ArrayList<String>();
	
	public Challenge currentChallenge;
	

	public StatTracker(GameData gameData) {
		super();
		this.gameData = gameData;
	}

	static {
		Stat[] all = Stat.values();
		for (Stat s : all) {
			statNames.add(s.name());
		}
		
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.CHALLENGE, 25));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.CHALLENGE, 5));
		
	}

	// Track stats since start or last challenge gained
	GameData gameData;

	// Track stats since the start of the game/life/level
	private HashMap<String, Integer> currentGame = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentLife = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentLevel = new HashMap<String, Integer>();

	private int getIntFromMap(HashMap<String, Integer> map, String key) {
		Integer r = map.get(key);
		if (r == null)
			return 0;
		return r;
	}

	public void resetLevel() {
		currentLevel.clear();
	}

	public void resetLife() {
		currentLife.clear();
		currentLevel.clear();
	}

	public void resetGame() {
		resetLife();
		currentGame.clear();
	}
	
	

	public void resetChallenge() {
		resetGame();
		for (String s : statNames) {
			gameData.resetStatChallenge(s);
		}
	}

	public void addStat(Stat stat, int add) {
		Integer old;

		old = currentGame.get(stat.name());
		if (old == null)
			old = 0;
		currentGame.put(stat.name(), old + add);

		old = currentLife.get(stat.name());
		if (old == null)
			old = 0;
		currentLife.put(stat.name(), old + add);

		old = currentLevel.get(stat.name());
		if (old == null)
			old = 0;
		currentLevel.put(stat.name(), old + add);

		gameData.addStat(stat.name(), add);
		
		if(currentChallenge != null && currentChallenge.stat == stat) currentChallenge.update();
	}

	public int getStat(Stat stat, StatType type) {
		switch (type) {
		case TOTAL:
			return gameData.getStat(stat.name());
		case CHALLENGE:
			return gameData.getStatChallenge(stat.name());
		case GAME:
			return getIntFromMap(currentGame, stat.name());
		case LEVEL:
			return getIntFromMap(currentLevel, stat.name());
		case LIFE:
			return getIntFromMap(currentLife, stat.name());
		default:
			return 0;
		}
	}
	
	public void nextChallenge(boolean reward){
		currentChallenge = null;
		resetChallenge();
		
		if(reward){
			gameData.completeChallenge();
		} else {
			gameData.skipChallenge();
		}
		
		int cNum = gameData.getChallengeNum();
		currentChallenge = challenges.get(cNum % challenges.size()).createChallenge(this);
	}

}

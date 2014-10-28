package com.ithinkrok.yellowquest.challenge;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.internal.bs;
import com.ithinkrok.yellowquest.GameData;
import com.ithinkrok.yellowquest.MainActivity;

public class StatTracker {

	private static interface ChallengeInfo {

		public Challenge createChallenge(StatTracker tracker);
	}

	public static class BasicChallengeInfo implements ChallengeInfo {

		private Stat stat;
		private StatType type;
		private int target;
		private String power;

		public BasicChallengeInfo(Stat stat, StatType type, int target, String power) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
			this.power = power;
		}

		@Override
		public Challenge createChallenge(StatTracker tracker) {
			Challenge c = new BasicChallenge(tracker, stat, type, target);
			c.power = power;
			return c;
		}

	}

	public static class WithoutChallengeInfo implements ChallengeInfo {

		private Stat stat;
		private StatType type;
		private int target;
		private Stat limited;
		private int limit;
		private String power;

		public WithoutChallengeInfo(Stat stat, StatType type, int target, Stat limited, int limit, String power) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
			this.limited = limited;
			this.limit = limit;
			this.power = power;
		}

		@Override
		public Challenge createChallenge(StatTracker tracker) {
			Challenge c = new WithoutChallenge(tracker, stat, type, target, limited, limit);
			c.power = power;
			return c;
		}

	}

	public void completeLevel(MainActivity context) {
		if (currentChallenge == null)
			return;
		if (currentChallenge.power != null && !context.getPlayer().hasPower(currentChallenge.power))
			return;
		currentChallenge.completeLevel(context);
	}

	public void lostLife(MainActivity context) {
		if (currentChallenge == null)
			return;
		if (currentChallenge.power != null && !context.getPlayer().hasPower(currentChallenge.power))
			return;
		currentChallenge.lostLife(context);
	}

	public void gameOver(MainActivity context) {
		if (currentChallenge == null)
			return;
		if (currentChallenge.power != null && !context.getPlayer().hasPower(currentChallenge.power))
			return;
		currentChallenge.gameOver(context);
	}

	public void loadGame(MainActivity context) {
		if (currentChallenge == null)
			return;
		if (currentChallenge.power != null && !context.getPlayer().hasPower(currentChallenge.power))
			return;
		currentChallenge.loadGame(context);
	}

	private static ArrayList<ChallengeInfo> challenges = new ArrayList<StatTracker.ChallengeInfo>();

	private static ArrayList<String> statNames = new ArrayList<String>();

	public Challenge currentChallenge;

	public StatTracker(GameData gameData) {
		super();
		this.gameData = gameData;
	}

	public void loaded() {
		generateChallenge();
	}

	static {
		Stat[] all = Stat.values();
		for (Stat s : all) {
			statNames.add(s.name());
		}

		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 1000, null));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.CHALLENGE, 15, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 5, null));
		challenges.add(new BasicChallengeInfo(Stat.UP, StatType.CHALLENGE, 500, "up"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.CHALLENGE, 1, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 3, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 5, "bounce"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.CHALLENGE, 5, null));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 3000, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.GAME, 2, null));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.GAME, 15, "bounce"));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 3, Stat.JUMP_OVER_BOXES, 0, null));
		challenges.add(new BasicChallengeInfo(Stat.POWER_SAVE, StatType.GAME, 2, "teleport"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.LIFE, 2, null));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 5, Stat.LEFT_DISTANCE, 0, null));
		challenges.add(new BasicChallengeInfo(Stat.UP, StatType.LIFE, 5000, null));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.LEVEL, 8, null));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.CHALLENGE, 1, Stat.JUMPS, 0, null));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.GAME, 3, null));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 5000, null));

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

	public void addStat(MainActivity context, Stat stat, int add) {
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

		String power = context.getPlayer().getPowerName();

		if (power != null)
			gameData.addStatPower(stat.name(), add, power);
		else
			gameData.addStat(stat.name(), add);

		if (currentChallenge != null && currentChallenge.isTracking(stat)) {
			if (currentChallenge.power != null && !currentChallenge.power.equals(power))
				return;
			currentChallenge.update(context, stat, add);
		}

	}

	public int getStat(Stat stat, StatType type) {
		return getStat(stat, type, null);
	}
	
	public int getStat(Stat stat, StatType type, String power) {
		switch (type) {
		case TOTAL:
			return gameData.getStat(stat.name());
		case CHALLENGE:
			if(power == null) gameData.getStatChallenge(stat.name());
			else return gameData.getStatChallengePower(stat.name(), power);
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

	public void generateChallenge() {
		if (currentChallenge != null)
			return;
		int cNum = gameData.getChallengeNum();
		currentChallenge = challenges.get(cNum % challenges.size()).createChallenge(this);
	}

	public void nextChallenge(boolean reward) {
		currentChallenge = null;
		resetChallenge();

		if (reward) {
			gameData.completeChallenge();
		} else {
			gameData.skipChallenge();
		}

		generateChallenge();
	}

	public void skipChallenge() {
		currentChallenge = null;
		resetChallenge();

		gameData.skipChallenge();

	}

	public void completeChallenge() {
		currentChallenge = null;
		resetChallenge();

		gameData.completeChallenge();
	}

}

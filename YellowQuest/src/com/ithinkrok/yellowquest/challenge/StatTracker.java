package com.ithinkrok.yellowquest.challenge;

import java.util.*;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.util.LongSparseArray;

import com.ithinkrok.yellowquest.GameData;
import com.ithinkrok.yellowquest.MainActivity;
import com.ithinkrok.yellowquest.ui.ToastSystem;

public class StatTracker {

	private static interface ChallengeInfo {

		public Challenge createChallenge(StatTracker tracker, int cycle);
	}

	public static class BasicChallengeInfo implements ChallengeInfo {

		private Stat stat;
		private StatType type;
		private int target;
		private String power;
		private boolean shadow, time;
		private int skipCost;

		public BasicChallengeInfo(Stat stat, StatType type, int target, String power) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
			this.power = power;
			skipCost = skippingCost;
		}
		
		public BasicChallengeInfo(Stat stat, StatType type, int target, String power, boolean shadow, boolean time) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
			this.power = power;
			this.shadow = shadow;
			this.time = time;
			skipCost = skippingCost;
		}

		@Override
		public Challenge createChallenge(StatTracker tracker, int cycle) {
			BasicChallenge c = new BasicChallenge(tracker, stat, type, target);
			c.power = power;
			c.shadow = shadow;
			c.time = time;
			c.skipCost = skipCost;
			if(cycle > 0){
				if(c.target == 1) c.target += (int)(cycle * 0.5);
				else c.target += cycle;
			}
			return c;
		}

	}
	
	private static int skippingCost = 3000;

	public static class WithoutChallengeInfo implements ChallengeInfo {

		private Stat stat;
		private StatType type;
		private int target;
		private Stat limited;
		private int limit;
		private String power;
		private boolean shadow, time;
		private int skipCost;

		public WithoutChallengeInfo(Stat stat, StatType type, int target, Stat limited, int limit, String power) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
			this.limited = limited;
			this.limit = limit;
			this.power = power;
			skipCost = skippingCost;
		}
		
		public WithoutChallengeInfo(Stat stat, StatType type, int target, Stat limited, int limit, String power, boolean shadow, boolean time) {
			super();
			this.stat = stat;
			this.type = type;
			this.target = target;
			this.limited = limited;
			this.limit = limit;
			this.power = power;
			this.shadow = shadow;
			this.time = time;
			skipCost = skippingCost;
		}

		@Override
		public Challenge createChallenge(StatTracker tracker, int cycle) {
			WithoutChallenge c = new WithoutChallenge(tracker, stat, type, target, limited, limit);
			c.power = power;
			c.shadow = shadow;
			c.time = time;
			c.skipCost = skipCost;
			if(cycle > 0){
				c.skipCost *= 1.2 * cycle;
				if(c.target == 1) c.target += (int)(cycle * 0.5);
				else c.target += cycle;
			}
			return c;
		}

	}
	
	public MainActivity context;
	

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

	public StatTracker(GameData gameData, MainActivity context) {
		super();
		this.gameData = gameData;
		this.context = context;
	}

	public void loaded() {
		generateChallenge();
	}

	static {
		Stat[] all = Stat.values();
		for (Stat s : all) {
			statNames.add(s.name());
		}

		//00 - 
		skippingCost = 3000;
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 1000, null));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.CHALLENGE, 15, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 5, null));
		challenges.add(new BasicChallengeInfo(Stat.UP, StatType.CHALLENGE, 500, "up"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.CHALLENGE, 1, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 3, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 3, "bounce"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.CHALLENGE, 3, null));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 3000, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.GAME, 2, null));
		
		//10
		skippingCost = 6000;
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.GAME, 15, "bounce"));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 3, Stat.JUMP_OVER_BOXES, 0, null));
		challenges.add(new BasicChallengeInfo(Stat.POWER_SAVE, StatType.GAME, 2, "teleport"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.LIFE, 2, null));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.LIFE, 3000, null));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 5, Stat.LEFT_DISTANCE, 0, null));
		challenges.add(new BasicChallengeInfo(Stat.UP, StatType.LIFE, 5000, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 1, null, true, false));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.LEVEL, 8, null));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.CHALLENGE, 1, Stat.JUMPS, 0, null));
		
		//20
		skippingCost = 9000;
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.GAME, 3, null));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 5000, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.LIFE, 3, null, false, true));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.LIFE, 15, null));
		challenges.add(new BasicChallengeInfo(Stat.POWER_SAVE, StatType.CHALLENGE, 10, "stick"));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 1, null, false, true));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.LEVEL, 1000, null));
		challenges.add(new BasicChallengeInfo(Stat.UP, StatType.LIFE, 1000, "up", false, true));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 7, null));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.CHALLENGE, 10, null));
		
		//30
		skippingCost = 13000;
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.LIFE, 2, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.LIFE, 5, "troll"));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.GAME, 25, "bounce"));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.LIFE, 2, null, false, true));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 1, "life"));
		challenges.add(new BasicChallengeInfo(Stat.POWER_SAVE, StatType.GAME, 2, "teleport", false, true));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 7, Stat.LEFT_DISTANCE, 0, null));
		challenges.add(new WithoutChallengeInfo(Stat.SCORE, StatType.LIFE, 3000, Stat.JUMP_OVER_BOXES, 0, null));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 5, "stick"));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 1, "troll"));
		
		//40
		skippingCost = 20000;
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.LIFE, 2, null, true, false));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.GAME, 5, null));
		challenges.add(new WithoutChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 4, Stat.JUMP_OVER_BOXES, 0, null, false, true));
		challenges.add(new BasicChallengeInfo(Stat.JUMP_OVER_BOXES, StatType.LEVEL, 10, null));
		challenges.add(new BasicChallengeInfo(Stat.SCORE, StatType.GAME, 9000, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.GAME, 1, "life", true, false));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.GAME, 9, null));
		challenges.add(new BasicChallengeInfo(Stat.BONUS, StatType.CHALLENGE, 1, "doublejump"));
		challenges.add(new BasicChallengeInfo(Stat.COMPLETE_LEVEL, StatType.LIFE, 6, null));
		challenges.add(new BasicChallengeInfo(Stat.RIGHT_SIDE_FLAG, StatType.LIFE, 3, null));
		

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
		data.clear();
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
		boolean shadow = context.getGame().shadowMode();
		boolean time = context.getGame().timeMode();

		if (power != null)
			addStatPowerMode(stat.name(), add, power, shadow, time);
		else
			addStatMode(stat.name(), add, shadow, time);

		if (currentChallenge != null && currentChallenge.isTracking(stat)) {
			if (currentChallenge.power != null && !currentChallenge.power.equals(power))
				return;
			currentChallenge.update(context, stat, add);
		}

	}

	public int getStat(Stat stat, StatType type) {
		return getStat(stat, type, null, false, false);
	}
	
	public int getStat(Stat stat, StatType type, String power){
		return getStat(stat, type, power, false, false);
	}
	
	public int getStat(Stat stat, StatType type, boolean shadow, boolean time){
		return getStat(stat, type, null, shadow, time);
	}
	
	public int getStat(Stat stat, StatType type, String power, boolean shadow, boolean time) {
		switch (type) {
		case TOTAL:
			return gameData.getStat(stat.name());
		case CHALLENGE:
			if(power == null) return getStatChallenge(stat.name(), shadow, time);
			else return getStatChallengePower(stat.name(), power, shadow, time);
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
		currentChallenge = challenges.get(cNum % challenges.size()).createChallenge(this, cNum / challenges.size());
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
		int rank = gameData.getPlayerRank();
		int ctr = gameData.getChallengesToRank();
		if(ctr <= 0) ctr = (rank / 10) + 1;
		ctr -= 1;
		if(ctr == 0){
			gameData.setPlayerRank(++rank);
			ToastSystem.showRankToast(rank);
			ctr = rank / 10;
		}
		gameData.setChallengesToRank(ctr);
		
		currentChallenge = null;
		resetChallenge();

		gameData.completeChallenge();
		
	}
	
	private static final int MOD = 1436763197;
	
	public static long hash(String string) {
		long h = 1125899906842597L; // prime
		int len = string.length();

		for (int i = 0; i < len; i++) {
			h = 31 * h + string.charAt(i);
		}
		return h;
	}
	
	private LongSparseArray<Object> data = new LongSparseArray<Object>();
	
	private void setInt(long key, int val) {
		data.put(key, val);
		data.put(~key, val ^ MOD);
	}
	
	private int getInt(long key, int cheat) {
		Object o1 = data.get(key);
		if (o1 == null || !(o1 instanceof Integer))
			return cheat;
		Object o2 = data.get(~key);
		if (o2 == null || !(o2 instanceof Integer))
			return cheat;
		int i1 = (Integer) o1;
		int i2 = (Integer) o2;
		if ((i1 ^ MOD) == i2)
			return i1;
		else
			return cheat;
	}
	
	public void load(SharedPreferences prefs) {
		Map<String, ?> data = prefs.getAll();
		for (Entry<String, ?> e : data.entrySet()) {
			if (e.getValue() == null)
				continue;
			if (!e.getKey().startsWith("data_"))
				continue;
			long key = Long.parseLong(e.getKey().substring(5));
			if (e.getValue() instanceof Integer) {
				int value = ((Number) e.getValue()).intValue();
				this.data.put(key, value);
			} else if (e.getValue() instanceof Long) {
				long value = ((Number) e.getValue()).longValue();
				this.data.put(key, value);
			} else if (e.getValue() instanceof String) {
				this.data.put(key, (String) e.getValue());
			}
		}
		
		loaded();
		
	}
	
	public void save(Editor editor) {
		editor.clear();
		editor.putInt("sv", 1);
		Object obj;
		Integer i;
		Long l;
		for (int d = 0; d < data.size(); ++d) {
			obj = data.valueAt(d);
			if (obj instanceof Integer) {
				i = (Integer) obj;
				editor.putInt("data_" + data.keyAt(d), i);
			} else if (obj instanceof Long) {
				l = (Long) obj;
				editor.putLong("data_" + data.keyAt(d), l);
			} else if (obj instanceof String) {
				editor.putString("data_" + data.keyAt(d), (String) obj);
			}
		}
		editor.apply();
	}
	
	private int getStatChallenge(String stat, boolean shadow, boolean time){
		if(shadow){
			if(time){
				return getInt(hash("shatim_cstat_" + stat), 0);
			}
			return getInt(hash("sha_cstat_" + stat), 0);
		}
		if(time){
			return getInt(hash("tim_cstat_" + stat), 0);
		}
		return getInt(hash("cstat_" + stat), 0);
	}
	
	private int getStatChallengePower(String stat, String power, boolean shadow, boolean time){
		if(shadow){
			if(time){
				return getInt(hash("shatim_" + power + "_cstat_" + stat), 0);
			}
			return getInt(hash("sha_" + power + "_cstat_" + stat), 0);
		}
		if(time){
			return getInt(hash("tim_" + power + "_cstat_" + stat), 0);
		}
		return getInt(hash(power + "_cstat_" + stat), 0);
	}
	
//	private void addStat(String stat, int add){
//		gameData.addStat(stat, add);
//		long id = hash("cstat_" + stat);
//		setInt(id, getInt(id, 0) + add);
//	}
//	
//	private void addStatPower(String stat, int add, String power){
//		gameData.addStat(stat, add);
//		long id = hash("cstat_" + stat);
//		setInt(id, getInt(id, 0) + add);
//		id = hash(power + "_cstat_" + stat);
//		setInt(id, getInt(id, 0) + add);
//	}
	
	private void addStatMode(String stat, int add, boolean shadow, boolean time){
		gameData.addStat(stat, add);
		long id = hash("cstat_" + stat);
		setInt(id, getInt(id, 0) + add);
		if(shadow){
			if(time){
				id = hash("shatim_cstat_" + stat);
				setInt(id, getInt(id, 0) + add);
			}
			id = hash("sha_cstat_" + stat);
			setInt(id, getInt(id, 0) + add);
		}
		if(time){
			id = hash("tim_cstat_" + stat);
			setInt(id, getInt(id, 0) + add);
		}
	}
	
	private void addStatPowerMode(String stat, int add, String power, boolean shadow, boolean time){
		gameData.addStat(stat, add);
		long id = hash("cstat_" + stat);
		setInt(id, getInt(id, 0) + add);
		id = hash(power + "_cstat_" + stat);
		setInt(id, getInt(id, 0) + add);
		if(shadow){
			if(time){
				id = hash("shatim_cstat_" + stat);
				setInt(id, getInt(id, 0) + add);
				id = hash("shatim_" + power + "_cstat_" + stat);
				setInt(id, getInt(id, 0) + add);
			}
			id = hash("sha_cstat_" + stat);
			setInt(id, getInt(id, 0) + add);
			id = hash("sha_" + power + "_cstat_" + stat);
			setInt(id, getInt(id, 0) + add);
		}
		if(time){
			id = hash("tim_cstat_" + stat);
			setInt(id, getInt(id, 0) + add);
			id = hash("tim_" + power + "_cstat_" + stat);
			setInt(id, getInt(id, 0) + add);
		}
	}

}

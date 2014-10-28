package com.ithinkrok.yellowquest;

import java.util.*;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.util.LongSparseArray;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.achievement.Achievements.UpdateAchievementResult;
import com.ithinkrok.yellowquest.challenge.StatTracker;
import com.ithinkrok.yellowquest.ui.*;

public class GameData {
	
	private static final int MOD = 1436862197;
	
	private static final int UNIQUE = android.os.Build.MODEL.hashCode();

	public static long hash(String string) {
		long h = 1125899906842597L; // prime
		int len = string.length();

		for (int i = 0; i < len; i++) {
			h = 31 * h + string.charAt(i);
		}
		return h;
	}

	private MainActivity context;

	private GoogleApiClient client;
	
	
	// Use gamedata to save achievements
	// 0 = not got
	// 1 = got, but only offline
	// 2 = got online

	private LongSparseArray<Object> data = new LongSparseArray<Object>();
	
	public StatTracker statTracker;

	public GameData(MainActivity context) {
		super();
		this.context = context;
		this.statTracker = new StatTracker(this, context);
	}
	
	public void addStat(String stat, int add){
		long id = hash("stat_" + stat);
		setInt(id, getInt(id, 0) + add);
	}
	
	
	public int getStat(String stat){
		return getInt(hash("stat_" + stat), 0);
	}
	
	public int getHighestLevel(){
		return getInt(hash("highest_level"), 0);
	}
	
	public void setHighestLevel(int level){
		if(level <= getHighestLevel()) return;
		
		setInt(hash("highest_level"), level);
	}
	
	
	public int getChallengeNum(){
		return getInt(hash("challenge_num"), 0);
	}
	
	public void skipChallenge(){
		setInt(hash("challenge_num"), getChallengeNum() + 1);
	}
	
	public int getCompletedChallenges(){
		return getInt(hash("challenges_done"), 0);
	}
	
	public void completeChallenge(){
		skipChallenge();
		setInt(hash("challenges_done"), getCompletedChallenges() + 1);
	}
	


	public boolean addAchievement(String achievement) {
		if (hasAchievement(achievement))
			return false;
		
		AchievementInfo info = AchievementInfo.getAchievement(achievement);
		client = context.getApiClient();
		if (client == null || !client.isConnected()) {
			setInt(getAchievementHash(achievement), 1);
			
		} else {
			//Suppress Google achievement notification
			Games.Achievements.unlockImmediate(client, achievement).setResultCallback(new  ResultCallback<Achievements.UpdateAchievementResult>() {

	            @Override
	            public void onResult(UpdateAchievementResult result) {
	                return;
	            }

	        });
			
			setInt(getAchievementHash(achievement), 2);
		}

		ToastSystem.showAchievementToast(info);
		
		if (info.reward < 1)
			return true;

		addScorePoints(info.reward);

		return true;
	}
	
	public int getPowerUnlocks(){
		return getInt(hash("unlockedpowers"), 0);
	}
	
	public int getBonusUnlocks(){
		return getInt(hash("unlockedbonus"), 0);
	}
	
	
	public void setPowerUnlocks(int n){
		setInt(hash("unlockedpowers"), n);
	}
	
	public void addPowerUnlock(int n){
		setInt(hash("unlockedpowers"), getPowerUnlocks() | (1 << n));
	}
	
	public void addBonusUnlock(int n){
		setInt(hash("unlockedbonus"), getBonusUnlocks() | (1 << n));
	}
	
	public int getPlayerRank(){
		return getInt(hash("player_rank"), 0);
	}
	
	public void setPlayerRank(int val){
		if(val <= getPlayerRank()) return;
		setInt(hash("player_rank"), val);
		
		client = context.getApiClient();
		if (client != null && client.isConnected()) {
			Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_ranks),
					val);
			setInt(hash("rank_uploaded"), val);
		}
	}
	
	public int getChallengesToRank(){
		return getInt(hash("challenges_to_rank"), -1);
	}
	
	public void setChallengesToRank(int ctr){
		setInt(hash("challenges_to_rank"), ctr);
	}
	
	public boolean hasPowerUnlock(int power){
		int lks = getPowerUnlocks();
		return ((lks >> power) & 1) == 1;
	}
	
	public void setMadePurchase(){
		setInt(hash("made_iap"), UNIQUE);
	}
	
	public boolean hasMadePurchase(){
		return getInt(hash("made_iap"), 0) == UNIQUE;
	}

	public boolean addHiScore(int score) {
		if (score < 1)
			return false;
		addScorePoints(score);
		setInt(hash("score_previous"), score);
		String base = "score_total";
		long hash = hash(base);
		int res = getInt(hash, 0);
		if (res < score) {
			setInt(hash, score);
			String text = context.getString(R.string.hiscore_beat);
			text = StringFormatter.format(text, score);
			
			ToastSystem.showHiscoreToast(score, res);
			
			client = context.getApiClient();
			if (client != null && client.isConnected()) {
				Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores),
						score);
				setInt(hash("score_uploaded"), score);
			}
			return true;
		} else{
			client = context.getApiClient();
			if (client != null && client.isConnected()) {
				//still in for daily or weekly hiscores
				Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores),
						score);
				//dont set the uploaded score, as this could be less
			}
			return false;
		}
	}
	
	public boolean addHiScoreShadowTime(int score) {
		if (score < 1)
			return false;
		setInt(hash("shatim_score_previous"), score);
		String base = "shatim_score_total";
		long hash = hash(base);
		int res = getInt(hash, 0);
		if (res < score) {
			setInt(hash, score);
			String text = context.getString(R.string.hiscore_beat);
			text = StringFormatter.format(text, score);
			
			ToastSystem.showHiscoreToast(score, res);
			
			client = context.getApiClient();
			if (client != null && client.isConnected()) {
				Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores),
						score);
				setInt(hash("shatim_score_uploaded"), score);
			}
			return true;
		} else{
			client = context.getApiClient();
			if (client != null && client.isConnected()) {
				//still in for daily or weekly hiscores
				Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores),
						score);
				//dont set the uploaded score, as this could be less
			}
			return false;
		}
	}
	
	public void updateOnlineHiScore(){
		if(client == null || !client.isConnected()) return;
		int offline = getInt(hash("score_total"), 0);
		int online = getInt(hash("score_uploaded"), 0);
		if(offline > online) {
			Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores),
					offline);
			setInt(hash("score_uploaded"), offline);
		}
		offline = getInt(hash("shatim_score_total"), 0);
		online = getInt(hash("shatim_score_uploaded"), 0);
		if(offline > online) {
			Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_shadowtime_hiscores),
					offline);
			setInt(hash("shatim_score_uploaded"), offline);
		}
		offline = getInt(hash("player_rank"), 0);
		online = getInt(hash("rank_uploaded"), 0);
		if(offline > online) {
			Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_ranks),
					offline);
			setInt(hash("rank_uploaded"), offline);
		}
	}
	

	public void addOfflineAchievements() {
		GoogleApiClient client = context.getApiClient();
		if (client == null || !client.isConnected())
			return;

		ArrayList<String> check = new ArrayList<String>();
		check.add(context.getString(R.string.achievement_almost));
		check.add(context.getString(R.string.achievement_big_failure));
		check.add(context.getString(R.string.achievement_easy));
		check.add(context.getString(R.string.achievement_expert));
		check.add(context.getString(R.string.achievement_hard));
		check.add(context.getString(R.string.achievement_impossible));
		check.add(context.getString(R.string.achievement_medium));
		check.add(context.getString(R.string.achievement_overshot));

		for (String s : check) {
			if (getAchievementLevel(s) != 1)
				continue;
			Games.Achievements.unlock(client, s);
			setInt(getAchievementHash(s), 2);
		}
	}

	public boolean addScore(int level, int score) {
		String base = "score_" + level + "_all";
		long hash = hash(base);
		int res = getInt(hash, 0);
		if (res < score) {
			setInt(hash, score);
			return true;
		} else
			return false;
	}

	public void addScorePoints(int add) {
		String base = "score_points";
		long hash = hash(base);
		int res = getInt(hash, 0);
		add += res;
		setInt(hash, add);

	}
	
	public void setScorePoints(int set){
		setInt(hash("score_points"), set);
	}

	private long getAchievementHash(String achievement) {
		return hash("achievement_" + achievement);
	}

	public int getAchievementLevel(String achievement) {
		int res = getInt(getAchievementHash(achievement), 0);
		return res;
	}

	public int getHiScore() {
		String base = "score_total";
		long hash = hash(base);
		int res = getInt(hash, 0);
		return res;
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

	public String getNextPower() {
		return getString(hash("power_next"), null);
	}

	public int getPowerUpgradeLevel(String power) {
		int amt = getInt(hash("power_" + power + "upgrade"), 0);
		return amt;
	}

	public int getPreviousScore() {
		String base = "score_previous";
		long hash = hash(base);
		int res = getInt(hash, 0);
		return res;
	}

	public int getScore(int level) {
		String base = "score_" + level + "_all";
		int res = getInt(hash(base), 0);
		return res;
	}

	public int getScorePoints() {
		String base = "score_points";
		long hash = hash(base);
		int res = getInt(hash, 0);
		return res;
	}

	private String getString(long key, String cheat) {
		Object o1 = data.get(key);
		if (o1 == null || !(o1 instanceof String))
			return cheat;
		Object o2 = data.get(~key);
		if (o2 == null || !(o2 instanceof Long))
			return cheat;
		String s = (String) o1;
		long l = (Long) o2;
		if (hash(s) == l)
			return s;
		else
			return cheat;
	}

	public int getTime(int level) {
		String base = "time_" + level + "_all";
		int res = getInt(hash(base), 0);
		;
		return res;
	}

	public boolean hasAchievement(String achievement) {
		int res = getInt(getAchievementHash(achievement), 0);
		return res > 0;
	}

	public boolean hasScorePoints(int amount) {
		return getScorePoints() >= amount;
	}

	public void load(SharedPreferences prefs, SharedPreferences challenges) {
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
		
		statTracker.load(challenges);
	}

	
	public void save(Editor editor, Editor challenges) {
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
		statTracker.save(challenges);
	}
	
	public void saveOnly(Editor editor){
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

	private void setInt(long key, int val) {
		data.put(key, val);
		data.put(~key, val ^ MOD);
	}

	public void setNextPower(String power) {
		setString(hash("power_next"), power);
	}

	public void setPowerUpgradeLevel(String power, int lvl) {
		setInt(hash("power_" + power + "upgrade"), lvl);
	}

	private void setString(long key, String val) {
		data.put(key, val);
		data.put(~key, hash(val));
	}

	public boolean subtractScorePoints(int add) {
		String base = "score_points";
		long hash = hash(base);
		int res = getInt(hash, 0);
		add -= res;
		if (add > 0)
			return false;
		setInt(hash, -add);
		return true;

	}

}

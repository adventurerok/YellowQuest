package com.ithinkrok.yellowquest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.util.LongSparseArray;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameData {
	

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

	public GameData(MainActivity context) {
		super();
		this.context = context;
	}

	public boolean addAchievement(String achievement) {
		return addAchievement(achievement, 500);
	}
	
	public void addOfflineAchievements(){
		GoogleApiClient client = context.getApiClient();
		if(client == null || !client.isConnected()) return;
		
		ArrayList<String> check = new ArrayList<String>();
		check.add(context.getString(R.string.achievement_almost));
		check.add(context.getString(R.string.achievement_big_failure));
		check.add(context.getString(R.string.achievement_easy));
		check.add(context.getString(R.string.achievement_expert));
		check.add(context.getString(R.string.achievement_hard));
		check.add(context.getString(R.string.achievement_impossible));
		check.add(context.getString(R.string.achievement_medium));
		check.add(context.getString(R.string.achievement_overshot));
		
		
		
		for(String s : check){
			if(getAchievementLevel(s) != 1) continue;
			Games.Achievements.unlock(client, s);
			data.put(getAchievementHash(s), 2);
		}
	}
	
	public boolean addAchievement(String achievement, int reward) {
		if (hasAchievement(achievement))
			return false;
		client = context.getApiClient();
		if (client == null || !client.isConnected()) {
			data.put(getAchievementHash(achievement), 1);
		} else {
			Games.Achievements.unlock(client, achievement);
			data.put(getAchievementHash(achievement), 2);
		}
		
		if(reward < 1) return true;
		
		addScorePoints(reward);
		String text = context.getString(R.string.achievement_reward);
		text = String.format(text, reward);
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		
		
		return true;
	}

	public boolean hasAchievement(String achievement) {
		Object obj = data.get(getAchievementHash(achievement));
		if (obj == null || !(obj instanceof Number))
			return false;
		return ((Number) obj).intValue() > 0;
	}
	
	public int getAchievementLevel(String achievement) {
		Object obj = data.get(getAchievementHash(achievement));
		if (obj == null || !(obj instanceof Number))
			return 0;
		return ((Number) obj).intValue();
	}

	private long getAchievementHash(String achievement) {
		return hash("achievement_" + achievement);
	}
	
	public void setNextPower(String power){
		data.put(hash("power_next"), power);
	}
	
	public String getNextPower(){
		return (String) data.get(hash("power_next"));
	}
	
	public int getPowerUpgradeLevel(String power){
		Integer amt = (Integer) data.get(hash("power_" + power + "upgrade"));
		if(amt == null) return 0;
		return amt;
	}
	
	public void setPowerUpgradeLevel(String power, int lvl){
		data.put(hash("power_" + power + "upgrade"), lvl);
	}

	public boolean addHiScore(int score) {
		if (score < 1)
			return false;
		addScorePoints(score);
		data.put(hash("score_previous"), score);
		String base = "score_total";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res == null || res < score) {
			data.put(hash, score);
			String text = context.getString(R.string.hiscore_beat);
			text = String.format(text, score);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			client = context.getApiClient();
			if (client != null && client.isConnected()) {
				Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores), score);
			}
			return true;
		} else
			return false;
	}

	public int getHiScore() {
		String base = "score_total";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res == null)
			return 0;
		else
			return res;
	}

	public int getPreviousScore() {
		String base = "score_previous";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res == null)
			return 0;
		else
			return res;
	}

	public int getScorePoints() {
		String base = "score_points";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res == null)
			return 0;
		else
			return res;
	}
	
	public boolean hasScorePoints(int amount) {
		return getScorePoints() >= amount;
	}

	public void addScorePoints(int add) {
		String base = "score_points";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res != null)
			add += res;
		data.put(hash, add);

	}
	
	public boolean subtractScorePoints(int add) {
		String base = "score_points";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res != null)
			add -= res;
		
		if(add > 0) return false;
		data.put(hash, -add);
		return true;

	}

	public boolean addScore(int level, int score) {
		String base = "score_" + level + "_all";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if (res == null || res < score) {
			data.put(hash, score);
			return true;
		} else
			return false;
	}

	public int getScore(int level) {
		String base = "score_" + level + "_all";
		Integer res = (Integer) data.get(hash(base));
		if (res == null)
			return 0;
		return res;
	}

	public int getTime(int level) {
		String base = "time_" + level + "_all";
		Integer res = (Integer) data.get(hash(base));
		if (res == null)
			return 0;
		return res;
	}

	public void load(SharedPreferences prefs) {
		Map<String, ?> data = prefs.getAll();
		for (Entry<String, ?> e : data.entrySet()) {
			if (e.getValue() == null)
				continue;
			if (!e.getKey().startsWith("data_"))
				continue;
			long key = Long.parseLong(e.getKey().substring(5));
			if(e.getValue() instanceof Number){
				int value = ((Number) e.getValue()).intValue();
				this.data.put(key, value);
			} else if(e.getValue() instanceof String){
				this.data.put(key, (String)e.getValue());
			}
		}
	}

	public void save(Editor editor) {
		Object obj;
		Integer i;
		for (int d = 0; d < data.size(); ++d) {
			obj =  data.valueAt(d);
			if(obj instanceof Integer){
				i = (Integer) obj;
				editor.putInt("data_" + data.keyAt(d), i);
			} else if(obj instanceof String){
				editor.putString("data_" + data.keyAt(d), (String)obj);
			}
		}
		editor.commit();
	}

}

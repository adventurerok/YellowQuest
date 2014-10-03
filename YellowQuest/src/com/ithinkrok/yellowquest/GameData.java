package com.ithinkrok.yellowquest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.util.LongSparseArray;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameData {
	
	private static final int MOD = 1436862197;

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

	public boolean addAchievement(String achievement, int reward) {
		if (hasAchievement(achievement))
			return false;
		client = context.getApiClient();
		if (client == null || !client.isConnected()) {
			setInt(getAchievementHash(achievement), 1);
		} else {
			Games.Achievements.unlock(client, achievement);
			setInt(getAchievementHash(achievement), 2);
		}

		if (reward < 1)
			return true;

		addScorePoints(reward);
		String text = context.getString(R.string.achievement_reward);
		text = String.format(text, reward);
		Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, (int) (context.view.width * 0.1), 0);
		t.show();

		return true;
	}
	
	public int getPowerUnlocks(){
		return getInt(hash("unlockedpowers"), 0);
	}
	
	public void setPowerUnlocks(int n){
		setInt(hash("unlockedpowers"), n);
	}
	
	public void addPowerUnlock(int n){
		setInt(hash("unlockedpowers"), getPowerUnlocks() | (1 << n));
	}
	
	public boolean hasPowerUnlock(int power){
		int lks = getPowerUnlocks();
		return ((lks >> power) & 1) == 1;
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
			text = String.format(text, score);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			client = context.getApiClient();
			if (client != null && client.isConnected()) {
				Games.Leaderboards.submitScore(client, context.getString(R.string.leaderboard_yellowquest_hiscores),
						score);
			}
			return true;
		} else
			return false;
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
	}

	public void save(Editor editor) {
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
		editor.commit();
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

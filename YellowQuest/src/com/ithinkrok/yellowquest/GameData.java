package com.ithinkrok.yellowquest;

import java.util.Map;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.util.LongSparseArray;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameData {
	
	public static long hash(String string) {
		  long h = 1125899906842597L; // prime
		  int len = string.length();

		  for (int i = 0; i < len; i++) {
		    h = 31*h + string.charAt(i);
		  }
		  return h;
		}
	private MainActivity activity;
	
	private GoogleApiClient client;
	
	
	//Use gamedata to save achievements
	//0 = not got
	//1 = got, but only offline
	//2 = got online
	

	private LongSparseArray<Object> data = new LongSparseArray<Object>();
	
	public GameData(MainActivity activity) {
		super();
		this.activity = activity;
	}
	
	public void addAchievement(String achievement){
		if(hasAchievement(achievement)) return;
		client = activity.getApiClient();
		if(client == null || !client.isConnected()){
			data.put(getAchievementHash(achievement), 1);
			return;
		}
		Games.Achievements.unlock(client, achievement);
		data.put(getAchievementHash(achievement), 2);
	}
	
	public boolean hasAchievement(String achievement){
		Object obj = data.get(getAchievementHash(achievement));
		if(obj == null || !(obj instanceof Number)) return false;
		return ((Number)obj).intValue() > 0;
	}
	
	
	private long getAchievementHash(String achievement){
		return hash("achievement_" + achievement);
	}
	
	public boolean addScore(int level, int score){
		String base = "score_" + level + "_all";
		long hash = hash(base);
		Integer res = (Integer) data.get(hash);
		if(res == null || res < score){
			data.put(hash, score);
			return true;
		} else return false;
	}
	
	
	public int getScore(int level){
		String base = "score_" + level + "_all";
		Integer res = (Integer) data.get(hash(base));
		if(res == null) return 0;
		return res;
	}
	
	public int getTime(int level){
		String base = "time_" + level + "_all";
		Integer res = (Integer) data.get(hash(base));
		if(res == null) return 0;
		return res;
	}
	
	
	public void load(SharedPreferences prefs){
		Map<String, ?> data = prefs.getAll();
		for(Entry<String, ?> e : data.entrySet()){
			if(e.getValue() == null || !(e.getValue() instanceof Number)) continue;
			if(!e.getKey().startsWith("data_")) continue;
			long key = Long.parseLong(e.getKey().substring(5));
			int value = ((Number)e.getValue()).intValue();
			this.data.put(key, value);
		}
	}
	
	public void save(Editor editor){
		Integer i;
		for(int d = 0; d < data.size(); ++d){
			i = (Integer) data.valueAt(d);
			if(i == null) continue;
			editor.putInt("data_" + data.keyAt(d), i);
		}
		editor.commit();
	}

}

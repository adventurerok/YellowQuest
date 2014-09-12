package com.ithinkrok.yellowquest;

import java.util.Map;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.util.LongSparseArray;

public class GameData {
	
	private LongSparseArray<Object> data = new LongSparseArray<Object>();
	
	public int getScore(int level, boolean shadow, boolean time){
		String base = "score_" + level + "_shad_" + shadow + "_time_" + time + "_end";
		Integer res = (Integer) data.get(hash(base));
		if(res == null) return 0;
		return res;
	}
	
	public int getScore(int level){
		String base = "score_" + level + "_all";
		Integer res = (Integer) data.get(hash(base));
		if(res == null) return 0;
		return res;
	}
	
	public int getTime(int level, boolean shadow, boolean time){
		String base = "time_" + level + "_shad_" + shadow + "_time_" + time + "_end";
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
	
	public void save(Editor editor){
		Integer i;
		for(int d = 0; d < data.size(); ++d){
			i = (Integer) data.valueAt(d);
			if(i == null) continue;
			editor.putInt("data_" + data.keyAt(d), i);
		}
		editor.commit();
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
	
	public static long hash(String string) {
		  long h = 1125899906842597L; // prime
		  int len = string.length();

		  for (int i = 0; i < len; i++) {
		    h = 31*h + string.charAt(i);
		  }
		  return h;
		}

}

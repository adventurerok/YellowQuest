package com.ithinkrok.yellowquest;

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
	
	public static long hash(String string) {
		  long h = 1125899906842597L; // prime
		  int len = string.length();

		  for (int i = 0; i < len; i++) {
		    h = 31*h + string.charAt(i);
		  }
		  return h;
		}

}

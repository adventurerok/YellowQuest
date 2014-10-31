package com.ithinkrok.yellowquest.ui;

import java.util.*;

import android.content.SharedPreferences;

import com.ithinkrok.yellowquest.*;

public abstract class Tip {
	
	private static HashMap<String, Integer> tipMessages = new HashMap<String, Integer>();
	private static ArrayList<Tip> tips = new ArrayList<Tip>();
	private static ArrayList<String> check = new ArrayList<String>();
	private static Random rand = new Random();
	
	private static String lastShown = null;
	
	static {
		new TipRate();
		new TipPower();
		new TipMode();
		new TipAlways("#hiscores", R.string.tip_hiscores);
		new TipAlways("#weekly", R.string.tip_weekly);
	}
	
	public static int getTipMessage(String name){
		return tipMessages.get(name);
	}
	
	public static String getTip(MainActivity context){
		if(!context.enableTips) return null;
		if(rand.nextInt(4) == 0){
			lastShown = null;
			return null;
		}
		check.clear();
		for(Tip t : tips){
			if(!t.name.equals(lastShown) && t.canShow(context)) check.add(t.name);
		}
		if(check.size() == 0){
			lastShown = null;
			return null;
		}
		String r = check.get(rand.nextInt(check.size()));
		lastShown = r;
		return r;
	}

	public String name;
	
	public int message;
	
	public abstract boolean canShow(MainActivity context);
	

	public Tip(String name, int message) {
		super();
		this.name = name;
		this.message = message;
		tipMessages.put(name, message);
		tips.add(this);
	}
	
	private static class TipAlways extends Tip {

		public TipAlways(String name, int message) {
			super(name, message);
			
		}

		@Override
		public boolean canShow(MainActivity context) {
			return true;
		}
		
	}
	
	private static class TipRate extends Tip {

		public TipRate() {
			super("#rate", R.string.tip_rate);
			
		}

		@Override
		public boolean canShow(MainActivity mContext) {
			SharedPreferences prefs = mContext.rateSettings;
			if (prefs.getBoolean("dontshowagain", false)) {
				return false;
			}
			
			long launch_count = prefs.getLong("launch_count", 0);
			Long date_firstLaunch = prefs.getLong("date_first_launch", 0);
			
			if (launch_count >= AppRater.LAUNCH_UNTIL_PROMPT) {
				if (System.currentTimeMillis() >= date_firstLaunch + (AppRater.DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
					return true;
				}
			}
			
			return false;
		}

	
		
	}
	
	private static class TipPower extends Tip {

		public TipPower() {
			super("#power", R.string.tip_powers);
		}

		@Override
		public boolean canShow(MainActivity context) {
			int amt = 0;
			for(int d = 0; d < 6; ++d){
				if(!context.getGameData().hasPowerUnlock(d)) continue;
				++amt;
			}
			return amt != 6;
		}
		
	}
	
	private static class TipMode extends Tip {

		public TipMode() {
			super("#mode", R.string.tip_modes);
		}

		@Override
		public boolean canShow(MainActivity context) {
			return !context.usedDifferentModes;
		}
		
		
	}
	
	
	
}

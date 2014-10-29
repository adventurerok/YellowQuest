package com.ithinkrok.yellowquest.challenge;

import android.content.Context;

import com.ithinkrok.yellowquest.R;
import com.ithinkrok.yellowquest.ui.ToastSystem;


public abstract class Challenge {
	
	public StatTracker tracker;

	public Stat stat;
	
	public StatType type;
	
	public String power = null; // can only be used if type = game/level/life
	
	public boolean shadow = false;
	public boolean time = false;
	
	public int skipCost = 0;
	
	
	public Challenge(StatTracker tracker, Stat stat, StatType type) {
		super();
		this.tracker = tracker;
		this.stat = stat;
		this.type = type;
	}


	public int getIconResource(){
		return stat.icon;
	}
	
	public abstract String getTitleText(Context context);
	public abstract String getProgressText(Context context);
	
	//Called when the stat it is tracking is updated
	public void update(Context context, Stat stat, int increase){
		
	}
	
	public boolean isTracking(Stat stat){
		return stat == this.stat;
	}
	
	public void completeLevel(Context context) {
		if(type == StatType.LEVEL){
			ToastSystem.showChallengeRestartedToast(getIconResource(), getTitleText(context));
		}
	}
	
	public void lostLife(Context context) {
		if(type == StatType.LIFE){
			ToastSystem.showChallengeRestartedToast(getIconResource(), getTitleText(context));
		}
	}
	
	public void gameOver(Context context){
		
	}
	
	public void loadGame(Context context){
		
	}
	
	public String getGameModeText(Context context){
		if(shadow){
			if(time){
				return context.getString(R.string.on_shadowtime_mode) + " ";
			} else {
				return context.getString(R.string.on_shadow_mode) + " ";
			}
		} else if(time){
			return context.getString(R.string.on_time_mode) + " ";
		}
		return "";
	}
}

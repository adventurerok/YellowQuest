package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.ui.ToastSystem;

import android.content.Context;


public abstract class Challenge {
	
	public StatTracker tracker;

	public Stat stat;
	
	public StatType type;
	
	
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
}

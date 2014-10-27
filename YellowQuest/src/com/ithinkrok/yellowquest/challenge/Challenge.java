package com.ithinkrok.yellowquest.challenge;

import android.content.Context;


public abstract class Challenge {
	
	public StatTracker tracker;

	public Stat stat;
	
	
	
	
	public Challenge(StatTracker tracker, Stat stat) {
		super();
		this.tracker = tracker;
		this.stat = stat;
	}


	public int getIconResource(){
		return stat.icon;
	}
	
	public abstract String getTitleText(Context context);
	public abstract String getProgressText(Context context);
	
	//Called when the stat it is tracking is updated
	public void update(Context context, int increase){
		
	}
	
	public void completeLevel(Context context){
		
	}
	
	public void lostLife(Context context){
		
	}
	
	public void gameOver(Context context){
		
	}
	
	public void loadGame(Context context){
		
	}
}

package com.ithinkrok.yellowquest.challenge;


public abstract class Challenge {
	
	public StatTracker tracker;

	public Stat stat;
	
	
	
	
	public Challenge(StatTracker tracker, Stat stat) {
		super();
		this.tracker = tracker;
		this.stat = stat;
	}


	//Called when the stat it is tracking is updated
	public void update(){
		
	}
	
	public void completeLevel(){
		
	}
	
	public void lostLife(){
		
	}
	
	public void gameOver(){
		
	}
}

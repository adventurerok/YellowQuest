package com.ithinkrok.yellowquest.challenge;


public class BasicChallenge extends Challenge {
	
	
	StatType type;
	int target;

	public BasicChallenge(StatTracker tracker, Stat stat, StatType type, int target) {
		super(tracker, stat);
		this.type = type;
		this.target = target;
	}

	@Override
	public void update() {
		if(tracker.getStat(stat, type) >= target){
			//Yay! Challenge complete!
		}
	}

}

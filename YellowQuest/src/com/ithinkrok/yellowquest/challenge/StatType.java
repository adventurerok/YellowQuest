package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.R;

public enum StatType {

	TOTAL(0),
	CHALLENGE(0),
	GAME(R.string.in_game),
	LEVEL(R.string.in_level),
	LIFE(R.string.in_life);
	
	public int suffix;
	
	private StatType(int suffix) {
		this.suffix = suffix;
	}
	
}

package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.R;

public enum Stat{
	RIGHT_SIDE_FLAG(R.drawable.achievement_overshot), //Tracked
	JUMP_OVER_BOXES(R.drawable.achievement_jumpman), //Tracked
	COMPLETE_LEVEL(R.drawable.challenge_level_up), //Tracked
	JUMPS(R.drawable.achievement_jumpman), //Tracked
	LEFT_DISTANCE(R.drawable.challenge_dont_go_right), //Tracked
	DEATHS(R.drawable.achievement_big_failure), //Tracked
	BONUS(0);
	
	public int icon;
	
	private Stat(int icon) {
		this.icon = icon;
	}
}
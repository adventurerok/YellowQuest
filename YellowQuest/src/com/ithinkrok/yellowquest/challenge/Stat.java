package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.R;

public enum Stat{
	RIGHT_SIDE_FLAG(R.drawable.achievement_overshot, R.string.right_side_flag, R.string.right_side_flagging), //Tracked
	JUMP_OVER_BOXES(R.drawable.achievement_jumpman, R.string.jump_over_boxes, R.string.jumped_over_boxes), //Tracked
	COMPLETE_LEVEL(R.drawable.challenge_level_up, 0, 0), //Tracked
	JUMPS(R.drawable.achievement_jumpman, 0, 0), //Tracked
	LEFT_DISTANCE(R.drawable.challenge_dont_go_right, 0, 0), //Tracked
	DEATHS(R.drawable.achievement_big_failure, 0, 0), //Tracked
	BONUS(0, 0, 0);
	
	public int icon;
	public int title; // %1$d is number you have to do, %2$s is StatType suffix
	public int progress;
	
	private Stat(int icon, int title, int progress) {
		this.icon = icon;
		this.title = title;
		this.progress = progress;
	}
}
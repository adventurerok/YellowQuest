package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.R;

public enum Stat{
	RIGHT_SIDE_FLAG(R.drawable.achievement_overshot, R.string.challenge_right_side_flag, R.string.progress_right_side_flag, 0, 0), //Tracked
	JUMP_OVER_BOXES(R.drawable.achievement_jumpman, R.string.challenge_jump_over_boxes, R.string.progress_jump_over_boxes, R.string.fail_jumped_over_boxes, R.string.fail_jumped_over_boxes_x), //Tracked
	COMPLETE_LEVEL(R.drawable.challenge_level_up, 0, 0, R.string.fail_leveled_up, 0), //Tracked
	JUMPS(R.drawable.achievement_jumpman, 0, 0, R.string.fail_jumped, R.string.fail_jumped_x), //Tracked
	LEFT_DISTANCE(R.drawable.challenge_dont_go_right, 0, 0, R.string.fail_go_left, 0), //Tracked
	DEATHS(R.drawable.achievement_big_failure, 0, 0, R.string.fail_died, R.string.fail_died_x), //Tracked
	BONUS(0, 0, 0, 0, 0);
	
	public int icon;
	public int title; // %1$d is number you have to do, %2$s is StatType suffix
	public int progress;
	public int fail0;
	public int failMany;
	
	private Stat(int icon, int title, int progress, int fail0, int failMany) {
		this.icon = icon;
		this.title = title;
		this.progress = progress;
		this.fail0 = fail0;
		this.failMany = failMany;
	}
}
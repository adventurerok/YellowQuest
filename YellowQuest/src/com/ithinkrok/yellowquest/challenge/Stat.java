package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.R;

public enum Stat{
	RIGHT_SIDE_FLAG(R.drawable.achievement_overshot, R.string.challenge_right_side_flag, R.string.progress_right_side_flag, 0, 0, 0, 0), //Tracked
	JUMP_OVER_BOXES(R.drawable.achievement_jumpman, R.string.challenge_jump_over_boxes, R.string.progress_jump_over_boxes, R.string.fail_jumped_over_boxes, R.string.fail_jumped_over_boxes_x, R.string.without_jumping_over_box, 0), //Tracked
	COMPLETE_LEVEL(R.drawable.challenge_level_up, R.string.challenge_level_up, R.string.progress_level_up, R.string.fail_leveled_up, 0, 0, 0), //Tracked
	JUMPS(R.drawable.achievement_jumpman, 0, 0, R.string.fail_jumped, R.string.fail_jumped_x, R.string.without_jumping, 0), //Tracked
	LEFT_DISTANCE(R.drawable.challenge_dont_go_right, 0, 0, R.string.fail_go_left, 0, R.string.without_go_left, 0), //Tracked
	DEATHS(R.drawable.achievement_big_failure, 0, 0, R.string.fail_died, R.string.fail_died_x, R.string.without_dying, 0), //Tracked
	BONUS(R.drawable.achievement_bonus_time, 0, 0, 0, 0, 0, 0);
	
	public int icon;
	public int title; // %1$d is number you have to do, %2$s is StatType suffix
	public int progress;
	public int fail0;
	public int failMany;
	public int without0;
	public int withoutMany;
	
	private Stat(int icon, int title, int progress, int fail0, int failMany, int without0, int withoutMany) {
		this.icon = icon;
		this.title = title;
		this.progress = progress;
		this.fail0 = fail0;
		this.failMany = failMany;
		this.without0 = without0;
		this.withoutMany = withoutMany;
	}
}
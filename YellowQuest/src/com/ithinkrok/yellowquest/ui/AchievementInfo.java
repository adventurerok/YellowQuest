package com.ithinkrok.yellowquest.ui;

import java.util.HashMap;

import android.content.Context;

import com.ithinkrok.yellowquest.R;

public class AchievementInfo {

	private static HashMap<String, AchievementInfo> info = new HashMap<String, AchievementInfo>();

	public int icon;
	public int name;
	public int description;
	public int reward;

	public AchievementInfo(int icon, int name, int description, int reward) {
		super();
		this.icon = icon;
		this.name = name;
		this.description = description;
		this.reward = reward;
	}

	public static void load(Context context) {
		info.put(context.getString(R.string.achievement_almost), new AchievementInfo(R.drawable.achievement_almost,
				R.string.achievement_almost_name, R.string.achievement_almost_desc, 99));
		info.put(context.getString(R.string.achievement_big_failure), new AchievementInfo(
				R.drawable.achievement_big_failure, R.string.achievement_big_failure_name,
				R.string.achievement_big_failure_desc, 1));
		info.put(context.getString(R.string.achievement_overshot), new AchievementInfo(R.drawable.achievement_overshot,
				R.string.achievement_overshot_name, R.string.achievement_overshot_desc, 5000));
		info.put(context.getString(R.string.achievement_easy), new AchievementInfo(R.drawable.achievement_easy,
				R.string.achievement_easy_name, R.string.achievement_easy_desc, 1000));
		info.put(context.getString(R.string.achievement_medium), new AchievementInfo(R.drawable.achievement_medium,
				R.string.achievement_medium_name, R.string.achievement_medium_desc, 3000));
		info.put(context.getString(R.string.achievement_hard), new AchievementInfo(R.drawable.achievement_hard,
				R.string.achievement_hard_name, R.string.achievement_hard_desc, 9000));
		info.put(context.getString(R.string.achievement_expert), new AchievementInfo(R.drawable.achievement_expert,
				R.string.achievement_expert_name, R.string.achievement_expert_desc, 27000));
		info.put(context.getString(R.string.achievement_impossible), new AchievementInfo(
				R.drawable.achievement_impossible, R.string.achievement_impossible_name,
				R.string.achievement_impossible_desc, 81000));
		info.put(context.getString(R.string.achievement_jumpman), new AchievementInfo(R.drawable.achievement_jumpman,
				R.string.achievement_jumpman_name, R.string.achievement_jumpman_desc, 2000));
		info.put(context.getString(R.string.achievement_bonus_time), new AchievementInfo(
				R.drawable.achievement_bonus_time, R.string.achievement_bonus_time_name,
				R.string.achievement_bonus_time_desc, 1000));
		info.put(context.getString(R.string.achievement_up_up_and_away), new AchievementInfo(R.drawable.bonus_up,
				R.string.achievement_up_up_away_name, R.string.achievement_up_up_away_desc, 500));
		info.put(context.getString(R.string.achievement_sacrifice), new AchievementInfo(R.drawable.bonus_life,
				R.string.achievement_sacrifice_name, R.string.achievement_sacrifice_desc, 3000));
		info.put(context.getString(R.string.achievement_bounceback), new AchievementInfo(R.drawable.bonus_bounce,
				R.string.achievement_bounceback_name, R.string.achievement_bounceback_desc, 1500));
		info.put(context.getString(R.string.achievement_headstart), new AchievementInfo(R.drawable.achievement_headstart,
				R.string.achievement_headstart_name, R.string.achievement_headstart_desc,300));
		info.put(context.getString(R.string.achievement_time_for_an_upgrade), new AchievementInfo(R.drawable.achievement_upgrade,
				R.string.achievement_upgrade_name, R.string.achievement_unlocker_desc,1000));
		info.put(context.getString(R.string.achievement_the_unlocker), new AchievementInfo(R.drawable.achievement_unlocker,
				R.string.achievement_unlocker_name, R.string.achievement_unlocker_desc,2000));
		info.put(context.getString(R.string.achievement_omnipotent), new AchievementInfo(R.drawable.achievement_omnipotent,
				R.string.achievement_omnipotent_name, R.string.achievement_omnipotent_desc,12000));
		info.put(context.getString(R.string.achievement_wormhole), new AchievementInfo(R.drawable.bonus_teleport,
				R.string.achievement_wormhole_name, R.string.achievement_wormhole_desc,2000));
		info.put(context.getString(R.string.achievement_frozen_time), new AchievementInfo(R.drawable.bonus_time_stop,
				R.string.achievement_frozen_time_name, R.string.achievement_frozen_time_desc,1500));
		info.put(context.getString(R.string.achievement_arrows), new AchievementInfo(R.drawable.bonus_troll,
				R.string.achievement_arrows_name, R.string.achievement_arrows_desc,20000));

	}

	public static AchievementInfo getAchievement(String id) {
		return info.get(id);
	}

	public static AchievementInfo getAchievement(Context context, int id) {
		return info.get(context.getString(id));
	}

}

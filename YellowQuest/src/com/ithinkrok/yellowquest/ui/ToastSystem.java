package com.ithinkrok.yellowquest.ui;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

import com.ithinkrok.yellowquest.*;
import com.ithinkrok.yellowquest.MainActivity.GameState;

public class ToastSystem {

	private static MainActivity context;
	
	private static View layoutAchievements[] = new View[5];
	private static View layoutProgress[] = new View[1];
	private static View layoutUnlocks[] = new View[5];
	
	private static int posAchievements = 0;
	private static int posProgress = 0;
	private static int posUnlocks = 0;

	public static void setContext(MainActivity context) {
		ToastSystem.context = context;
	}

	public static void showTextToast(int res) {
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}

	public static void showTextToast(String res) {
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("InflateParams")
	public static void showAchievementToast(AchievementInfo achievement) {
		
		long allstart = System.nanoTime();
		
		View layout;
		
		if(layoutAchievements[posAchievements] != null) layout = layoutAchievements[posAchievements];
		else {
			layout = layoutAchievements[posAchievements] =context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		}
		
		++posAchievements;
		if(posAchievements >= layoutAchievements.length) posAchievements = 0;
		
		layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		
		long start = System.nanoTime();

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageDrawable(context.loadDrawable(achievement.icon));
		
		long time = System.nanoTime() - start;
		if(time > 20 * 1000000){
			Log.w("YellowQuest", "loadimageasset took " + (time / 1000000));
		}

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(achievement.name);

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(achievement.description);

		TextView reward = (TextView) layout.findViewById(R.id.achievement_reward);
		String rewardText = context.getText(R.string.ach_reward).toString();
		String rewardNum = BoxMath.formatNumber(achievement.reward);
		rewardText = StringFormatter.format(rewardText, rewardNum);
		reward.setText(rewardText);

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();
		
		long alltime = System.nanoTime() - allstart;
		
		if(alltime > 20 * 1000000){
			Log.w("YellowQuest", "showachievementtoast took " + (alltime / 1000000));
		}

	}
	
	@SuppressLint("InflateParams")
	public static void showBonusToast(int bonusText, int bonusReward) {
		long allstart = System.nanoTime();
		
		View layout;
		
		if(layoutAchievements[posAchievements] != null) layout = layoutAchievements[posAchievements];
		else {
			layout = layoutAchievements[posAchievements] =context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		}
		
		++posAchievements;
		if(posAchievements >= layoutAchievements.length) posAchievements = 0;
		
		layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		
		long start = System.nanoTime();

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageDrawable(context.loadDrawable(R.drawable.achievement_bonus_time));
		
		long time = System.nanoTime() - start;
		if(time > 20 * 1000000){
			Log.w("YellowQuest", "loadimageasset took " + (time / 1000000));
		}

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(R.string.bonus_area_complete);

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(bonusText);

		TextView reward = (TextView) layout.findViewById(R.id.achievement_reward);
		String rewardText = context.getText(R.string.ach_reward).toString();
		String rewardNum = BoxMath.formatNumber(bonusReward);
		rewardText = StringFormatter.format(rewardText, rewardNum);
		reward.setText(rewardText);

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();
		
		long alltime = System.nanoTime() - allstart;
		
		if(alltime > 20 * 1000000){
			Log.w("YellowQuest", "showachievementtoast took " + (alltime / 1000000));
		}

	}
	
	public static void releaseCache(){
		for(int d = 0; d < layoutAchievements.length; ++d){
			layoutAchievements[d] = null;
		}
		
		for(int d = 0; d < layoutProgress.length; ++d){
			layoutProgress[d] = null;
		}
		
		for(int d = 0; d <  layoutUnlocks.length; ++d){
			layoutUnlocks[d] = null;
		}
	}
	
	@SuppressLint("InflateParams")
	public static void generateCache(){
		for(int d = 0; d < layoutAchievements.length; ++d){
			if(layoutAchievements[d] != null) return;
			layoutAchievements[d] = context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		}
		for(int d = 0; d < layoutProgress.length; ++d){
			if(layoutProgress[d] != null) return;
			layoutProgress[d] = context.getLayoutInflater().inflate(R.layout.progress, null, false);
		}
		for(int d = 0; d < layoutUnlocks.length; ++d){
			if(layoutUnlocks[d] != null) return;
			layoutUnlocks[d] = context.getLayoutInflater().inflate(R.layout.unlock, null, false);
		}
	}

	@SuppressLint("InflateParams")
	public static void showHiscoreToast(int current, int old) {
		View layout;
		
		if(layoutAchievements[posAchievements] != null) layout = layoutAchievements[posAchievements];
		else {
			layout = layoutAchievements[posAchievements] =context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		}
		
		++posAchievements;
		if(posAchievements >= layoutAchievements.length) posAchievements = 0;

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageDrawable(context.loadDrawable(R.drawable.new_hiscore));

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(StringFormatter.format(context.getString(R.string.new_hiscore), current));

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(StringFormatter.format(context.getString(R.string.old_hiscore), current - old, old));

		TextView reward = (TextView) layout.findViewById(R.id.achievement_reward);
		reward.setText("");

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);
		toast.show();

		// Log.i("YellowQuest", "End make achievement toast");
	}

	@SuppressLint("InflateParams")
	public static void showChallengeProgressToast(int iconRes, String text, double percent) {
		percent /= 100d;

		View layout;
		
		if(layoutProgress[posProgress] != null) layout = layoutProgress[posProgress];
		else {
			layout = layoutProgress[posProgress] =context.getLayoutInflater().inflate(R.layout.progress, null, false);
		}
		
		++posProgress;
		if(posProgress >= layoutProgress.length) posProgress = 0;

		ImageView icon = (ImageView) layout.findViewById(R.id.progress_icon);
		icon.setImageDrawable(context.loadDrawable(iconRes));

		TextView name = (TextView) layout.findViewById(R.id.progress_name);
		name.setText(text);

		View progressLeft = layout.findViewById(R.id.progress_bar_left);
		RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams) progressLeft.getLayoutParams();
		leftParams.width = (int) (280 * percent);
		progressLeft.setLayoutParams(leftParams);

		View progressRight = layout.findViewById(R.id.progress_bar_right);
		RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams) progressRight.getLayoutParams();
		rightParams.width = (int) (280 * (1 - percent));
		progressRight.setLayoutParams(rightParams);
		
		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);

		boolean game = context.state == GameState.GAME;

		if (game)
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		else
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);

		toast.show();

	}

	public static void showChallengeCompleteToast(int iconRes, String text){
		showChallengeMessageToast(iconRes, R.string.challenge_complete, text);
	}
	
	public static void showChallengeFailedToast(int iconRes, String text){
		showChallengeMessageToast(iconRes, R.string.challenge_failed, text);
	}
	
	public static void showChallengeRestartedToast(int iconRes, String text){
		showChallengeMessageToast(iconRes, R.string.challenge_restarted, text);
	}
	
	@SuppressLint("InflateParams")
	public static void showChallengeMessageToast(int iconRes, int message, String text) {
		View layout;
		
		if(layoutAchievements[posAchievements] != null) layout = layoutAchievements[posAchievements];
		else {
			layout = layoutAchievements[posAchievements] =context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		}
		
		++posAchievements;
		if(posAchievements >= layoutAchievements.length) posAchievements = 0;

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageDrawable(context.loadDrawable(iconRes));

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(message);

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(text);
		
		if(text.length() > 80){
			RelativeLayout.LayoutParams params = (LayoutParams) desc.getLayoutParams();
			params.width = 350;
			desc.setLayoutParams(params);
		}

		TextView reward = (TextView) layout.findViewById(R.id.achievement_reward);
		reward.setText("");

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);

		boolean game = context.state == GameState.GAME;

		if (game)
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		else
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);

		toast.show();
	}

	@SuppressLint("InflateParams")
	public static void showRankToast(int rank) {
		View layout;
		
		if(layoutAchievements[posAchievements] != null) layout = layoutAchievements[posAchievements];
		else {
			layout = layoutAchievements[posAchievements] =context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		}
		
		++posAchievements;
		if(posAchievements >= layoutAchievements.length) posAchievements = 0;

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageDrawable(context.loadDrawable(R.drawable.new_hiscore));

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(StringFormatter.format(context.getString(R.string.new_rank), rank));

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(StringFormatter.format(context.getString(R.string.rank_up), rank));

		TextView reward = (TextView) layout.findViewById(R.id.achievement_reward);
		reward.setText("");

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);

		boolean game = context.state == GameState.GAME;

		if (game)
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		else
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);

		toast.show();

		// Log.i("YellowQuest", "End make achievement toast");
	}

	@SuppressLint("InflateParams")
	public static void showUnlockToast(int iconId, int textId) {
		View layout;
		
		if(layoutUnlocks[posUnlocks] != null) layout = layoutUnlocks[posUnlocks];
		else {
			layout = layoutUnlocks[posUnlocks] =context.getLayoutInflater().inflate(R.layout.unlock, null, false);
		}
		
		++posUnlocks;
		if(posUnlocks >= layoutUnlocks.length) posUnlocks = 0;

		ImageView icon = (ImageView) layout.findViewById(R.id.unlock_icon);
		icon.setImageDrawable(context.loadDrawable(iconId));

		TextView name = (TextView) layout.findViewById(R.id.unlock_text);
		name.setText(textId);

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();

		// Log.i("YellowQuest", "End make achievement toast");
	}

}

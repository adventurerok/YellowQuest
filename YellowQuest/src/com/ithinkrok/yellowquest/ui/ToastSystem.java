package com.ithinkrok.yellowquest.ui;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.ithinkrok.yellowquest.*;
import com.ithinkrok.yellowquest.MainActivity.GameState;

public class ToastSystem {

	private static MainActivity context;

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
		// Log.i("YellowQuest", "Start make achievement toast");
		View layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageResource(achievement.icon);

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(achievement.name);

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(achievement.description);

		TextView reward = (TextView) layout.findViewById(R.id.achievement_reward);
		String rewardText = context.getText(R.string.ach_reward).toString();
		String rewardNum = BoxMath.formatNumber(achievement.reward);
		rewardText = String.format(rewardText, rewardNum);
		reward.setText(rewardText);

		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();

		// Log.i("YellowQuest", "End make achievement toast");
	}

	@SuppressLint("InflateParams")
	public static void showHiscoreToast(int current, int old) {
		// Log.i("YellowQuest", "Start make achievement toast");
		View layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageResource(R.drawable.new_hiscore);

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(String.format(context.getString(R.string.new_hiscore), current));

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(String.format(context.getString(R.string.old_hiscore), current - old, old));

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

		View layout = context.getLayoutInflater().inflate(R.layout.progress, null, false);

		ImageView icon = (ImageView) layout.findViewById(R.id.progress_icon);
		icon.setImageResource(iconRes);

		TextView name = (TextView) layout.findViewById(R.id.progress_name);
		name.setText(text);

		View progressLeft = layout.findViewById(R.id.progress_bar_left);
		RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams) progressLeft.getLayoutParams();
		leftParams.width = (int) (250 * percent);
		progressLeft.setLayoutParams(leftParams);

		View progressRight = layout.findViewById(R.id.progress_bar_right);
		RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams) progressRight.getLayoutParams();
		rightParams.width = (int) (250 * (1 - percent));
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

	@SuppressLint("InflateParams")
	public static void showChallengeCompleteToast(int iconRes, String text) {
		View layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageResource(R.drawable.new_hiscore);

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(R.string.challenge_complete);

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(text);

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
		// Log.i("YellowQuest", "Start make achievement toast");
		View layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);

		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageResource(R.drawable.new_hiscore);

		TextView name = (TextView) layout.findViewById(R.id.achievement_name);
		name.setText(String.format(context.getString(R.string.new_rank), rank));

		TextView desc = (TextView) layout.findViewById(R.id.achievement_desc);
		desc.setText(String.format(context.getString(R.string.rank_up), rank));

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
		// Log.i("YellowQuest", "Start make achievement toast");
		View layout = context.getLayoutInflater().inflate(R.layout.unlock, null, false);

		ImageView icon = (ImageView) layout.findViewById(R.id.unlock_icon);
		icon.setImageResource(iconId);

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

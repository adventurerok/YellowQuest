package com.ithinkrok.yellowquest.ui;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.ithinkrok.yellowquest.*;

public class ToastSystem {
	
	
	private static MainActivity context;
	
	
	
	public static void setContext(MainActivity context) {
		ToastSystem.context = context;
	}

	public static void showTextToast(int res){
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}
	
	public static void showTextToast(String res){
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}
	
	@SuppressLint("InflateParams")
	public static void showAchievementToast(AchievementInfo achievement){
		//Log.i("YellowQuest", "Start make achievement toast");
		View layout = context.getLayoutInflater().inflate(R.layout.achievement, null, false);
		
		ImageView icon = (ImageView) layout.findViewById(R.id.achievement_icon);
		icon.setImageResource(achievement.icon);
		
		TextView name =(TextView) layout.findViewById(R.id.achievement_name);
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
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();
		
		//Log.i("YellowQuest", "End make achievement toast");
	}
	
	@SuppressLint("InflateParams")
	public static void showUnlockToast(int iconId, int textId){
		//Log.i("YellowQuest", "Start make achievement toast");
		View layout = context.getLayoutInflater().inflate(R.layout.unlock, null, false);
		
		ImageView icon = (ImageView) layout.findViewById(R.id.unlock_icon);
		icon.setImageResource(iconId);
		
		TextView name =(TextView) layout.findViewById(R.id.unlock_text);
		name.setText(textId);
		
		
		Toast toast = new Toast(context);
		toast.setView(layout);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 10);
		toast.show();
		
		//Log.i("YellowQuest", "End make achievement toast");
	}

}

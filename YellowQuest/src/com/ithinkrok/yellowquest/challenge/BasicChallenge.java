package com.ithinkrok.yellowquest.challenge;

import android.content.Context;

import com.ithinkrok.yellowquest.StringFormatter;
import com.ithinkrok.yellowquest.ui.PowerInfo;
import com.ithinkrok.yellowquest.ui.ToastSystem;


public class BasicChallenge extends Challenge {
	
	
	int target;
	int step;

	public BasicChallenge(StatTracker tracker, Stat stat, StatType type, int target) {
		super(tracker, stat, type);
		this.target = target;
		
		if(target > 5999) step = 2000;
		else if(target > 2999) step = 1000;
		else if(target > 1499) step = 500;
		else if(target > 599) step = 200;
		else if(target > 299) step = 100;
		else if(target > 149) step = 50;
		else if(target > 59) step = 20;
		else if(target > 20 && (target % 10) == 0) step = 10;
		else if(target > 5 && (target % 5) == 0) step = 5;
		else if(target > 15 && target % 8 == 0) step = target / 8;
		else if(target > 9 && target % 4 == 0) step = target / 4;
		else if(target > 9 && target % 2 == 0) step = target / 2;
		else if(target > 9) step = (target / 2) + 1;
		else step = 1;
	}
	
	

	public BasicChallenge(StatTracker tracker, Stat stat, StatType type, int target, int step) {
		super(tracker, stat, type);
		this.target = target;
		this.step = step;
	}



	@Override
	public void update(Context context, Stat stat, int increase) {
		int current = tracker.getStat(stat, type, power);
		if(current >= target){
			ToastSystem.showChallengeCompleteToast(getIconResource(), getTitleText(context));
			//tracker.nextChallenge(true);
			tracker.completeChallenge();
		} else {
			int old = current - increase;
			if(current % step == 0 || (current % step) < (old % step) || increase >= step){
				ToastSystem.showChallengeProgressToast(getIconResource(), getProgressText(context), ((double)current/(double)target)*100d);
			}
		}
	}

	@Override
	public String getTitleText(Context context) {
		String suffix = "";
		if(type.suffix != 0) suffix = context.getString(type.suffix);
		String title = "";
		if(stat.title == 0) return title;
		String with = "";
		if(power != null) with = PowerInfo.getData(power).getWithText(context);
		title = context.getString(stat.title);
		title = StringFormatter.format(title, target, suffix, "", with);
		return title;
	}

	@Override
	public String getProgressText(Context context) {
		String progress = "";
		if(stat.progress == 0) return progress;
		progress = context.getString(stat.progress);
		progress = StringFormatter.format(progress, tracker.getStat(stat, type), target);
		return progress;
	}


	

}

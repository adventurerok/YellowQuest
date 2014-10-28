package com.ithinkrok.yellowquest.challenge;

import android.content.Context;

import com.ithinkrok.yellowquest.StringFormatter;
import com.ithinkrok.yellowquest.ui.PowerInfo;
import com.ithinkrok.yellowquest.ui.ToastSystem;


public class WithoutChallenge extends Challenge {
	
	
	int target;
	int step;
	Stat limited;
	int limit;

	public WithoutChallenge(StatTracker tracker, Stat stat, StatType type, int target, Stat limited, int limit) {
		super(tracker, stat, type);
		this.target = target;
		this.limited = limited;
		this.limit = limit;
		
		if(target > 20 && (target % 10) == 0) step = 10;
		else if(target > 5 && (target % 5) == 0) step = 5;
		else if(target > 15 && target % 8 == 0) step = target / 8;
		else if(target > 9 && target % 4 == 0) step = target / 4;
		else if(target > 9 && target % 2 == 0) step = target / 2;
		else if(target > 9) step = (target / 2) + 1;
		else step = 1;
	}
	
	

	public WithoutChallenge(StatTracker tracker, Stat stat, StatType type, int target, int step, Stat limited, int limit) {
		super(tracker, stat, type);
		this.target = target;
		this.step = step;
		this.limited = limited;
		this.limit = limit;
	}
	
	@Override
	public boolean isTracking(Stat stat) {
		return super.isTracking(stat) || limited == stat;
	}



	@Override
	public void update(Context context, Stat gained, int increase) {
		int current = tracker.getStat(stat, type, power, shadow, time);
		int lat = tracker.getStat(limited, type, power, shadow, time);
		if(lat > limit){
			if(gained == limited && lat - increase <= limit){
				if(limit == 0) ToastSystem.showChallengeFailedToast(getIconResource(), context.getString(gained.fail0));
			}
			return;
		}
		if(gained != stat) return;
		
		
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
		String without = "";
		if(limit == 0){
			if(limited.without0 != 0) without = context.getString(limited.without0);
		} else {
			if(limited.withoutMany != 0) without = context.getString(limited.withoutMany);
		}
		String title = "";
		if(stat.title == 0) return title;
		String with = "";
		if(power != null) with = PowerInfo.getData(power).getWithText(context);
		String mode = getGameModeText(context);
		title = context.getString(stat.title);
		title = StringFormatter.format(title, target, suffix, without, with, mode);
		return title;
	}

	@Override
	public String getProgressText(Context context) {
		String progress = "";
		if(stat.progress == 0) return progress;
		progress = context.getString(stat.progress);
		progress = StringFormatter.format(progress, tracker.getStat(stat, type, power, shadow, time), target);
		return progress;
	}
	


	

}

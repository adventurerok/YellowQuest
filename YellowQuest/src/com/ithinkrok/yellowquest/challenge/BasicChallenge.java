package com.ithinkrok.yellowquest.challenge;

import com.ithinkrok.yellowquest.ui.ToastSystem;

import android.content.Context;


public class BasicChallenge extends Challenge {
	
	
	StatType type;
	int target;
	int step;

	public BasicChallenge(StatTracker tracker, Stat stat, StatType type, int target) {
		super(tracker, stat);
		this.type = type;
		this.target = target;
		
		if(target > 20 && (target % 10) == 0) step = 10;
		else if(target > 5 && (target % 5) == 0) step = 5;
		else if(target > 15 && target % 8 == 0) step = target / 8;
		else if(target > 9 && target % 4 == 0) step = target / 4;
		else if(target > 9 && target % 2 == 0) step = target / 2;
		else if(target > 9) step = (target / 2) + 1;
		else step = 1;
	}
	
	

	public BasicChallenge(StatTracker tracker, Stat stat, StatType type, int target, int step) {
		super(tracker, stat);
		this.type = type;
		this.target = target;
		this.step = step;
	}



	@Override
	public void update(Context context, int increase) {
		int current = tracker.getStat(stat, type);
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
		title = context.getString(stat.title);
		title = String.format(title, target, suffix);
		return title;
	}

	@Override
	public String getProgressText(Context context) {
		String progress = "";
		if(stat.progress == 0) return progress;
		progress = context.getString(stat.progress);
		progress = String.format(progress, tracker.getStat(stat, type), target);
		return progress;
	}


	

}
package com.ithinkrok.yellowquest;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameProgress {

	private MainActivity activity;
	private GoogleApiClient client;
	//private 
	
	public void addAchievement(String achievement){
		client = activity.getApiClient();
		Games.Achievements.unlock(client, achievement);
	}
	
}

package com.ithinkrok.yellowquest;

import java.util.ArrayList;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameProgress {

	private MainActivity activity;
	private GoogleApiClient client;
	
	private ArrayList<String> achievements = new ArrayList<String>();
	
	
	//Use gamedata to save achievements
	//0 = not got
	//1 = got, but only offline
	//2 = got online
	
	public GameProgress(MainActivity activity) {
		super();
		this.activity = activity;
	}


	public void addAchievement(String achievement){
		if(achievements.contains(achievement)) return;
		client = activity.getApiClient();
		if(client == null || !client.isConnected()) return;
		Games.Achievements.unlock(client, achievement);
		achievements.add(achievement);
	}
	
}

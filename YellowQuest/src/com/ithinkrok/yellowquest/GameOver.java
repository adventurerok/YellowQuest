package com.ithinkrok.yellowquest;

import android.content.Context;

public class GameOver {

	public int time = 25;
	public int type = 0;
	public String message = "Game Over";
	
	
//	public GameOver(int type, String message) {
//		super();
//		this.type = type;
//		this.message = message;
//	}
	
	public GameOver(int type, int message, Context context){
		this.type = type;
		this.message = context.getString(message);
	}
	
	
	
}

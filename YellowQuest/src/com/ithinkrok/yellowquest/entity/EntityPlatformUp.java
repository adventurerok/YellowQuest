package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformUp extends EntityPlatform {

	private static final Paint PAINT_GREEN = new Paint();
	
	static {
		PAINT_GREEN.setColor(0xff00ff00);
	}
	
	public EntityPlatformUp(YellowQuest game) {
		super(game);
		this.color = PAINT_GREEN;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		this.y_velocity = 5; // 45 ups
		//this.y_velocity = 11.25; // 20 ups
	}
	
	@Override
	public void noPlayer(EntityPlayer player) {
		this.y_velocity = 0;
	}

	@Override
	public void install() {
		game.bgenY += 5 + game.random(100);
	}
	
}

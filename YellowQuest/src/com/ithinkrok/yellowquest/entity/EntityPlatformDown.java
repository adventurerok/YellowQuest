package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformDown extends EntityPlatform {

	private static final Paint PAINT_RED = new Paint();
	
	static {
		PAINT_RED.setColor(0xffff0000);
	}
	
	public EntityPlatformDown(YellowQuest game) {
		super(game);
		this.color = PAINT_RED;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		this.y_velocity = -5; // 45 ups
		//this.y_velocity = -11.25; //20 ups
	}
	
	@Override
	public void install() {
		game.bgenY -= 30 + game.random(100);
	}

}

package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformConveyor extends EntityPlatform {

	private static final Paint PAINT_GREY = new Paint();
	
	static {
		PAINT_GREY.setColor(0xff888888);
	}
	
	public EntityPlatformConveyor(YellowQuest game) {
		super(game);
		this.color = PAINT_GREY;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		player.x_velocity -= 1.6; // 45 ups
		//player.x_velocity -= 1.35; // 20 ups
	}

}

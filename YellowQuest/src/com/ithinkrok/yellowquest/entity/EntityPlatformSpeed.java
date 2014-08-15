package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformSpeed extends EntityPlatform {
	
	private static final Paint PAINT_SPEED = new Paint();
	
	static {
		PAINT_SPEED.setColor(0xffeaaa6a);
	}

	public EntityPlatformSpeed(YellowQuest game) {
		super(game);
		this.color = PAINT_SPEED;
	}
	
	@Override
	public void install() {
		this.accel = 35;
	}

}

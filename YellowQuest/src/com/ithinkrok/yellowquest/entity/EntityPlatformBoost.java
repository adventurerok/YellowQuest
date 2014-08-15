package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformBoost extends EntityPlatform {
	
	private static final Paint PAINT_FABDAD = new Paint();
	
	static {
		PAINT_FABDAD.setColor(0xfffabdad);
	}

	public EntityPlatformBoost(YellowQuest game) {
		super(game);
		this.color = PAINT_FABDAD;
	}
	
	@Override
	public void install() {
		this.slip = 1.2;
	}

}

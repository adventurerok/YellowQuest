package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformJumpHide extends EntityPlatform {

	private static Paint PAINT_PURPLE = new Paint();
	
	static {
		PAINT_PURPLE.setColor(0xff882277);
	}
	
	public EntityPlatformJumpHide(YellowQuest game) {
		super(game);
		this.color = PAINT_PURPLE;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		player.y_velocity = 14;
		this.remove = true;
	}

}

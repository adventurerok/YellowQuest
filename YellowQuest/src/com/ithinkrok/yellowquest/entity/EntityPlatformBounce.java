package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformBounce extends EntityPlatform {

	private static final Paint PAINT_MAGENTA = new Paint();
	
	static {
		PAINT_MAGENTA.setColor(0xffff00ff);
	}
	
	public EntityPlatformBounce(YellowQuest game) {
		super(game);
		this.color = PAINT_MAGENTA;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(player.onGround) player.y_velocity = 13;
	}
	
	@Override
	public void install() {
		game.bgenY += 2 + game.random(100);
	}

}

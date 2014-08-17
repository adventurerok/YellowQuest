package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitBounce extends Trait {
	
	private static final Paint PAINT_MAGENTA = new Paint();
	
	static {
		PAINT_MAGENTA.setColor(0xffff00ff);
	}

	public TraitBounce(EntityPlatform parent) {
		super(parent);
		color = PAINT_MAGENTA;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(player.onGround) player.y_velocity = 13;
	}

	@Override
	public String getName() {
		return "bounce";
	}

	@Override
	public int getIndex() {
		return 5;
	}
	
	@Override
	public void install() {
		parent.game.bgenY += 2 + parent.game.random(100);
	}

}

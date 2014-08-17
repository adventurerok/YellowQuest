package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitDown extends Trait {
	
	private static final Paint PAINT_RED = new Paint();
	
	static {
		PAINT_RED.setColor(0xffff0000);
	}

	public TraitDown(EntityPlatform parent) {
		super(parent);
		color = PAINT_RED;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		parent.y_velocity = -5; 
	}

	@Override
	public String getName() {
		return "down";
	}

	@Override
	public int getIndex() {
		return 0;
	}
	
	@Override
	public void install() {
		parent.game.bgenY -= 30 + parent.game.random(100);
	}

}

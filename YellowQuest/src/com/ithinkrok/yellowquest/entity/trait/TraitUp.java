package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitUp extends Trait {
	
	private static final Paint PAINT_GREEN = new Paint();
	
	static {
		PAINT_GREEN.setColor(0xff00ff00);
	}

	public TraitUp(EntityPlatform parent) {
		super(parent);
		color = PAINT_GREEN;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		parent.y_velocity = 5; // 45 ups
	}
	
	@Override
	public void noPlayer(EntityPlayer player) {
		parent.y_velocity = 0;
	}

	@Override
	public void install() {
		parent.game.bgenY += 5 + parent.game.random(100);
	}

	@Override
	public String getName() {
		return "up";
	}

	@Override
	public int getIndex() {
		return 0;
	}

}

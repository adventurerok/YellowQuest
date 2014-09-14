package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitConveyor extends Trait {
	
	private static final Paint PAINT_GREY = new Paint();
	
	static {
		PAINT_GREY.setColor(0xff888888);
	}

	public TraitConveyor(EntityPlatform parent) {
		super(parent);
		color = PAINT_GREY;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(player.hasPower("troll")) player.x_velocity += 1.6;
		else player.x_velocity -= 1.6;
	}

	@Override
	public String getName() {
		return "conveyor";
	}

	@Override
	public int getIndex() {
		return 95;
	}

}

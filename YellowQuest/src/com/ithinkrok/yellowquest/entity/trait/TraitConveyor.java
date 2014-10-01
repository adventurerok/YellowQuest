package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.ui.PowerInfo;

public class TraitConveyor extends Trait {
	
	public static final Paint PAINT_GREY = new Paint();
	
	private static boolean powerUnlock = false;
	
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
		
		if(!powerUnlock){
			PowerInfo.getData("teleport").unlock(player.game.getContext());
			powerUnlock = true;
		}
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

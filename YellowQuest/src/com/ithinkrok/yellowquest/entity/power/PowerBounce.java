package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.Entity.EntityType;
import com.ithinkrok.yellowquest.entity.*;
import com.ithinkrok.yellowquest.entity.trait.TraitBounce;

public class PowerBounce extends Power {
	
	
	public PowerBounce(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		jumpIncrease = 3 + upgradeLevel;
		paint = TraitBounce.PAINT_MAGENTA;
	}
	
	
	@Override
	public void update(EntityPlayer player) {
		if(!player.onGround) return;
		if(player.intersecting != null && player.intersecting.type == EntityType.PLATFORM){
			if(((EntityPlatform)player.intersecting).hasTrait("bounce")) return;
		}
		player.y_velocity += YellowQuest.DEFAULT_JUMP + upgradeLevel;
		player.onGround = false;
	}

	@Override
	public String getName() {
		return "bounce";
	}

}

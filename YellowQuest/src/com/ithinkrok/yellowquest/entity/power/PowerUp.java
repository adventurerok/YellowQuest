package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerUp extends Power {
	
	int up = 1;

	public PowerUp(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		up = 1 + upgradeLevel;
	}

	@Override
	public String getName() {
		return "up";
	}
	
	@Override
	public void update(EntityPlayer player) {
		if(!player.onGround || player.intersecting == null) return;
		if(!(player.intersecting instanceof EntityPlatform)) return;
		player.move(0, up);
		player.onGround = true;
		player.intersecting.move(0, up);
	}

}

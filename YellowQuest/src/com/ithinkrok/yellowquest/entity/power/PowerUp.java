package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.trait.TraitUp;

public class PowerUp extends Power {
	
	public int up = 2;

	public PowerUp(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		up = 2 + upgradeLevel;
		paint = TraitUp.PAINT_GREEN;
	}

	@Override
	public String getName() {
		return "up";
	}
	
	@Override
	public void update(EntityPlayer player) {
		if(!player.onGround || player.intersecting == null) return;
		if(!(player.intersecting instanceof EntityPlatform)) return;
		int maxUp = (1000 / up) + 1;
		if("up".equals(player.intersecting.bonusType)) maxUp = (1500 / up) + 1;
		if(maxUp < player.intersecting.timeOnPlatform) return;
		player.move(0, up);
		player.onGround = true;
		player.intersecting.move(0, up);
	}

}

package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerLife extends Power {
	

	public PowerLife(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		paint = EntityPlayer.PAINT_YELLOW;
	}

	@Override
	public String getName() {
		return "life";
	}
	
	@Override
	public void update(EntityPlayer player) {
		player.game.playerLives += 1 + upgradeLevel;
		player.setPower(null);
	}

}

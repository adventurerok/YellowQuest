package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.trait.TraitConveyor;

public class PowerTeleport extends Power {
	
	
	
	int cooldown = 0;
	int cooling = 0;

	public PowerTeleport(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		cooldown = 45 * (100 - (upgradeLevel * 40));
		paint = TraitConveyor.PAINT_GREY;
	}

	@Override
	public String getName() {
		return "teleport";
	}

	
	@Override
	public float cooldownPercent() {
		return (1 - ((float)cooling/(float)cooldown)) * 100f;
	}
	
	@Override
	public boolean showPowerButton() {
		return true;
	}
	
	@Override
	public void update(EntityPlayer player) {
		if(cooling > 0) --cooling;
	}
	
	@Override
	public void powerButtonPressed() {
		if(cooling != 0) return;
		int boxNum = player.game.playerBox + 1;
		EntityPlatform box = player.game.getPlatform(boxNum);
		if(box == null) return;
		//player.calc
		int tx = (int) box.x;
		int ty = (int) (box.box.ey + 32);
		player.calcBounds(tx, ty, player.width, player.height);
		cooling = cooldown;
		//cooling = 45;
	}
	
	
}


package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerDoubleJump extends Power {
	
	public boolean usedJump = false;

	public PowerDoubleJump(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		paint = EntityPlatform.PAINT_BLUE;
	}
	
	@Override
	public void update(EntityPlayer player) {
		if(player.onGround) usedJump = false;
	}

	@Override
	public String getName() {
		return "doublejump";
	}
	
	@Override
	public boolean showPowerButton() {
		return upgradeLevel == 0;
	}
	
	@Override
	public void powerButtonPressed() {
		if(player.onGround)  return;
		if(!usedJump) player.y_velocity = YellowQuest.DEFAULT_JUMP + 0.25;
		usedJump = true;
	}
	
	public boolean doSecondJump(){
		return upgradeLevel > 0 && !usedJump;
	}

}

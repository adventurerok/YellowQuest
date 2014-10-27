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
		if(player.onGround || usedJump)  return;
		player.y_velocity = YellowQuest.DEFAULT_JUMP + 0.25;
		usedJump = true;
		
		int dx = (int) (player.x - player.game.doubleX);
		int dy = (int) (player.y - player.game.doubleY);
		int dist = (dx * dx) + (dy * dy);
		
		if(dist < 20000){
			player.fallDist = 0;
			player.game.generateBonusBoxes(player.game.doubleX - 40, player.game.doubleY - 500);
		}
	}
	
	public boolean doSecondJump(){
		return upgradeLevel > 0 && !usedJump;
	}

}

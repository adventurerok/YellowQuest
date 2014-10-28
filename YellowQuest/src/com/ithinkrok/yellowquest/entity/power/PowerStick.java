package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.challenge.Stat;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerStick extends Power {
	
	public static final Paint PAINT_STICK = new Paint();
	
	public double syv;
	
	public boolean sticking = false;
	public boolean xcollision = false;
	
	static {
		PAINT_STICK.setColor(0xff008000);
	}
	

	public PowerStick(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		paint = PAINT_STICK;
	}
	
	@Override
	public void update(EntityPlayer player) {
		player.gravity = player.intersecting == null;
		if(player.gravity){
			if(sticking && xcollision){
				xcollision = false;
				player.y_velocity = syv;
			}
			sticking = false;
			return;
		}
		sticking = true;
		if(player.collisionHorizontal){
			if(player.timeOnPlatform == 0){
				player.game.gameData.statTracker.addStat(player.game.getContext(), Stat.POWER_SAVE, 1);
			}
			syv = player.y_velocity;
			player.y_velocity = 0;
		}
		
		if(player.box.ex == player.intersecting.box.sx || player.box.sx == player.intersecting.box.ex){
			player.y_velocity = 0;
			xcollision = true;
		} else xcollision = false;
		if(player.box.ey == player.intersecting.box.sy){
			if(player.collisionVertical){
				if(player.timeOnPlatform == 0) player.game.gameData.statTracker.addStat(player.game.getContext(), Stat.POWER_SAVE, 1);
				player.x_velocity = 0;
			}
			if(player.game.doingJump && !player.game.wasDoingJump){
				player.move(0, -1);
				player.gravity = true;
			}
		}
	}
	

	@Override
	public String getName() {
		return "stick";
	}

}

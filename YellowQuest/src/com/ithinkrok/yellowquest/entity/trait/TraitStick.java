package com.ithinkrok.yellowquest.entity.trait;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.power.PowerStick;
import com.ithinkrok.yellowquest.ui.PowerInfo;

public class TraitStick extends Trait {

	public TraitStick(EntityPlatform parent) {
		super(parent);
		color = PowerStick.PAINT_STICK;
	}
	
	public double syv;
	
	public boolean xcollision = false;
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		player.gravity = false;
		
		if(parent.timeOnPlatform == 0){
			PowerInfo.getData("stick").unlock(player.game.getContext());
		}
		
		if(player.collisionHorizontal){
			syv = player.y_velocity;
			player.y_velocity = 0;
		}
		
		if(player.box.ex == player.intersecting.box.sx || player.box.sx == player.intersecting.box.ex){
			player.y_velocity = 0;
			xcollision = true;
		} else xcollision = false;
		if(player.box.ey == player.intersecting.box.sy){
			if(player.collisionVertical) player.x_velocity = 0;
			if(player.game.doingJump && !player.game.wasDoingJump){
				player.move(0, -1);
				player.gravity = true;
			}
		}
	}
	
	@Override
	public void noPlayer(EntityPlayer player) {
		if(parent.timeSinceIntercept == 0) return;
		player.gravity = true;
		if(xcollision){
			player.y_velocity = syv;
			xcollision = false;
		}
	}

	@Override
	public String getName() {
		return "stick";
	}

	@Override
	public int getIndex() {
		return 75;
	}

}

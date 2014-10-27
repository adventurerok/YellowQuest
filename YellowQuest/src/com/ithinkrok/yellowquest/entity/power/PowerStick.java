package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;
import android.util.Log;

import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerStick extends Power {
	
	public static final Paint PAINT_STICK = new Paint();
	
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
		if(player.gravity) return;
		if(player.collisionHorizontal) player.y_velocity = 0;
		
		if(player.box.ex == player.intersecting.box.sx || player.box.sx == player.intersecting.box.ex){
			player.y_velocity = 0;
		}
	}
	

	@Override
	public String getName() {
		return "stick";
	}

}

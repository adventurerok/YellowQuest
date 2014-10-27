package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;

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
	public String getName() {
		return "stick";
	}

}

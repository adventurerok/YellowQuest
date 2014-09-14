package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.trait.TraitBounce;

public class PowerJump extends Power {
	
	

	public PowerJump(EntityPlayer player) {
		super(player);
		jumpIncrease = 2;
		paint = TraitBounce.PAINT_MAGENTA;
	}

}

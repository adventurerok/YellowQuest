package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.trait.TraitTroll;

public class PowerTroll extends Power {

	public PowerTroll(EntityPlayer player) {
		super(player);
		paint = TraitTroll.PAINT_TROLL;
	}

	@Override
	public String getName() {
		return "troll";
	}

}

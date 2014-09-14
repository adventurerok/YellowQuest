package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.trait.TraitBoost;

public class PowerSlip extends Power {

	public PowerSlip(EntityPlayer player) {
		super(player);
		alternateSlip = 0.96f;
		paint = TraitBoost.PAINT_FABDAD;
	}

	@Override
	public String getName() {
		return "slip";
	}

}

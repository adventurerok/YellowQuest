package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.trait.TraitTroll;

public class PowerTroll extends Power {
	
	private boolean enabled = true;

	public PowerTroll(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		paint = TraitTroll.PAINT_TROLL;
	}

	@Override
	public String getName() {
		return "troll";
	}
	
	@Override
	public boolean showPowerButton() {
		return upgradeLevel > 0;
	}
	
	@Override
	public void powerButtonPressed() {
		enabled = !enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

}

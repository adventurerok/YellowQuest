package com.ithinkrok.yellowquest.entity.trait;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitExtraLife extends Trait {

	public TraitExtraLife(EntityPlatform parent) {
		super(parent);
		color = EntityPlayer.PAINT_YELLOW;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		player.game.playerLives += 1;
		this.remove = true;
	}

	@Override
	public String getName() {
		return "life";
	}

	@Override
	public int getIndex() {
		return 0;
	}

}

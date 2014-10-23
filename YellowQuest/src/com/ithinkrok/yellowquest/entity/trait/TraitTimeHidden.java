package com.ithinkrok.yellowquest.entity.trait;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.power.PowerTimeStop;

public class TraitTimeHidden extends Trait {

	public TraitTimeHidden(EntityPlatform parent) {
		super(parent);
		color = YellowQuest.PAINT_GAMEOVER;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		((PowerTimeStop)player.getPower()).backwardsTimer();
	}
	
	@Override
	public void aiUpdate() {
		parent.collidable = parent.isVisible = parent.game.isTimeStopped;
	}
	
	
	@Override
	public void install() {
		parent.collidable = parent.isVisible = parent.game.isTimeStopped;
	}

	@Override
	public String getName() {
		return "timehide";
	}

	@Override
	public int getIndex() {
		return 99;
	}

}

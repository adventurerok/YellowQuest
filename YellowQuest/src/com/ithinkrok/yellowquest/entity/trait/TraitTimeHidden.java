package com.ithinkrok.yellowquest.entity.trait;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.EntityPlatform;

public class TraitTimeHidden extends Trait {

	public TraitTimeHidden(EntityPlatform parent) {
		super(parent);
		color = YellowQuest.PAINT_GAMEOVER;
	}
	
	@Override
	public void aiUpdate() {
		parent.collidable = parent.isVisible = !parent.game.isTimeStopped;
	}
	
	
	@Override
	public void install() {
		parent.collidable = parent.isVisible = !parent.game.isTimeStopped;
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

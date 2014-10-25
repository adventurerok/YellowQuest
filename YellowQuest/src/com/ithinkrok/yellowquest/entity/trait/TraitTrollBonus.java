package com.ithinkrok.yellowquest.entity.trait;

import com.ithinkrok.yellowquest.Arrow;
import com.ithinkrok.yellowquest.Arrow.Direction;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitTrollBonus extends TraitTroll {
	
	boolean sequence[];

	public TraitTrollBonus(EntityPlatform parent) {
		super(parent);
		
		
		sequence = new boolean [3];
		for(int d = 0; d < sequence.length; ++d) sequence[d] = parent.game.random(2) == 1 ? true : false;
		if(sequence[0] == sequence[1] == sequence[2]){
			sequence[parent.game.random(3)] = !sequence[0];
		}
		
		for(int d = 0; d < 3; ++d){
			parent.game.arrows.add(new Arrow(parent.x - 40 + d*40, parent.box.ey + 20, sequence[d] ? Direction.FORWARDS : Direction.BACKWARDS, color));
		}
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		super.intersectsPlayer(player);
	}

}

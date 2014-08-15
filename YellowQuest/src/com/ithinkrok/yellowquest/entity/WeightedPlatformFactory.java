package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

public interface WeightedPlatformFactory {

	public EntityPlatform create(YellowQuest game);
	public int getWeight(YellowQuest game);
	
}

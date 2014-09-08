package com.ithinkrok.yellowquest.entity;

import java.util.ArrayList;

import com.ithinkrok.yellowquest.YellowQuest;

public class WeightedPlatformFactories {
	
	
	private static class WPFBoost implements WeightedPlatformFactory{

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformBoost(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 8 ? 1 : 0;
		}
		
	}
	
	private static class WPFSpeed implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformSpeed(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 7 ? 1 : 0;
		}
		
	}
	
	
	
	private static ArrayList<WeightedPlatformFactory> factories = new ArrayList<WeightedPlatformFactory>();
	
	static{
		factories.add(new WPFSpeed());
		factories.add(new WPFBoost());
	}
	
	public static EntityPlatform randomPlatform(YellowQuest game){
		if(game.nextBox == 0) return factories.get(0).create(game);
		if(game.nextBox == game.level.size - 1) return factories.get(0).create(game);
		int weight = 0;
		int d = 0;
		for(d = 0; d < factories.size(); ++d){
			weight += factories.get(d).getWeight(game);
		}
		weight = game.random(weight);
		for(d = 0; d < factories.size(); ++d) {
			weight -= factories.get(d).getWeight(game);
			if(weight < 0) return factories.get(d).create(game);
		}
		return factories.get(0).create(game);
	}
}

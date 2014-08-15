package com.ithinkrok.yellowquest.entity;

import java.util.ArrayList;

import com.ithinkrok.yellowquest.YellowQuest;

public class WeightedPlatformFactories {

	private static class WPFUp implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformUp(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 0 ? 1 : 0;
		}
		
	}
	
	private static class WPFDown implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformDown(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 2 ? 1 : 0;
		}
		
	}
	
	private static class WPFMoving implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformMoving(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			if(game.level.number < 10 && game.level.lastBoxType instanceof EntityPlatformDown) return 0;
			//return 10000;
			return game.level.number > 1 ? 1 : 0;
		}
		
	}
	
	private static class WPFBounce implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformBounce(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 4 ? 1 : 0;
		}
		
	}
	
	private static class WPFBoost implements WeightedPlatformFactory{

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformBounce(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 8 ? 1 : 0;
		}
		
	}
	
	private static class WPFConveyor implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformConveyor(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 6 ? 1 : 0;
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
	
	private static class WPFJumpHide implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformJumpHide(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 7 ? 1 : 0;
		}
		
	}
	
	private static class WPFTroll implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatformTroll(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return game.level.number > 9 ? 1 : 0;
		}
		
	}
	
	private static class WPFDefault implements WeightedPlatformFactory {

		@Override
		public EntityPlatform create(YellowQuest game) {
			return new EntityPlatform(game);
		}

		@Override
		public int getWeight(YellowQuest game) {
			return 2;
		}
		
	}
	
	private static ArrayList<WeightedPlatformFactory> factories = new ArrayList<WeightedPlatformFactory>();
	
	static{
		factories.add(new WPFDefault());
		factories.add(new WPFUp());
		factories.add(new WPFDown());
		factories.add(new WPFConveyor());
		factories.add(new WPFMoving());
		factories.add(new WPFBounce());
		factories.add(new WPFJumpHide());
		factories.add(new WPFTroll());
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
			if(weight <= 0) return factories.get(d).create(game);
		}
		return factories.get(0).create(game);
	}
}

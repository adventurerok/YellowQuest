package com.ithinkrok.yellowquest.entity.trait;

import java.util.ArrayList;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.EntityPlatform;

public class WeightedTraitFactory {
	
	private static interface Weight{
		
		public Trait create(EntityPlatform parent);
		public int getWeight(EntityPlatform parent);
		public String getName();
		
	}
	
	private static class WeightMoving implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitMoving(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			return parent.game.level.number > 1 ? 10 : 0;
		}

		@Override
		public String getName() {
			return "moving";
		}
		
	}
	
	private static class WeightNone implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return null;
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			return 20;
		}

		@Override
		public String getName() {
			return "none";
		}
		
	}
	
	private static class WeightTroll implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitTroll(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			return parent.game.level.number > 8 ? 4 : 0;
		}

		@Override
		public String getName() {
			return "troll";
		}
		
	}
	
	private static class WeightUp implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitUp(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			if(parent.hasTrait("down") || parent.hasTrait("bounce")) return 0;
			return parent.game.level.number > 0 ? 10 : 0;
		}

		@Override
		public String getName() {
			return "up";
		}
		
	}
	
	private static class WeightDown implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitDown(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			if(parent.hasTrait("up")) return 0;
			return parent.game.level.number > 2 ? 10 : 0;
		}

		@Override
		public String getName() {
			return "down";
		}
		
	}
	
	private static class WeightBounce implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitBounce(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			if(parent.hasTrait("up")) return 0;
			return parent.game.level.number > 4 ? 10 : 0;
		}

		@Override
		public String getName() {
			return "bounce";
		}
		
	}
	
	private static class WeightConveyor implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitConveyor(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			if(parent.hasTrait("boost")) return parent.game.level.number > 11 ? 5 : 0;
			return parent.game.level.number > 5 ? 10 : 0;
		}

		@Override
		public String getName() {
			return "conveyor";
		}
		
	}
	
	
	private static class WeightBoost implements Weight {

		@Override
		public Trait create(EntityPlatform parent) {
			return new TraitBoost(parent);
		}

		@Override
		public int getWeight(EntityPlatform parent) {
			if(parent.hasTrait("boost")) return parent.game.level.number > 11 ? 5 : 0;
			return parent.game.level.number > 7 ? 10 : 0;
		}

		@Override
		public String getName() {
			return "boost";
		}
		
	}
	
	private static ArrayList<Weight> weights = new ArrayList<Weight>();
	
	static {
		weights.add(new WeightNone());
		weights.add(new WeightMoving());
		weights.add(new WeightTroll());
		weights.add(new WeightUp());
		weights.add(new WeightDown());
		weights.add(new WeightBounce());
		weights.add(new WeightConveyor());
		weights.add(new WeightBoost());
	}
	
	public static EntityPlatform randomPlatform(YellowQuest game){
		if(game.nextBox == 0) return new EntityPlatform(game);
		if(game.nextBox == game.level.size - 1) return new EntityPlatform(game);
		
		EntityPlatform ent = new EntityPlatform(game);
		Trait first = null;
		
		int weight = 0;
		int d = 0;
		for(d = 0; d < weights.size(); ++d){
			weight += weights.get(d).getWeight(ent);
		}
		weight = game.random(weight);
		for(d = 0; d < weights.size(); ++d) {
			weight -= weights.get(d).getWeight(ent);
			if(weight < 0){
				first = weights.get(d).create(ent);
				break;
			}
		}
		
		if(first == null) return ent;
		ent.traits = new Trait[]{first};
		
		if(game.random(3) != 0) return ent;
		
		Trait second = null;
		weight = 0;
		d = 0;
		for(d = 0; d < weights.size(); ++d){
			if(weights.get(d).getName().equals(first.getName())) continue;
			weight += weights.get(d).getWeight(ent);
		}
		weight = game.random(weight);
		for(d = 0; d < weights.size(); ++d) {
			if(weights.get(d).getName().equals(first.getName())) continue;
			weight -= weights.get(d).getWeight(ent);
			if(weight < 0){
				second = weights.get(d).create(ent);
				break;
			}
		}
		
		if(second == null) return ent;
		if(second.getIndex() < first.getIndex()){
			ent.traits = new Trait[]{second, first};
		} else ent.traits = new Trait[]{first, second};
		
		
		return ent;
	}

}

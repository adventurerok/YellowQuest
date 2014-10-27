package com.ithinkrok.yellowquest.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.ithinkrok.yellowquest.*;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.power.*;
import com.ithinkrok.yellowquest.entity.trait.*;

public class PowerInfo {
	
	private static interface PowerCreator {
		
		public Power createPower(EntityPlayer player, int upgradeLevel);
		
	}

	static int mult[] = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256 };

	//Class<? extends Power> clazz;
	String name;
	int color;
	int buyCost;
	int upgradeCost;
	int displayName;
	int displayInfo;
	int displayUpgradeInfo;
	int maxUpgrade;
	int warnInfo;
	int powerNum;
	int displayUnlock;
	int displayIcon;
	int minBonusLevel;
	int index;
	PowerCreator creator;
	
	int displayBonusComplete;
	public int bonusReward;

	public PowerInfo(String name, int color, int buyCost, int upgradeCost,
			int displayName, int displayInfo, int displayUpgradeInfo, int maxUpgrade, int warnInfo, int powerNum, int displayUnlock, int displayIcon, int minBonusLevel, int displayBonusComplete, int bonusReward) {
		super();
		//this.clazz = clazz;
		this.name = name;
		this.color = color;
		this.buyCost = buyCost;
		this.upgradeCost = upgradeCost;
		this.displayName = displayName;
		this.displayInfo = displayInfo;
		this.displayUpgradeInfo = displayUpgradeInfo;
		this.maxUpgrade = maxUpgrade;
		this.warnInfo = warnInfo;
		this.powerNum = powerNum;
		this.displayUnlock = displayUnlock;
		this.displayIcon = displayIcon;
		this.minBonusLevel = minBonusLevel;
		this.displayBonusComplete = displayBonusComplete;
		this.bonusReward = bonusReward;
		
		this.buyCost = 1;
		this.upgradeCost = 1;

	}
	
	public PowerInfo setCreator(PowerCreator creator){
		this.creator = creator;
		return this;
	}

	public Power newInstance(EntityPlayer player, int upgradeLevel) {
		return creator.createPower(player, upgradeLevel);
	}

	public int upgradeCost(int lvl) {
		return upgradeCost * mult[lvl];
	}
	
	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void unlock(MainActivity context){
		if(context.getGameData().hasPowerUnlock(powerNum)) return;
		context.getGameData().addPowerUnlock(powerNum);
		//context.view.game.toastText(displayUnlock);
		ToastSystem.showUnlockToast(displayIcon, displayUnlock);
		
		unlockNewAchievements(context);
	}
	
	public void showBonusComplete(){
		ToastSystem.showBonusToast(displayBonusComplete, bonusReward);
	}
	
	public static void unlockNewAchievements(MainActivity context){
		int count = 0;
		for(int d = 0; d < getPowerCount(); ++d){
			if(context.getGameData().hasPowerUnlock(d)) ++count;
		}
		
		if(count >= 4) context.view.game.addAchievement(R.string.achievement_the_unlocker);
		if(count >= 8) context.view.game.addAchievement(R.string.achievement_omnipotent);
	}

	private static ArrayList<PowerInfo> data = new ArrayList<PowerInfo>();
	private static HashMap<String, PowerInfo> named = new HashMap<String, PowerInfo>();

	static {
		data.add(new PowerInfo("up", TraitUp.PAINT_GREEN.getColor(), 2000, 10000,
				R.string.power_up, R.string.power_up_desc, R.string.power_up_upgrade, 2,
				R.string.power_up_warn, 0, R.string.power_up_unlock, R.drawable.unlock_up, 3, R.string.bonus_area_up, 500).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerUp(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("bounce", TraitBounce.PAINT_MAGENTA.getColor(), 3500, 18000,
				R.string.power_bounce, R.string.power_bounce_desc, R.string.power_bounce_upgrade, 2,
				R.string.power_bounce_warn, 1, R.string.power_bounce_unlock, R.drawable.unlock_bounce, 5, R.string.bonus_area_bounce, 1000).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerBounce(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("troll", TraitTroll.PAINT_TROLL.getColor(), 5000, 25000,
				R.string.power_troll, R.string.power_troll_desc, R.string.power_troll_upgrade, 1,
				R.string.power_troll_warn, 2, R.string.power_troll_unlock, R.drawable.unlock_backwards, 9, R.string.bonus_area_troll, 2500).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerTroll(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("time", YellowQuest.PAINT_GAMEOVER.getColor(), 7500, 37000,
				R.string.power_time, R.string.power_time_desc, R.string.power_time_upgrade, 2,
				R.string.power_time_warn, 3, R.string.power_time_unlock, R.drawable.unlock_time_stop, 3, R.string.bonus_area_time_stop, 500).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerTimeStop(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("teleport", TraitConveyor.PAINT_GREY.getColor(), 10000, 50000,
				R.string.power_teleport, R.string.power_teleport_desc, R.string.power_teleport_upgrade, 2,
				R.string.power_teleport_warn, 4, R.string.power_teleport_unlock, R.drawable.unlock_teleport, 6, R.string.bonus_area_teleport, 750).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerTeleport(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("life", EntityPlayer.PAINT_YELLOW.getColor(), 15000, 75000,
				R.string.power_life, R.string.power_life_desc, R.string.power_life_upgrade, 2,
				R.string.power_life_warn, 5, R.string.power_life_unlock, R.drawable.unlock_extra_life, 6, R.string.bonus_area_life, 750).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerLife(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("doublejump", EntityPlatform.PAINT_BLUE.getColor(), 12000, 60000,
				R.string.power_doublejump, R.string.power_doublejump_desc, R.string.power_doublejump_upgrade, 1,
				R.string.power_doublejump_warn, 6, R.string.power_doublejump_unlock, R.drawable.unlock_doublejump, 10, R.string.bonus_area_doublejump, 1000).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerDoubleJump(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("stick", PowerStick.PAINT_STICK.getColor(), 20000, 100000,
				R.string.power_stick, R.string.power_stick_desc, R.string.power_stick_upgrade, 0,
				R.string.power_stick_warn, 7, R.string.power_stick_unlock, R.drawable.unlock_stick, 7, R.string.bonus_area_sticky, 750).setCreator(new PowerCreator() {
					
					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerStick(player, upgradeLevel);
					}
				}));

		for (int d = 0; d < data.size(); ++d) {
			PowerInfo info = data.get(d);
			info.index = d;
			named.put(info.name, info);
		}
	}
	
	public static PowerInfo generateBonus(YellowQuest game){
		ArrayList<PowerInfo> canBe = new ArrayList<PowerInfo>();
		for(PowerInfo p : data){
			if(p.minBonusLevel <= game.level.number) canBe.add(p);
		}
		if(canBe.size() == 0) return null;
		return canBe.get(game.random(canBe.size()));
	}

	public static PowerInfo getData(int index) {
		return data.get(index);
	}

	public static int buyCost(int index) {
		return getData(index).buyCost;
	}

	public static int buyCost(String index) {
		return getData(index).buyCost;
	}

	public static int getPowerCount() {
		return data.size();
	}

	public static PowerInfo getData(String name) {
		return named.get(name);
	}

}
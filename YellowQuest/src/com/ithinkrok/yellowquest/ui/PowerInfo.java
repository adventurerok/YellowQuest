package com.ithinkrok.yellowquest.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

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

	// Class<? extends Power> clazz;
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

	public PowerInfo setCosts(int buyCost, int upgradeCost) {
		this.buyCost = buyCost;
		this.upgradeCost = upgradeCost;

		// TODO change
		this.buyCost = 1;
		this.upgradeCost = 1;
		return this;
	}

	public PowerInfo setInfo(int displayName, int displayInfo) {
		this.displayName = displayName;
		this.displayInfo = displayInfo;
		return this;
	}

	public PowerInfo setUpgradeInfo(int displayUpgradeInfo, int maxUpgrade, int warnInfo) {
		this.displayUpgradeInfo = displayUpgradeInfo;
		this.maxUpgrade = maxUpgrade;
		this.warnInfo = warnInfo;
		return this;
	}

	public PowerInfo setPowerNum(int powerNum) {
		this.powerNum = powerNum;
		return this;
	}

	public PowerInfo setBonusInfo(int minBonusLevel, int displayBonusComplete, int bonusReward) {
		this.minBonusLevel = minBonusLevel;
		this.displayBonusComplete = displayBonusComplete;
		this.bonusReward = bonusReward;
		return this;
	}

	public PowerInfo setUnlockInfo(int displayUnlock, int displayIcon) {
		this.displayUnlock = displayUnlock;
		this.displayIcon = displayIcon;
		return this;
	}

	public PowerInfo(String name, int color) {
		super();
		// this.clazz = clazz;
		this.name = name;
		this.color = color;

	}

	public PowerInfo setCreator(PowerCreator creator) {
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

	public void unlock(MainActivity context) {
		if (context.getGameData().hasPowerUnlock(powerNum))
			return;
		context.getGameData().addPowerUnlock(powerNum);
		ToastSystem.showUnlockToast(displayIcon, displayUnlock);

		unlockNewAchievements(context);
	}

	public void showBonusComplete(MainActivity context) {
		context.getGameData().addBonusUnlock(powerNum);
		if (context.getGameData().getBonusUnlocks() == 255) {
			context.getGame().addAchievement(R.string.achievement_bonus_exploiter);
		}
		ToastSystem.showBonusToast(displayBonusComplete, bonusReward);
	}
	
	public String getWithText(Context context){
		return StringFormatter.format(context.getString(R.string.with_power), context.getString(displayName)) + " ";
	}

	public static void unlockNewAchievements(MainActivity context) {
		int count = 0;
		for (int d = 0; d < getPowerCount(); ++d) {
			if (context.getGameData().hasPowerUnlock(d))
				++count;
		}

		if (count >= 4)
			context.getGame().addAchievement(R.string.achievement_the_unlocker);
		if (count >= 8)
			context.getGame().addAchievement(R.string.achievement_omnipotent);
	}

	private static ArrayList<PowerInfo> data = new ArrayList<PowerInfo>();
	private static HashMap<String, PowerInfo> named = new HashMap<String, PowerInfo>();

	static {
		data.add(new PowerInfo("up", TraitUp.PAINT_GREEN.getColor()).setCosts(2000, 10000)
				.setInfo(R.string.power_up, R.string.power_up_desc)
				.setUpgradeInfo(R.string.power_up_upgrade, 2, R.string.power_up_warn).setPowerNum(0)
				.setUnlockInfo(R.string.power_up_unlock, R.drawable.unlock_up)
				.setBonusInfo(3, R.string.bonus_area_up, 500).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerUp(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("bounce", TraitBounce.PAINT_MAGENTA.getColor()).setCosts(3500, 18000)
				.setInfo(R.string.power_bounce, R.string.power_bounce_desc)
				.setUpgradeInfo(R.string.power_bounce_upgrade, 2, R.string.power_bounce_warn).setPowerNum(1)
				.setUnlockInfo(R.string.power_bounce_unlock, R.drawable.unlock_bounce)
				.setBonusInfo(5, R.string.bonus_area_bounce, 1000).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerBounce(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("troll", TraitTroll.PAINT_TROLL.getColor()).setCosts(5000, 25000)
				.setInfo(R.string.power_troll, R.string.power_troll_desc)
				.setUpgradeInfo(R.string.power_troll_upgrade, 1, R.string.power_troll_warn).setPowerNum(2)
				.setUnlockInfo(R.string.power_troll_unlock, R.drawable.unlock_backwards)
				.setBonusInfo(9, R.string.bonus_area_troll, 2500).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerTroll(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("time", YellowQuest.PAINT_GAMEOVER.getColor()).setCosts(7500, 37000)
				.setInfo(R.string.power_time, R.string.power_time_desc)
				.setUpgradeInfo(R.string.power_time_upgrade, 2, R.string.power_time_warn).setPowerNum(3)
				.setUnlockInfo(R.string.power_time_unlock, R.drawable.unlock_time_stop)
				.setBonusInfo(3, R.string.bonus_area_time_stop, 500).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerTimeStop(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("teleport", TraitConveyor.PAINT_GREY.getColor()).setCosts(10000, 50000)
				.setInfo(R.string.power_teleport, R.string.power_teleport_desc)
				.setUpgradeInfo(R.string.power_teleport_upgrade, 2, R.string.power_teleport_warn).setPowerNum(4)
				.setUnlockInfo(R.string.power_teleport_unlock, R.drawable.unlock_teleport)
				.setBonusInfo(6, R.string.bonus_area_teleport, 750).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerTeleport(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("life", EntityPlayer.PAINT_YELLOW.getColor()).setCosts(15000, 75000)
				.setInfo(R.string.power_life, R.string.power_life_desc)
				.setUpgradeInfo(R.string.power_life_upgrade, 2, R.string.power_life_warn).setPowerNum(5)
				.setUnlockInfo(R.string.power_life_unlock, R.drawable.unlock_extra_life)
				.setBonusInfo(6, R.string.bonus_area_life, 750).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerLife(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("doublejump", EntityPlatform.PAINT_BLUE.getColor()).setCosts(12000, 60000)
				.setInfo(R.string.power_doublejump, R.string.power_doublejump_desc)
				.setUpgradeInfo(R.string.power_doublejump_upgrade, 1, R.string.power_doublejump_warn).setPowerNum(6)
				.setUnlockInfo(R.string.power_doublejump_unlock, R.drawable.unlock_doublejump)
				.setBonusInfo(10, R.string.bonus_area_doublejump, 1000).setCreator(new PowerCreator() {

					@Override
					public Power createPower(EntityPlayer player, int upgradeLevel) {
						return new PowerDoubleJump(player, upgradeLevel);
					}
				}));
		data.add(new PowerInfo("stick", PowerStick.PAINT_STICK.getColor()).setCosts(20000, 100000)
				.setInfo(R.string.power_stick, R.string.power_stick_desc)
				.setUpgradeInfo(R.string.power_stick_upgrade, 0, R.string.power_stick_warn).setPowerNum(7)
				.setUnlockInfo(R.string.power_stick_unlock, R.drawable.unlock_stick)
				.setBonusInfo(7, R.string.bonus_area_sticky, 750).setCreator(new PowerCreator() {

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

	public static PowerInfo generateBonus(YellowQuest game) {
		ArrayList<PowerInfo> canBe = new ArrayList<PowerInfo>();
		for (PowerInfo p : data) {
			if (p.minBonusLevel <= game.level.number)
				canBe.add(p);
		}
		if (canBe.size() == 0)
			return null;
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
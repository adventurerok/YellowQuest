package com.ithinkrok.yellowquest.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.ithinkrok.yellowquest.R;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.power.*;
import com.ithinkrok.yellowquest.entity.trait.*;

public class PowerInfo {

	static int mult[] = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256 };

	Class<? extends Power> clazz;
	String name;
	int color;
	int buyCost;
	int upgradeCost;
	int displayName;
	int displayInfo;
	int displayUpgradeInfo;
	int maxUpgrade;
	int warnInfo;

	public PowerInfo(Class<? extends Power> clazz, String name, int color, int buyCost, int upgradeCost,
			int displayName, int displayInfo, int displayUpgradeInfo, int maxUpgrade, int warnInfo) {
		super();
		this.clazz = clazz;
		this.name = name;
		this.color = color;
		this.buyCost = buyCost;
		this.upgradeCost = upgradeCost;
		this.displayName = displayName;
		this.displayInfo = displayInfo;
		this.displayUpgradeInfo = displayUpgradeInfo;
		this.maxUpgrade = maxUpgrade;
		this.warnInfo = warnInfo;

	}

	public Power newInstance(EntityPlayer player, int upgradeLevel) {
		try {
			return clazz.getConstructor(EntityPlayer.class, int.class).newInstance(player, upgradeLevel);
		} catch (InstantiationException e) {
			Log.e("YellowQuest", "Fix your code dammit");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e("YellowQuest", "Fix your code dammit");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Log.e("YellowQuest", "Fix your code dammit");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e("YellowQuest", "Fix your code dammit");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.e("YellowQuest", "Fix your code dammit");
			e.printStackTrace();
		}
		throw new RuntimeException("You need to take a closer look at some of da code!");
	}

	public int upgradeCost(int lvl) {
		return upgradeCost * mult[lvl];
	}

	private static ArrayList<PowerInfo> data = new ArrayList<PowerInfo>();
	private static HashMap<String, PowerInfo> named = new HashMap<String, PowerInfo>();

	static {
		data.add(new PowerInfo(PowerBounce.class, "bounce", TraitBounce.PAINT_MAGENTA.getColor(), 1000, 5000,
				R.string.power_bounce, R.string.power_bounce_desc, R.string.power_bounce_upgrade, 2,
				R.string.power_bounce_warn));
		data.add(new PowerInfo(PowerTroll.class, "troll", TraitTroll.PAINT_TROLL.getColor(), 2000, 10000,
				R.string.power_troll, R.string.power_troll_desc, R.string.power_troll_upgrade, 1,
				R.string.power_troll_warn));
		data.add(new PowerInfo(PowerUp.class, "up", TraitUp.PAINT_GREEN.getColor(), 5000, 25000,
				R.string.power_up, R.string.power_up_desc, R.string.power_up_upgrade, 2,
				R.string.power_up_warn));
		data.add(new PowerInfo(PowerTeleport.class, "teleport", TraitConveyor.PAINT_GREY.getColor(), 10000, 50000,
				R.string.power_teleport, R.string.power_teleport_desc, R.string.power_teleport_upgrade, 4,
				R.string.power_teleport_warn));

		for (PowerInfo info : data) {
			named.put(info.name, info);
		}
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
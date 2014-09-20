package com.ithinkrok.yellowquest.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.ithinkrok.yellowquest.R;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.power.Power;
import com.ithinkrok.yellowquest.entity.power.PowerBounce;
import com.ithinkrok.yellowquest.entity.trait.TraitBounce;
import com.ithinkrok.yellowquest.entity.trait.TraitTroll;

public class PowerInfo {

	Class<? extends Power> clazz;
	String name;
	int color;
	int buyCost;
	int upgradeCost;
	int displayName;
	int displayInfo;
	int displayUpgradeInfo;

	public PowerInfo(Class<? extends Power> clazz, String name, int color, int buyCost, int upgradeCost,
			int displayName, int displayInfo, int displayUpgradeInfo) {
		super();
		this.clazz = clazz;
		this.name = name;
		this.color = color;
		this.buyCost = buyCost;
		this.upgradeCost = upgradeCost;
		this.displayName = displayName;
		this.displayInfo = displayInfo;
		this.displayUpgradeInfo = displayUpgradeInfo;
	}
	
	public Power newInstance(EntityPlayer player, int upgradeLevel){
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
	
	private static ArrayList<PowerInfo> data = new ArrayList<PowerInfo>();
	private static HashMap<String, PowerInfo> named = new HashMap<String, PowerInfo>();
	
	static {
		data.add(new PowerInfo(PowerBounce.class, "bounce", TraitBounce.PAINT_MAGENTA.getColor(), 5000, 50000,
				R.string.power_bounce, R.string.power_bounce_desc, R.string.power_bounce_upgrade));
		data.add(new PowerInfo(PowerBounce.class, "troll", TraitTroll.PAINT_TROLL.getColor(), 10000, 100000,
				R.string.power_troll, R.string.power_troll_desc, R.string.power_troll_upgrade));
	}
	
	public static PowerInfo getData(int index){
		return data.get(index);
	}
	
	public static int buyCost(int index){
		return getData(index).buyCost;
	}
	
	public static int buyCost(String index){
		return getData(index).buyCost;
	}
	
	public static int getPowerCount(){
		return data.size();
	}
	
	public static PowerInfo getData(String name){
		return named.get(name);
	}

}
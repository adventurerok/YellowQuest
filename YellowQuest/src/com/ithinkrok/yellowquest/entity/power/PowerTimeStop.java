package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerTimeStop extends Power {
	
	public static final Paint PAINT_TIME_STOP = new Paint();
	
	static {
		PAINT_TIME_STOP.setColor(0x5500ee00);
	}
	
	int stopTime = 3 * 45;
	int cooldown = 50 * 45;
	int cooling = 0;
	int coolup = 0;

	public PowerTimeStop(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		paint = YellowQuest.PAINT_GAMEOVER;
		stopTime = 45 * (3 + 3 * upgradeLevel);
		cooldown = 45 * (50 - 5 * upgradeLevel);
	}
	
	@Override
	public void update(EntityPlayer player) {
		if(coolup > 0){
			--player.game.timer;
			--coolup;
		} else if(cooling > 0){
			--cooling;
		}
	}
	
	@Override
	public boolean showPowerButton() {
		return true;
	}
	
	@Override
	public void powerButtonPressed() {
		if(coolup > 0 || cooling > 0) return;
		coolup = stopTime;
		cooling = cooldown;
	}
	
	@Override
	public float cooldownPercent() {
		if(coolup > 0)	return (1 - ((float)coolup/(float)cooldown)) * 100f;
		if(cooling > 0) return (1 - ((float)cooling/(float)cooldown)) * 100f;
		return super.cooldownPercent();
	}
	
	@Override
	public Paint getCooldownPaint() {
		if(coolup > 0) return PAINT_TIME_STOP;
		else return null;
	}

	@Override
	public String getName() {
		return "time";
	}

}

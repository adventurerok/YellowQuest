package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class PowerTimeStop extends Power {
	
	public static final Paint PAINT_TIME_STOP = new Paint();
	
	static {
		PAINT_TIME_STOP.setColor(0x5500ee00);
	}
	
	public int stopTime = 3 * 45;
	public int cooldown = 50 * 45;
	public int cooling = 0;
	public int coolup = 0;

	public PowerTimeStop(EntityPlayer player, int upgradeLevel) {
		super(player, upgradeLevel);
		paint = YellowQuest.PAINT_GAMEOVER;
		stopTime = 45 * (3 + 3 * upgradeLevel);
		cooldown = 45 * (50 - 10 * upgradeLevel);
	}
	
	@Override
	public void update(EntityPlayer player) {
		if(coolup > 0){
			player.game.isTimeStopped = true;
			--player.game.timer;
			--coolup;
		} else if(cooling > 0){
			player.game.isTimeStopped = false;
			--cooling;
		}
	}
	
	@Override
	public boolean showPowerButton() {
		return true;
	}
	
	public void setTimers(){
		coolup = stopTime;
		cooling = cooldown;
	}
	
	@Override
	public void powerButtonPressed() {
		if(coolup > 0 || cooling > 0) return;
		coolup = stopTime;
		cooling = cooldown;
	}
	
	@Override
	public float cooldownPercent() {
		if(coolup > 0)	return (1 - ((float)coolup/(float)stopTime)) * 100f;
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

	public void backwardsTimer() {
		if(coolup > 0 && coolup - 1 < stopTime) coolup += 2;
	}

	public void resetTimers() {
		coolup = 0;
	}

}

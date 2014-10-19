package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.ui.PowerInfo;

public class TraitUp extends Trait {
	
	public static final Paint PAINT_GREEN = new Paint();
	
	private static boolean powerUnlock = false;
	
	private int playerUpTime = 0;
	private int maxUpTime = 200;
	
	static {
		PAINT_GREEN.setColor(0xff00ff00);
	}

	public TraitUp(EntityPlatform parent) {
		super(parent);
		color = PAINT_GREEN;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(playerUpTime == 0){
			maxUpTime = (player.hasPower("up") ? 250 : 200);
		}
		++playerUpTime;
		if(playerUpTime < maxUpTime) parent.y_velocity = 5; // 45 ups
		else if(playerUpTime == maxUpTime) parent.y_velocity = 0;
		if(!powerUnlock){
			PowerInfo.getData("up").unlock(player.game.getContext());
			powerUnlock = true;
		}
	}
	
	@Override
	public void noPlayer(EntityPlayer player) {
		parent.y_velocity = 0;
	}

	@Override
	public void install() {
		parent.game.bgenY += 5 + parent.game.random(100);
	}

	@Override
	public String getName() {
		return "up";
	}

	@Override
	public int getIndex() {
		return 0;
	}
	
	@Override
	public double getMaxYPos() {
		return super.getMaxYPos() + 1000;
	}

}

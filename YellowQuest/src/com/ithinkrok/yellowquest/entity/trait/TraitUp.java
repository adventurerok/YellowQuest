package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.entity.power.PowerUp;
import com.ithinkrok.yellowquest.ui.PowerInfo;

public class TraitUp extends Trait {
	
	public static final Paint PAINT_GREEN = new Paint();
	
	private static boolean powerUnlock = false;
	
	public int maxUpTime = 1000;
	
	static {
		PAINT_GREEN.setColor(0xff00ff00);
	}

	public TraitUp(EntityPlatform parent) {
		super(parent);
		color = PAINT_GREEN;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(parent.timeOnPlatform == 0){
			if(player.hasPower("up")){
				maxUpTime = maxUpTime / (5 + ((PowerUp)player.getPower()).up);
			} else maxUpTime = 200;
		}
		if(parent.timeOnPlatform < maxUpTime) parent.y_velocity = 5; // 45 ups
		else if(parent.timeOnPlatform == maxUpTime){
			parent.y_velocity = 0;
			parent.timeOnPlatform += 500;
		}
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

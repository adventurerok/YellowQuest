package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.ui.PowerInfo;

public class TraitBounce extends Trait {
	
	public static final Paint PAINT_MAGENTA = new Paint();
	
	private static boolean powerUnlock = false;
	
	static {
		PAINT_MAGENTA.setColor(0xffff00ff);
	}

	public TraitBounce(EntityPlatform parent) {
		super(parent);
		color = PAINT_MAGENTA;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(!player.onGround) return;
		if(player.hasPower("bounce")) return;
		player.y_velocity = 13 * player.getJumpMultiplier() + player.getJumpIncrease();
		player.onGround = false;
		if(!powerUnlock){
			PowerInfo.getData("bounce").unlock(player.game.getContext());
			powerUnlock = true;
		}
	}

	@Override
	public String getName() {
		return "bounce";
	}

	@Override
	public int getIndex() {
		return 5;
	}
	
	@Override
	public void install() {
		parent.game.bgenY += 2 + parent.game.random(100);
		parent.jump = 13;
	}

}

package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.GameOver;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitDown extends Trait {
	
	private static final Paint PAINT_RED = new Paint();
	
	private int downTicks = 0;
	private boolean goingDown = false;
	
	static {
		PAINT_RED.setColor(0xffff0000);
	}

	public TraitDown(EntityPlatform parent) {
		super(parent);
		color = PAINT_RED;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		parent.y_velocity = -5;
		goingDown = true;
		if(downTicks > 197){
			if(player.game.playerLives > 1){
				player.game.gameOver = new GameOver(2, "Level failed");
			} else {
				player.game.gameOver = new GameOver(0, "You went down too far");
			}
		}
	}
	
	@Override
	public void aiUpdate() {
		if(goingDown) ++downTicks;
		if(downTicks > 200){
			parent.remove = true;
		}
	}

	@Override
	public String getName() {
		return "down";
	}

	@Override
	public int getIndex() {
		return 0;
	}
	
	@Override
	public void install() {
		parent.game.bgenY -= 30 + parent.game.random(100);
	}

}

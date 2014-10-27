package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.GameOver;
import com.ithinkrok.yellowquest.R;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitDown extends Trait {
	
	private static final Paint PAINT_RED = new Paint();
	
	private int downTicks = 0;
	private boolean goingDown = false;
	
	public boolean stickBonusMode = false;
	
	static {
		PAINT_RED.setColor(0xffff0000);
	}

	public TraitDown(EntityPlatform parent) {
		super(parent);
		color = PAINT_RED;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(stickBonusMode && player.y < parent.y) return;
		
		parent.y_velocity = -5;
		goingDown = true;
		if(downTicks > 197){
			if(player.game.playerLives > 1){
				player.game.gameOver = new GameOver(2, R.string.level_failed, player.game.getContext());
			} else {
				if(player.game.random(5000) == 2845){
					switch(player.game.random(4)){
					case 0:
						player.game.gameOver = new GameOver(0, R.string.cant_get_up, player.game.getContext());
						break;
					case 1:
						player.game.gameOver = new GameOver(0, R.string.you_were_trapped, player.game.getContext());
						break;
					case 2:
						player.game.gameOver = new GameOver(0, R.string.i_couldnt_help, player.game.getContext());
						break;
					case 3:
						player.game.gameOver = new GameOver(0, R.string.i_feel_so_sorry, player.game.getContext());
						break;
						
					}
				} else {
					player.game.gameOver = new GameOver(0, R.string.down_too_far, player.game.getContext());
				}
			}
		}
	}
	
	@Override
	public void aiUpdate() {
		if(stickBonusMode) return;
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

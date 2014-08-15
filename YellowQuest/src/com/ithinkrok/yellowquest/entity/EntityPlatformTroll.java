package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.GameOver;
import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformTroll extends EntityPlatform {

	private static final Paint PAINT_TROLL = new Paint();
	
	static {
		PAINT_TROLL.setColor(0xff95aeff);
	}
	
	public EntityPlatformTroll(YellowQuest game) {
		super(game);
		this.color = PAINT_TROLL;
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if (player.x_velocity > 0) {
            if (game.playerLives > 1) {
                game.playerLives -= 1;
                game.gameOver = new GameOver(2, "Level Failed");
            } else {
                game.gameOver = new GameOver(0, "Don't go right!");
            }
        }
	}

}

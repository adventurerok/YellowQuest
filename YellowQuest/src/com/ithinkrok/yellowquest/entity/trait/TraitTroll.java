package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.GameOver;
import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class TraitTroll extends Trait {

	public static final Paint PAINT_TROLL = new Paint();

	static {
		PAINT_TROLL.setColor(0xff95aeff);
	}

	public TraitTroll(EntityPlatform parent) {
		super(parent);
		color = PAINT_TROLL;
	}

	@Override
	public String getName() {
		return "troll";
	}

	@Override
	public int getIndex() {
		return 99;
	}

	@Override
	public void intersectsPlayer(EntityPlayer player) {
		boolean pow = player.hasPower("troll");
		if (pow && player.x_velocity >= 0)
			return;
		if (!pow && player.x_velocity <= 0)
			return;
		if (parent.game.playerLives > 1) {
			parent.game.playerLives -= 1;
			parent.game.gameOver = new GameOver(2, "Level Failed");
		} else {
			parent.game.gameOver = new GameOver(0, "Don't go right!");
		}
	}

}

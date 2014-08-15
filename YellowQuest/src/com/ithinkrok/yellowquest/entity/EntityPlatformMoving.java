package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatformMoving extends EntityPlatform {

	private static Paint PAINT_ORANGE = new Paint();
	
	private boolean ymode = false;
	
	static {
		PAINT_ORANGE.setColor(0xffff5721);
	}
	
	public EntityPlatformMoving(YellowQuest game) {
		super(game);
		this.color = PAINT_ORANGE;
	}
	
	@Override
	public void aiUpdate() {
		this.aiTimer++;
		if(this.ymode){
			if (this.aiDir == 0) this.y_velocity = 3;
	        else this.y_velocity = -3;
		} else {
	        if (this.aiDir == 0) this.x_velocity = 3;
	        else this.x_velocity = -3;
		}
		
        if (this.aiTimer > this.aiMaxTime) {
            if (this.aiDir == 1) this.aiDir = 0;
            else this.aiDir = 1;
            this.aiTimer = 0;
        }
	}
	
	@Override
	public void install() {
		this.ymode = (game.random(3) == 0);
		this.aiMaxTime = 18 + game.random(75);
		if(this.ymode){
			int sub = game.random(aiMaxTime * 3);
			this.calcBounds(this.x, this.y - sub, this.width, this.height);
			game.bgenY += ((this.aiMaxTime * 3) - sub) - 30;
		} else { 
			game.bgenX += this.aiMaxTime * 3;
		}
	}

}

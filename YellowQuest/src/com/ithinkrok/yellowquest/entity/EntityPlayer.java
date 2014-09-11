package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.BoxMath;
import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlayer extends Entity {

	private static final Paint PAINT_YELLOW = new Paint();
	
	static {
		PAINT_YELLOW.setColor(0xffffff00);
	}
	
	public EntityPlayer(YellowQuest game) {
		super(game, EntityType.PLAYER);
		this.color = PAINT_YELLOW;
	}

	@Override
	public void update() {
		if (Math.abs(this.x_velocity) < 0.01) this.x_velocity = 0;
	    double aSlip = YellowQuest.DEFAULT_SLIP;
	    if (this.intersecting != null) {
	        aSlip = this.intersecting.slip;
	    }
	    this.x_velocity *= aSlip;
	    if (this.y_velocity > 0 && game.doJump()){
	    	this.y_velocity += BoxMath.JUMP_GRAVITY;
	    } else{
	    	this.y_velocity += BoxMath.FALL_GRAVITY;
	    }
	    
	    if (this.y_velocity < BoxMath.FALL_VELOCITY_MAX) this.y_velocity = BoxMath.FALL_VELOCITY_MAX;
	    
	    if (this.intersecting != null && !this.box.intersects(this.intersecting.box)) this.intersecting = null;
	    this.move(this.x_velocity, this.y_velocity);
	    if (this.intersecting != null) {
	        if (this.intersecting.boxNumber > game.playerBox) {
	        	int skipped = this.intersecting.boxNumber - game.playerBox;
	        	if(game.timeMode()) game.timer -= YellowQuest.TIMER_BOX * skipped;
	        	int boxMult = 10;
	        	if(game.timeMode()) boxMult = 20;
	        	else if(game.shadowMode()) boxMult = 15;
	        	game.addScore(skipped * boxMult);
	        	--skipped;
	        	while(skipped > 0){
	        		game.addScore(skipped * 10);
	        		--skipped;
	        	}
	            game.playerBox = this.intersecting.boxNumber;
	            
	        }
	    }
	}
	

}

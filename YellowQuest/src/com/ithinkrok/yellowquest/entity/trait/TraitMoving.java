package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.*;

public class TraitMoving extends Trait {
	
	private static Paint PAINT_ORANGE = new Paint();

	private boolean ymode = false;

	static {
		PAINT_ORANGE.setColor(0xffFF971F);
	}

	public boolean enabled = true;

	private int aiMaxTime;

	private int aiTimer;

	private int aiDir = 0;
	
	private boolean stopOnPlayer = false;
	private boolean startNoPlayer = true;

	public TraitMoving(EntityPlatform parent) {
		super(parent);
		color = PAINT_ORANGE;
	}

	@Override
	public String getName() {
		return "moving";
	}
	
	@Override
	public void intersectsPlayer(EntityPlayer player) {
		if(stopOnPlayer) enabled = false;
	}
	
	@Override
	public void noPlayer(EntityPlayer player) {
		if(startNoPlayer) enabled = true;
	}
	
	
	@Override
	public void aiUpdate() {
		if(!enabled) return;
		this.aiTimer++;
		if(this.ymode){
			if (this.aiDir == 0) parent.y_velocity = 3;
	        else parent.y_velocity = -3;
		} else {
	        if (this.aiDir == 0) parent.x_velocity = 3;
	        else parent.x_velocity = -3;
		}
		
        if (this.aiTimer > this.aiMaxTime) {
            if (this.aiDir == 1) this.aiDir = 0;
            else this.aiDir = 1;
            this.aiTimer = 0;
        }
	}

	@Override
	public void install() {
		this.ymode = (parent.game.random(3) == 0);
		this.aiMaxTime = 18 + parent.game.random(75);
		if (this.ymode) {
			int sub = parent.game.random(aiMaxTime * 3);
			if(parent.hasTrait("down") || parent.hasTrait("jumphide")){
				sub = aiMaxTime * 3;
			}
			parent.calcBounds(parent.x, parent.y - sub, parent.width, parent.height);
			parent.game.bgenY += ((this.aiMaxTime * 3) - sub) - 30;
			if(parent.hasTrait("up")){
				stopOnPlayer = true;
			} else if(parent.hasTrait("down")){
				stopOnPlayer = true;
				startNoPlayer = false;
			}
		} else {
			parent.game.bgenX += this.aiMaxTime * 3;
		}
		
	}

	@Override
	public int getIndex() {
		return 100;
	}
	
	@Override
	public double getMaxYPos() {
		if(aiDir == 0){
			return super.getMaxYPos() + (aiMaxTime - aiTimer) * 3;
		} else {
			return super.getMaxYPos() + aiTimer * 3;
		}
	}

}

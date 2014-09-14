package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class Power {

	protected EntityPlayer player;
	
	protected float jumpMultiplier = 1.0f;
	protected float accelMultiplier = 1.0f;
	protected float jumpIncrease = 0.0f; //increase applied after multiplication
	protected float accelIncrease = 0.0f;
	protected Paint paint;
	
	
	public Power(EntityPlayer player) {
		super();
		this.player = player;
	}
	
	public float getJumpIncrease() {
		return jumpIncrease;
	}
	
	public float getJumpMultiplier() {
		return jumpMultiplier;
	}
	
	public float getAccelIncrease() {
		return accelIncrease;
	}
	
	public float getAccelMultiplier() {
		return accelMultiplier;
	}
	
	public Paint getPaint() {
		return paint;
	}
	
	public void update(EntityPlayer player){
		
	}
	
	
}

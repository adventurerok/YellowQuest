package com.ithinkrok.yellowquest.entity.power;

import android.graphics.Paint;
import android.view.View;
import android.widget.*;

import com.ithinkrok.yellowquest.MainActivity;
import com.ithinkrok.yellowquest.R;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public abstract class Power {

	protected EntityPlayer player;

	protected float jumpMultiplier = 1.0f;
	protected float accelMultiplier = 1.0f;
	protected float jumpIncrease = 0.0f; // increase applied after
											// multiplication
	protected float accelIncrease = 0.0f;
	protected float alternateSlip = 0;
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

	public float getAlternateSlip() {
		return alternateSlip;
	}

	public void update(EntityPlayer player) {

	}

	public abstract String getName();

}

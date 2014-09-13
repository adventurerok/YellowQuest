package com.ithinkrok.yellowquest.entity.power;

import com.ithinkrok.yellowquest.entity.EntityPlayer;

public class Power {

	protected EntityPlayer player;
	
	public float jumpMultiplier = 1.0f;
	public float speedMultiplier = 1.0f;
	
	
	public Power(EntityPlayer player) {
		super();
		this.player = player;
	}
	
	
	
}

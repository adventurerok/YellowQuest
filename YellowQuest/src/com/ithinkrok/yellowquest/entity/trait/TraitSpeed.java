package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;

public class TraitSpeed extends Trait {

	private static final Paint PAINT_SPEED = new Paint();

	static {
		PAINT_SPEED.setColor(0xffeaaa6a);
	}

	public TraitSpeed(EntityPlatform parent) {
		super(parent);
		color = PAINT_SPEED;
	}

	@Override
	public String getName() {
		return "speed";
	}

	@Override
	public int getIndex() {
		return 80;
	}

	@Override
	public void install() {
		parent.accel = 35;
	}

}

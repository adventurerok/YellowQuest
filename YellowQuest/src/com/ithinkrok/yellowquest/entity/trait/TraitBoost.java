package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;

public class TraitBoost extends Trait {
	
	private static final Paint PAINT_FABDAD = new Paint();
	
	static {
		PAINT_FABDAD.setColor(0xfffabdad);
	}

	public TraitBoost(EntityPlatform parent) {
		super(parent);
		color = PAINT_FABDAD;
	}
	
	@Override
	public void install() {
		parent.slip = 1.2;
	}

	@Override
	public String getName() {
		return "boost";
	}

	@Override
	public int getIndex() {
		return 80;
	}

}

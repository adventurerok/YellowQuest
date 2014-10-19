package com.ithinkrok.yellowquest;

import android.graphics.Paint;

public class Arrow {

	public static enum Direction {
		UP,
		DOWN,
		BACKWARDS,
		FORWARDS
	}
	
	float x;
	float y;
	Direction dir;
	Paint paint;
	
	public Arrow(double x, double y, Direction dir, Paint paint) {
		super();
		this.x = (float) x;
		this.y = (float) y;
		this.dir = dir;
		this.paint = paint;
	}
	
	
	
}

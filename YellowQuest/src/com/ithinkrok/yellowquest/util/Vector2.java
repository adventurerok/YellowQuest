package com.ithinkrok.yellowquest.util;

public class Vector2 {

	public final double x, y;

	public Vector2(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Vector2 add(double x, double y){
		return new Vector2(this.x + x, this.y + y);
	}
	
	public Vector2 add(Vector2 other){
		return new Vector2(x + other.x, y + other.y);
	}
	
	
}

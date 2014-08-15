package com.ithinkrok.yellowquest.util;

public class Vector2 {

	public double x, y;

	public Vector2(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Vector2 add(double x, double y){
		return new Vector2(this.x + x, this.y + y);
	}
	
	public Vector2 add(double x, double y, Vector2 out){
		out.x = this.x + x;
		out.y = this.y + y;
		return out;
	}
	
	public void set(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2 add(Vector2 other){
		return new Vector2(x + other.x, y + other.y);
	}
	
	public Vector2 add(Vector2 other, Vector2 out){
		out.x = x + other.x;
		out.y = y + other.y;
		return out;
	}
	
	
}

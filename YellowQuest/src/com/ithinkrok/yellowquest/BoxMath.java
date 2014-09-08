package com.ithinkrok.yellowquest;

public class BoxMath {
	
	public static double JUMP_GRAVITY = -0.4;
	public static double FALL_GRAVITY = -0.8;
	public static double FALL_VELOCITY_MAX = -21;

	public static double maxJumpHeight(double jumpVelocity){
		double time = -jumpVelocity/JUMP_GRAVITY;
		return (jumpVelocity * time) + ((JUMP_GRAVITY * time * time) / 2d);
	}
	
	//The highest a box can be placed relative to the last box if it is (distance) away from the last box
	//Assumes constant x velocity
	public static double maxBoxHeight(double jumpVelocity, double xvelocity, double distance){
		double jumpTime = -jumpVelocity/JUMP_GRAVITY; //t = [v(0)-u(jumpVelocity)]/a(gravity)
		double max = (jumpVelocity * jumpTime) + ((JUMP_GRAVITY * jumpTime * jumpTime) / 2d);
		double totalTime = distance / xvelocity;
		if(totalTime <= jumpTime) return max;
		double fallTime = totalTime - jumpTime;
		double fallVelocity = FALL_GRAVITY * fallTime;
		double yfall;
		if(fallVelocity <= FALL_VELOCITY_MAX){
			yfall = (FALL_GRAVITY * fallTime * fallTime) /2d;
		} else {
			double aTime = FALL_VELOCITY_MAX / FALL_GRAVITY;
			yfall = ((FALL_GRAVITY * aTime * aTime) /2d) + ((fallTime - aTime) * FALL_GRAVITY);
		}
		return max + yfall;
		
	}
	
	public static double maxSpeed(double accel, double slip){
		return accel / (1 - slip);
	}
	
	public static double distanceTravelled(double initial, double slip, double accel, double time){
		double max = accel / (1 - slip);
		if(initial == max) return max * time;
		double pow = Math.pow(slip, time);
		return (initial + (accel * (((1-pow)/(1-slip)) - 1))) / pow;
	}
}

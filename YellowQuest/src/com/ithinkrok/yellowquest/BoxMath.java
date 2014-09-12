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
		return (accel / (1 - slip)) * slip;
	}
	
	public static double velocity(double initial, double slip, double accel, int time){
		for(int d = 0; d < time; ++d){
			initial += accel;
			initial *= slip;
		}
		return initial;
	}
	
	public static double testDistance(double initial, double slip, double accel, int time){
		double dist = 0;
		for(int d = 0; d < time; ++d){
			initial += accel;
			initial *= slip;
			dist += initial;
		}
		return dist;
	}
	
	public static double distance(double initial, double slip, double accel, int time){
		double pow = Math.pow(slip, time);
		double first = initial * (pow - 1);
		double second = (accel * (pow - 1)) / (slip - 1);
		return (first + second - (time * accel)) / (slip - 1);
	}
	
	public static double distanceTravelled(double initial, double slip, double accel, int time){
		double max = accel / (1 - slip);
		if(initial == max) return max * time;
		double pow = Math.pow(slip, time);
		//double spec = ((1-(pow))/(1-slip));
		//return (initial + (accel * spec)) / pow;
		double part1 = initial * pow;
		//double part2 = ((accel*slip)*(1-pow)) / (1-(slip));
		double part2 = ((accel*slip)*(pow - 1)) / (slip-1);
		return part1 + part2;
		//return (initial + (accel * powerSequence(slip, time))) / Math.pow(slip, time);
	}
}

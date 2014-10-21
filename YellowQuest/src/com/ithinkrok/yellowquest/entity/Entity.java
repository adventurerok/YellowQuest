package com.ithinkrok.yellowquest.entity;

import java.util.ArrayList;

import com.ithinkrok.yellowquest.CanvasSurfaceView;
import com.ithinkrok.yellowquest.YellowQuest;
import com.ithinkrok.yellowquest.util.Box;

import android.graphics.Paint;

public abstract class Entity {

	private static Entity _ting;
	private static Entity _closest;
	
	private static ArrayList<Entity> test = new ArrayList<Entity>(4);
	private static Box exp = new Box(0, 0, 1, 1);

	public static enum EntityType {
		PLAYER, PLATFORM, OBJECT
	}

	public int timeOnPlatform = 0;

	public String bonusType = null;
	public int bonusData = 0;
	
	public int timeSinceIntercept = 0;

	public YellowQuest game;
	public EntityType type;
	public int entityPos;
	public int boxNumber;
	public Paint color;
	public boolean collidable = true;
	public double slip = YellowQuest.DEFAULT_SLIP;
	public double accel = YellowQuest.DEFAULT_ACCEL;
	public double jump = YellowQuest.DEFAULT_JUMP;
	public Entity intersecting = null;
	public Entity lastIntersected = null;
	public Entity oldIntersected = null;
	public double x = 0, y = 100, width = 32, height = 32;
	public double x_velocity = 0, y_velocity = 0, x_velocity_old = 0, y_velocity_old = 0;
	public boolean onGround = false;
	public double fallDist = 0;
	public int aiTimer = 0;
	public int aiDir = 0;
	public int aiMaxTime = 0;
	public boolean remove = false;
	public Box box = new Box(-16, 84, 16, 116);
	
	private boolean isPlayer = false;

	public Entity(YellowQuest game, EntityType type) {
		super();
		this.game = game;
		this.type = type;
		if (this.type == EntityType.PLATFORM)
			this.boxNumber = game.nextBox++;
		else
			this.boxNumber = -1;
		
		if(this.type == EntityType.PLAYER) isPlayer = true;
	}

	public Entity calcBounds(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.box.set(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
		return this;
	}

	public Entity teleport(double x, double y) {
		return calcBounds(x, y, width, height);
	}

	public void move(double xv, double yv) {
		if (xv == 0 && yv == 0) {
			this.x_velocity_old = 0;
			this.y_velocity_old = 0;
			return;
		}
		this.box.include(xv, yv, exp);
		test.clear();
		int d;
		Entity cur;
		for (d = 0; d < game.boxes.size(); ++d) {
			cur = game.boxes.get(d);
			if (cur != this && cur.collidable && exp.intersects(cur.box))
				test.add(cur);
		}
		// Game.renderText(test.length);
		double xc = xv;
		double yc = yv;
		double ys = yv;
		double xs = xv;
		

		if (Math.abs(yv) > 0.001) {
			for (d = 0; d < test.size(); ++d) {
				ys = yv;
				_ting = test.get(d);
				yv = _ting.box.calcYOffset(this.box, yv);
				if (isPlayer && ys != yv){
					_closest = _ting;
				}
			}
			
			if(_closest != null){
				if(_closest != lastIntersected){
					oldIntersected = lastIntersected;
					lastIntersected = _closest;
				}
				intersecting = _closest;
			}
			
			this.y_velocity_old = yv;
			// Game.renderText(yv);
			this.box.move(0, yv, this.box);
			_ting = _closest = null;
		}

		if (Math.abs(xv) > 0.001) {
			for (d = 0; d < test.size(); ++d) {
				xs = xv;
				_ting = test.get(d);
				xv = _ting.box.calcXOffset(this.box, xv);
				if (isPlayer && xs != xv){
					_closest = _ting;
				}
			}
			
			if(_closest != null){
				if(_closest != lastIntersected){
					oldIntersected = lastIntersected;
					lastIntersected = _closest;
				}
				intersecting = _closest;
			}
			
			this.x_velocity_old = xv;
			// Game.renderText(xv);
			this.box.move(xv, 0, this.box);
			_ting = _closest = null;
		}

		if (this.type == EntityType.PLAYER) {
			if (xc != xv)
				this.x_velocity = 0;
			if (yc != yv)
				this.y_velocity = 0;
		}
		this.x = (this.box.sx + this.box.ex) / 2;
		this.y = (this.box.sy + this.box.ey) / 2;
		// this.onGround = (yc < yv && yc < 0.0d);
		this.onGround = (yc < yv) || (this.onGround && yc == 0);
		if (yv < 0)
			this.fallDist -= yv;
		else
			this.fallDist = 0;
	}

	public void reset() {
		collidable = true;
		slip = YellowQuest.DEFAULT_SLIP;
		accel = YellowQuest.DEFAULT_ACCEL;
		intersecting = null;
		lastIntersected = null;
		oldIntersected = null;
		x = 0;
		y = 100;
		width = 32;
		height = 32;
		x_velocity = 0;
		y_velocity = 0;
		x_velocity_old = 0;
		y_velocity_old = 0;
		onGround = false;
		fallDist = 0;
		aiTimer = 0;
		aiDir = 0;
		aiMaxTime = 0;
		remove = false;
		box.set(this.x - this.width / 2, this.y - this.height / 2, this.x + this.width / 2, this.y + this.height / 2);
	}

	public abstract void update();

	public void draw(CanvasSurfaceView rend) {
		float xp = (float) (box.sx - game.player.x + rend.width / 2);
		float yp = (float) (box.sy - game.player.y + rend.height / 2);
		if (xp > rend.width || yp > rend.height)
			return;
		float w = (float) (box.ex - box.sx);
		float h = (float) (box.ey - box.sy);
		if ((xp + w) < 0 || (yp + h) < 0)
			return;
		rend.fillRect(xp, yp, w, h, color);
	}

	public void delete() {

	}

}

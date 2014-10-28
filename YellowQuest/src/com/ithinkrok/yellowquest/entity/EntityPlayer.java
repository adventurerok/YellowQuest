package com.ithinkrok.yellowquest.entity;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.*;
import com.ithinkrok.yellowquest.challenge.Stat;
import com.ithinkrok.yellowquest.entity.power.Power;

public class EntityPlayer extends Entity {
	

	public static final Paint PAINT_YELLOW = new Paint();
	
	public boolean gravity = true;
	
	public EntityPlatform sticking = null;
	
	public boolean sticky = false;

	static {
		PAINT_YELLOW.setColor(0xffffff00);
	}

	protected Power power = null;

	public EntityPlayer(YellowQuest game) {
		super(game, EntityType.PLAYER);
		this.color = PAINT_YELLOW;
	}

	public boolean hasPower(String power) {
		if (this.power == null)
			return false;
		return this.power.getName().equals(power);
	}
	
	public void setPower(Power power) {
		this.power = power;
		sticky = hasPower("stick");
	}
	
	public String getPowerName(){
		if(power == null) return null;
		else return power.getName();
	}

	@Override
	public void update() {
		if (power != null)
			power.update(this);

		if (Math.abs(this.x_velocity) < 0.01)
			this.x_velocity = 0;
		double aSlip = YellowQuest.AIR_SLIP;
		if (this.intersecting != null) {
			aSlip = this.intersecting.slip;
		}
		if (aSlip == YellowQuest.DEFAULT_SLIP && this.power != null) {
			double alt = power.getAlternateSlip();
			if (alt > 0.01)
				aSlip = alt;
		}
		this.x_velocity *= aSlip;
		if(gravity){
			if (this.y_velocity > 0 && game.doingJump) {
				this.y_velocity += BoxMath.JUMP_GRAVITY;
			} else {
				this.y_velocity += BoxMath.FALL_GRAVITY;
			}
	
			if (this.y_velocity < BoxMath.FALL_VELOCITY_MAX)
				this.y_velocity = BoxMath.FALL_VELOCITY_MAX;
		}

		if (this.intersecting != null && !this.box.intersects(this.intersecting.box))
			this.intersecting = null;
		double oldx = x;
		
		//_oldIntersect = this.intersecting;
		
		this.move(this.x_velocity, this.y_velocity);
		
		if(lastIntersected != null && intersecting == null){
			//if(_oldIntersect.box.intersects(this.box)) intersecting = _oldIntersect;
			if(lastIntersected.box.intersects(this.box)) intersecting = lastIntersected;
		}
		//_oldIntersect = null;
		
		if(oldx > x){
			game.gameData.statTracker.addStat(game.getContext(), Stat.LEFT_DISTANCE, (int)(oldx - x));
		}
		if (this.intersecting != null) {
			if (this.intersecting.boxNumber > game.playerBox) {
				if(!game.level.isBonus && ((EntityPlatform)this.intersecting).isBonus){
					game.nextBox = 9999;
					game.level = game.level.bonusLevel;
				}
				int skipped = this.intersecting.boxNumber - game.playerBox;
				if(skipped > 7) skipped = 1;
				if(skipped > 3){
					game.addAchievement(R.string.achievement_jumpman);
				}
				if (game.timeMode())
					game.timer -= YellowQuest.TIMER_BOX * skipped;
				int boxMult = 10;
				if (game.timeMode())
					boxMult = 20;
				else if (game.shadowMode())
					boxMult = 15;
				int scoreAdd = skipped * boxMult;
				--skipped;
				if(skipped > 0) game.gameData.statTracker.addStat(game.getContext(), Stat.JUMP_OVER_BOXES, skipped);
				while (skipped > 0) {
					scoreAdd += skipped * 10;
					--skipped;
				}
				game.addScore(scoreAdd);
				game.playerBox = this.intersecting.boxNumber;

			}
		}
	}

	public float getAccelMultiplier() {
		return power != null ? power.getAccelMultiplier() : 1.0f;
	}

	public float getJumpMultiplier() {
		return power != null ? power.getJumpMultiplier() : 1.0f;
	}

	public float getAccelIncrease() {
		return power != null ? power.getAccelIncrease() : 0.0f;
	}

	public float getJumpIncrease() {
		return power != null ? power.getJumpIncrease(): 0.0f;
	}

	@Override
	public void draw(CanvasSurfaceView rend) {
		if(!isVisible) return;
		Paint paint;
		if (power == null)
			paint = this.color;
		else
			paint = power.getPaint();
		float xp = (float) (box.sx - game.player.x + rend.width / 2);
		float yp = (float) (box.sy - game.player.y + rend.height / 2);
		if (xp > rend.scaledWidth || yp > rend.scaledHeight)
			return;
		float w = (float) (box.ex - box.sx);
		float h = (float) (box.ey - box.sy);
		if ((xp + w) < rend.scaledX || (yp + h) < rend.scaledY)
			return;
		rend.fillRect(xp, yp, w, h, paint);
		if (power == null)
			return;
		int bonusBorder = (int) (Math.min(width, height) / 4d);
		xp = (float) ((box.sx - game.player.x + rend.width / 2) + bonusBorder);
		yp = (float) ((box.sy - game.player.y + rend.height / 2) + bonusBorder);
		w = (float) ((box.ex - box.sx) - (bonusBorder * 2));
		h = (float) ((box.ey - box.sy) - (bonusBorder * 2));
		rend.fillRect(xp, yp, w, h, color);
	}
	
	public Power getPower() {
		return power;
	}

}

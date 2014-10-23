package com.ithinkrok.yellowquest.entity;

import java.util.ArrayList;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.*;
import com.ithinkrok.yellowquest.entity.trait.Trait;

public class EntityPlatform extends Entity {
	
	private static ArrayList<Trait> _visibleTraits = new ArrayList<Trait>();

	private static final Paint PAINT_BLUE = new Paint();
	private static final Paint PAINT_WHITE = new Paint();
	
	public boolean revealed = true;
	
	public boolean isBonus = false;
	
	
	
	
	static {
		PAINT_BLUE.setColor(0xff0000ff);
		PAINT_WHITE.setColor(0xffffffff);
	}
	
	public EntityPlatform(YellowQuest game) {
		super(game, EntityType.PLATFORM);
		this.color = PAINT_BLUE;
		if(game.shadowMode()) revealed = false;
	}
	
	public Trait[] traits = new Trait[0];
	

	@Override
	public void update() {
		if (this.boxNumber + YellowQuest.BOX_BUFFER < game.playerBox) this.remove = true;
	    if (Math.abs(this.x_velocity) < 0.01) this.x_velocity = 0;
	    boolean pis = false;
	    if (game.player.intersecting == this) {
	        pis = true;
	        
	        if(isBonus && timeOnPlatform == 0){
	        	if("up".equals(game.level.bonusType)){
	        		game.addAchievement(R.string.achievement_up_up_and_away);
	        	} else if(("life").equals(game.level.bonusType)){
	        		game.addAchievement(R.string.achievement_sacrifice);
	        	}
	        }
	        
	        //game.player.intersecting = this;
	        revealed = true;
	        this.intersectsPlayer(game.player);
	        ++timeOnPlatform;
	        
	        if(timeSinceIntercept == 0 && bonusData == 1742 && "bounce".equals(bonusType) && game.player.hasPower("bounce")){
	        	if(game.player.oldIntersected != null && game.player.oldIntersected.bonusData == 12){
	        		game.addAchievement(R.string.achievement_bounceback);
	        		game.player.teleport(0, 2100);
	        		game.player.fallDist = 0;
	        	}
	        }
	        
	        ++timeSinceIntercept;
	    } else{
	    	this.noPlayer(game.player);
	    	timeSinceIntercept = 0;
	    }
//	    this.moveUpdate();
	    this.aiUpdate();
	    if (pis) {
	        if (this.y_velocity > 0) {
	        	boolean ground = game.player.onGround;
	            game.player.move(0, this.y_velocity);
	            this.move(0, this.y_velocity);
	            game.player.onGround = ground;
	        } else if (this.y_velocity < 0) {
	            this.move(0, this.y_velocity);
	            boolean ground = game.player.onGround;
	            game.player.move(0, this.y_velocity);
	            game.player.onGround = ground;
	        }
	        game.player.move(this.x_velocity, 0);
	        this.move(this.x_velocity, 0);
	    } else this.move(this.x_velocity, this.y_velocity);
	}
	
	public void intersectsPlayer(EntityPlayer player){
		for(int d = 0; d < traits.length; ++d){
			traits[d].intersectsPlayer(player);
		}
	}
	
	public void noPlayer(EntityPlayer player){
		for(int d = 0; d < traits.length; ++d){
			traits[d].noPlayer(player);
		}
	}
	
	public void aiUpdate(){
		for(int d = 0; d < traits.length; ++d){
			traits[d].aiUpdate();
			if(traits[d].remove){
				Trait[] after = new Trait[traits.length - 1];
				for(int i = 0; i < d; ++i){
					after[i] = traits[i];
				}
				for(int i = d + 1; i < traits.length; ++i){
					after[i - 1] = traits[i];
				}
				traits = after;
			}
		}
	}
	
//	public void setupMove(boolean allow){
//		if(!allow){
//			this.moveType = 0;
//			return;
//		}
//		int typeGen = game.random(4);
//		if(typeGen == 1 || typeGen == 2) this.moveType = typeGen;
//		this.aiMaxTime = 18 + game.random(75);
//		if(this.moveType == 2){
//			int sub = game.random(aiMaxTime * 3);
//			this.calcBounds(this.x, this.y - sub, this.width, this.height);
//			game.bgenY += ((this.aiMaxTime * 3) - sub) - 30;
//		} else if(this.moveType == 1){ 
//			game.bgenX += this.aiMaxTime * 3;
//		}
//	}
	
	
	public void install(){
		for(int d = 0; d < traits.length; ++d){
			traits[d].install();
		}
	}
	
	public boolean hasTrait(String name){
		for(int d = 0; d < traits.length; ++d){
			if(traits[d].getName().equals(name)) return true;
		}
		return false;
	}
	
	public Trait getTrait(String name){
		for(int d = 0; d < traits.length; ++d){
			if(traits[d].getName().equals(name)) return traits[d];
		}
		return null;
	}
	
	public double getMaxYPos(){
		double max = box.ey;
		double t;
		for(int d = 0; d < traits.length; ++d){
			t = traits[d].getMaxYPos();
			if(t > max) max = t;
		}
		return max;
	}
	
	public double getMaxYWithJump(){
		double max = getMaxYPos();
		return max + BoxMath.maxJumpHeight(jump) - 1; // -1 for luck!
	}
	
	
	//private static int BONUS_BORDER = 4;
	
	@Override
	public void draw(CanvasSurfaceView rend) {
		if(!isVisible) return;
		Paint paint;
		if(!revealed) paint = PAINT_WHITE;
		else {
			for(int i = 0; i < traits.length; ++i){
				if(traits[i].isVisible) _visibleTraits.add(traits[i]);
			}
			if(_visibleTraits.size() == 0) paint = this.color;
			else if(_visibleTraits.size() == 1) paint = _visibleTraits.get(0).color;
			else paint = _visibleTraits.get(1).color;
		}
        float xp = (float) (box.sx - game.player.x + rend.width / 2);
        float yp = (float) (box.sy - game.player.y + rend.height / 2);
        if (xp > rend.scaledWidth || yp > rend.scaledHeight){
        	_visibleTraits.clear();
        	return;
        }
        float w = (float) (box.ex - box.sx);
        float h = (float) (box.ey - box.sy);
        if ((xp + w) < rend.scaledX || (yp + h) < rend.scaledY){
        	_visibleTraits.clear();
        	return;
        }
        rend.fillRect(xp, yp, w, h, paint);
        if(traits.length < 2 || !revealed){
        	_visibleTraits.clear();
        	return;
        }
        int bonusBorder = (int) (Math.min(width, height) / 4d);
        xp = (float) ((box.sx - game.player.x + rend.width / 2) + bonusBorder);
        yp = (float) ((box.sy - game.player.y + rend.height / 2) + bonusBorder);
        w = (float) ((box.ex - box.sx) - (bonusBorder * 2));
        h = (float) ((box.ey - box.sy) - (bonusBorder * 2));
        rend.fillRect(xp, yp, w, h, _visibleTraits.get(0).color);
        _visibleTraits.clear();
	}

}

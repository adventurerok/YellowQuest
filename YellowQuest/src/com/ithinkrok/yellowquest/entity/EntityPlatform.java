package com.ithinkrok.yellowquest.entity;

import com.ithinkrok.yellowquest.YellowQuest;

import android.graphics.Paint;

public class EntityPlatform extends Entity {

	private static final Paint PAINT_BLUE = new Paint();
	
	static {
		PAINT_BLUE.setColor(0xff0000ff);
	}
	
	public EntityPlatform(YellowQuest game) {
		super(game, EntityType.PLATFORM);
		this.color = PAINT_BLUE;
	}
	

	@Override
	public void update() {
		if (this.boxNumber + YellowQuest.BOX_BUFFER < game.playerBox) this.remove = true;
	    if (Math.abs(this.x_velocity) < 0.01) this.x_velocity = 0;
	    boolean pis = false;
	    if (game.player.box.intersects(this.box)) {
	        pis = true;
	        game.player.intersecting = this;
	        this.intersectsPlayer(game.player);
	    } else this.noPlayer(game.player);
	    this.aiUpdate();
	    if (pis) {
	        if (this.y_velocity > 0) {
	            game.player.move(0, this.y_velocity);
	            this.move(0, this.y_velocity);
	        } else if (this.y_velocity < 0) {
	            this.move(0, this.y_velocity);
	            game.player.move(0, this.y_velocity);
	        }
	        game.player.move(this.x_velocity, 0);
	        this.move(this.x_velocity, 0);
	    } else this.move(this.x_velocity, this.y_velocity);
	}
	
	public void intersectsPlayer(EntityPlayer player){
		
	}
	
	public void noPlayer(EntityPlayer player){
		
	}
	
	public void aiUpdate(){
		
	}
	
	public void install(){
		
	}

}

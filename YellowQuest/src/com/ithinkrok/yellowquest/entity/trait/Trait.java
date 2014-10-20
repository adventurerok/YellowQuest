package com.ithinkrok.yellowquest.entity.trait;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.EntityPlatform;
import com.ithinkrok.yellowquest.entity.EntityPlayer;

public abstract class Trait {
	
	public EntityPlatform parent;
	public Paint color;
	public boolean isVisible = true;
	public boolean remove = false;
	
	
	public Trait(EntityPlatform parent) {
		super();
		this.parent = parent;
	}
	
	public abstract String getName();
	
	/**
	 * A lower index than another trait means it will go first into the trait array
	 * @return
	 */
	public abstract int getIndex();
	
	public void intersectsPlayer(EntityPlayer player){
		
	}
	
	public void noPlayer(EntityPlayer player){
		
	}
	
	public void aiUpdate(){
		
	}
	
	public void install(){
		
	}
	
	public boolean canDualTrait(){
		return true;
	}
	
	public double getMaxYPos(){
		return parent.box.ey;
	}

}

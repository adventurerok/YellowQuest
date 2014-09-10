package com.ithinkrok.yellowquest;

import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Paint;

import com.ithinkrok.yellowquest.entity.*;
import com.ithinkrok.yellowquest.entity.trait.TraitDown;
import com.ithinkrok.yellowquest.entity.trait.WeightedTraitFactory;
import com.ithinkrok.yellowquest.util.Box;

public class YellowQuest {

	private static final Paint PAINT_RED = new Paint();
	private static final Paint PAINT_BROWN = new Paint();
	
	static {
		PAINT_RED.setColor(0xFFFF0000);
		PAINT_BROWN.setColor(0xFF556644);
	}
	
	
	//public static final double DEFAULT_SLIP = 0.82;  //45 ups versions
	//public static final double DEFAULT_ACCEL = 2;
	//public static final double DEFAULT_JUMP = 10;
	
	public static final double DEFAULT_SLIP = 0.82; //45 ups versions
	public static final double DEFAULT_ACCEL = 2;
	public static final double DEFAULT_JUMP = 10; //Doesn't change
	
	public static final int BOX_BUFFER = 6;

	long randomSeed = Calendar.getInstance().getTimeInMillis();
	
	public GameProgress progress;
	public MainActivity activity;

	public GameOver gameOver = null;
	public Level level = new Level();
	public ArrayList<Entity> boxes = new ArrayList<Entity>();
	public EntityPlayer player;
	public int nextBox = 0;
	public int playerBox = 0;
	public int playerLives = 3;
	public int timer = 0;
	public int bgenX = -32, bgenY = 0;
	public int bgenYMax = 0, bgenYMin = 0;
	private CanvasSurfaceView canvas;
	
	private int lastWidth = 0;
	private int lastHeight = 0;
	
	private Box leftButton;
	private Box rightButton;
	private Box jumpButton;
	
	private boolean shadow;
	private boolean timed;
	
	private Paint white;
	
	public YellowQuest(CanvasSurfaceView canvas) {
		this.canvas = canvas;
		activity = canvas.getActivity();
		progress = canvas.getActivity().getProgress();
		createButtons();
		white = new Paint();
		white.setColor(0x33ffffff);
	}
	
	public void createButtons(){
		if(lastWidth == canvas.width && lastHeight == canvas.height) return;
		double bsm = 2;
		leftButton = new Box(20 * bsm, canvas.height - 120 * bsm, 80 * bsm, canvas.height - 20 * bsm);
		rightButton = new Box(100 * bsm, canvas.height - 120 * bsm, 200 * bsm, canvas.height - 20 * bsm);
		jumpButton = new Box(canvas.width - 120 * bsm, canvas.height - 120 * bsm, canvas.width - 20 * bsm, canvas.height - 20 * bsm);
		lastWidth = canvas.width;
		lastHeight = canvas.height;
	}
	

	public boolean goLeft() {
		return canvas.touchInBox(leftButton);
		//return wasdKeys[1];
	}

	public boolean goRight() {
		return canvas.touchInBox(rightButton);
		//return wasdKeys[3];
	}

	public boolean doJump() {
		return canvas.touchInBox(jumpButton);
		//return wasdKeys[0];
	}

	public int random(int max) {
		randomSeed ^= (randomSeed << 21);
		randomSeed ^= (randomSeed >>> 35);
		randomSeed ^= (randomSeed << 4);
		int res = (int) ((randomSeed >> 24) % max);
		if (res < 0) res += max;
		return res;
	}

	public void update() {
		double acell = DEFAULT_ACCEL;
		if (this.player.intersecting != null) acell = this.player.intersecting.accel;
		int maxSpeed = 250; //45 ups
		if (gameOver == null) {
			// if (this.cheats) {
			// for (var k = 57; k > 47; --k) {
			// if (keys[k]) {
			// var lvl = k - 50;
			// if (lvl == -2) lvl = 8;
			// if (keys[16]) lvl += 10;
			// if (keys[17]) lvl += 10;
			// Level.number = lvl;
			// this.nextLevel();
			// break;
			// }
			// }
			// }
			if (this.playerBox + BOX_BUFFER > this.nextBox) {
				this.generateBoxes(1);
			}
			// if (keys[82]) {
			// thisOver.time = 20;
			// thisOver.message = "this Reset";
			// }
			if (this.timer > 3999) {
				if (this.playerLives > 1) {
					this.playerLives -= 1;
					gameOver = new GameOver(2, "Time's Out");
				} else gameOver = new GameOver(0, "Time's Up");
			}
			if (goLeft()) this.player.x_velocity -= acell;
			if (this.player.x_velocity < -maxSpeed) this.player.x_velocity = -maxSpeed;
			if (goRight()) this.player.x_velocity += acell;
			if (this.player.x_velocity > maxSpeed) this.player.x_velocity = maxSpeed;
			if (doJump() && this.player.onGround){
				this.player.y_velocity = DEFAULT_JUMP;
			}
			if (this.player.fallDist > 1000) {
				if(playerBox == 0 && level.number == 0){
					addAchievement(R.string.achievement_big_failure);
				} else if(playerBox + 2 >= level.size){
					addAchievement(R.string.achievement_almost);
				}
				if (this.playerLives > 1) {
					this.playerLives -= 1;
					gameOver = new GameOver(2, "Level Failed");
				} else gameOver = new GameOver(0, "Game Over");
			}
			Entity cur;
			for (int d = 0; d < this.boxes.size(); ++d) {
				cur = this.boxes.get(d);
				if(cur == player) continue;
				cur.entityPos = d;
				cur.update();
				if (cur.remove) {
					this.boxes.remove(d);
					d -= 1;
				}
			}
			player.update();
			if (this.playerBox + 1 >= level.size) {
				gameOver = new GameOver(1, "Next Level");
				
				if(Math.abs(player.box.sx - level.finalBox.box.ex) < 1){
					addAchievement(R.string.achievement_overshot);
				}
				switch(level.number){
				case 2:
					addAchievement(R.string.achievement_easy);
					break;
				case 5:
					addAchievement(R.string.achievement_medium);
					break;
				case 8:
					addAchievement(R.string.achievement_hard);
					break;
				case 11:
					addAchievement(R.string.achievement_expert);
					break;
				case 14:
					addAchievement(R.string.achievement_impossible);
					break;
				}
			}
		} else {
			--gameOver.time;
			if (gameOver.time == 0) {
				if (gameOver.type == 0) this.reload();
				else if (gameOver.type == 1) this.nextLevel();
				else if (gameOver.type == 2) this.restartLevel();
				gameOver = null;
			}
		}
	}

	public void reload() {
		gameOver = null;
	    boxes.clear();
	    playerLives = 3;
	    level.number = 0;
	    level.size = 8;
	    nextBox = 0;
	    playerBox = 0;
	    bgenX = -32;
	    bgenY = 0;
	    bgenYMax = 0;
	    bgenYMin = 0;
	    player.reset();
	    boxes.add(player);
	    generateBoxes(BOX_BUFFER);
	    timer = 0;
	}
	
	public void load() {
		player = new EntityPlayer(this);
		reload();
	}

	public void nextLevel() {
		boxes.clear();
	    level.lastBoxType = null;
	    level.number += 1;
	    level.size = 8 + (level.number * 4);
	    nextBox = 0;
	    bgenX = -32;
	    bgenY = 0;
	    bgenYMax = 0;
	    bgenYMin = 0;
	    playerBox = 0;
	    player.reset();
	    boxes.add(player);
	    generateBoxes(BOX_BUFFER);
	    timer = 0;  
	}

	public void restartLevel() {
		boxes.clear();
	    level.lastBoxType = null;
	    level.size = 8 + (level.number * 4);
	    nextBox = 0;
	    bgenX = -32;
	    bgenY = 0;
	    bgenYMax = 0;
	    bgenYMin = 0;
	    playerBox = 0;
	    player.reset();
	    boxes.add(player);
	    generateBoxes(BOX_BUFFER);
	    timer = 0;
	}

	public void generateBoxes(int amount) {
		if (nextBox >= level.size) return;
		double xe;
		double ye;
		EntityPlatform ent;
		for (int d = 0; d < amount; ++d) {
			if (nextBox >= level.size) break;
			xe = 64 + random(128);
			ye = 16 + random(32);
			ent = (EntityPlatform) WeightedTraitFactory.randomPlatform(this).calcBounds(bgenX + xe / 2, bgenY - ye / 2, xe, ye);
			ent.install();
			level.lastBoxType = ent;
			boxes.add(ent);
			level.finalBox = ent;
			bgenX += xe;
			bgenX += 10;
			bgenX += random(170);
			bgenY += random(230) - 110;
			if (bgenY > bgenYMax) bgenYMax = bgenY;
			else if (bgenY < bgenYMin) bgenYMin = bgenY;
		}
	}
	
	public void addAchievement(String achievement){
		progress.addAchievement(achievement);
	}
	
	public void addAchievement(int achievement){
		progress.addAchievement(activity.getString(achievement));
	}
	
	private void drawFlag(CanvasSurfaceView rend, float x, float y){
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		rend.fillRect(x - 30, y + 30, 30, 30, PAINT_RED);
		rend.fillRect(x, y, 5, 60, PAINT_BROWN);
	}
	
	public void draw(CanvasSurfaceView rend){
		drawFlag(rend, (float)level.finalBox.x, (float)level.finalBox.box.ey);
		for(int d = 0; d < boxes.size(); ++d){
			boxes.get(d).draw(rend);
		}
		drawBox(leftButton);
		drawBox(rightButton);
		drawBox(jumpButton);
	}
	
	public void drawBox(Box box){
		canvas.canvas.drawRect((float)box.sx, (float)box.sy, (float)(box.ex), (float)(box.ey), white);
	}
}

package com.ithinkrok.yellowquest;

import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

import com.ithinkrok.yellowquest.entity.*;
import com.ithinkrok.yellowquest.entity.power.PowerTroll;
import com.ithinkrok.yellowquest.entity.trait.WeightedTraitFactory;
import com.ithinkrok.yellowquest.ui.PowerInfo;
import com.ithinkrok.yellowquest.util.Box;

public class YellowQuest {

	private static final Paint PAINT_RED = new Paint();
	private static final Paint PAINT_BROWN = new Paint();
	public static final Paint PAINT_STATS = new Paint();
	public static final Paint PAINT_GAMEOVER = new Paint();
	private static final Paint PAINT_BUTTON = new Paint();
	private static final Paint PAINT_COOLDOWN = new Paint();

	static {
		PAINT_RED.setColor(0xFFFF0000);
		PAINT_BROWN.setColor(0xFF556644);
		PAINT_STATS.setColor(0xFFFFFFFF);
		PAINT_GAMEOVER.setColor(0xFFFFFFFF);
		PAINT_BUTTON.setColor(0x55ffffff);
		PAINT_COOLDOWN.setColor(0x55ffcccc);

		Typeface tf = Typeface.create("sans-serif", Typeface.BOLD);
		PAINT_STATS.setTypeface(tf);
		PAINT_GAMEOVER.setTypeface(tf);
		PAINT_STATS.setTextSize(12);
		PAINT_GAMEOVER.setTextSize(30);
	}

	// public static final double DEFAULT_SLIP = 0.82; //45 ups versions
	// public static final double DEFAULT_ACCEL = 2;
	// public static final double DEFAULT_JUMP = 10;

	public static final double DEFAULT_SLIP = 0.82; // 45 ups versions
	public static final double DEFAULT_ACCEL = 2.5;
	public static final double DEFAULT_JUMP = 10; // Doesn't change
	public static final double AIR_SLIP = 0.75;
	public static final double AIR_ACCEL = 2.5;

	public static final int TIMER_MAX = 8999;
	public static final int TIMER_SECOND = 45;
	public static final int TIMER_START = TIMER_SECOND * 4;
	public static final int TIMER_BOX = (int) ((2 / 3d) * TIMER_SECOND) + 1;

	public static int BOX_BUFFER = 6;

	long randomSeed = Calendar.getInstance().getTimeInMillis();

	public GameData gameData;
	public MainActivity activity;

	public GameOver gameOver = null;
	public Level level = new Level();
	public ArrayList<Entity> boxes = new ArrayList<Entity>();
	public EntityPlayer player;
	public int nextBox = 0;
	public int playerBox = 0;
	public int playerLives = 3;
	public int timer = 0;
	public int totalTimer = 0;
	public int bgenX = -32, bgenY = 0;
	public int bgenYMax = 0, bgenYMin = 0;
	private CanvasSurfaceView canvas;

	private int lastWidth = 0;
	private int lastHeight = 0;

	private Box leftButton;
	private Box rightButton;
	private Box jumpButton;

	private Box leftButtonR;
	private Box rightButtonR;

	private Box powerButton;

	private boolean shadow = false;
	private boolean timed = false;

	private int tPos = 0;

	private boolean timerStarted = false;

	private int score;
	private int levelScore;

	private boolean display = false;

	private boolean reverse = false;

	private Box coolBox = new Box(0, 0, 0, 0);

	private boolean wasPowerPressed = false;

	public YellowQuest(CanvasSurfaceView canvas) {
		this.canvas = canvas;
		activity = canvas.getActivity();
		gameData = canvas.getActivity().getGameData();
		createButtons(canvas);
		BOX_BUFFER = (canvas.width / 148) + 1;
		PAINT_STATS.setTextSize(canvas.density * 12);
		PAINT_GAMEOVER.setTextSize(canvas.density * 30);
	}

	public void addAchievement(int achievement) {
		gameData.addAchievement(activity.getString(achievement));
	}
	
	public void addAchievement(int achievement, int score) {
		gameData.addAchievement(activity.getString(achievement), score);
	}
	
	public EntityPlatform getPlatform(int num){
		for(int d = 0; d < boxes.size(); ++d){
			if(!(boxes.get(d) instanceof EntityPlatform)) continue;
			if(boxes.get(d).boxNumber == num) return (EntityPlatform) boxes.get(d);
			
		}
		return null;
	}

	public void addAchievement(String achievement) {
		gameData.addAchievement(achievement);
	}

	public void addScore(int score) {
		this.score += score;
		this.levelScore += score;
	}

	public void createButtons(CanvasSurfaceView canvas) {
		if (lastWidth == canvas.width && lastHeight == canvas.height)
			return;
		double bsm = canvas.density;
		leftButton = new Box(20 * bsm, canvas.height - 120 * bsm, 80 * bsm, canvas.height - 20 * bsm);
		rightButton = new Box(100 * bsm, canvas.height - 120 * bsm, 200 * bsm, canvas.height - 20 * bsm);
		jumpButton = new Box(canvas.width - 120 * bsm, canvas.height - 120 * bsm, canvas.width - 20 * bsm,
				canvas.height - 20 * bsm);

		rightButtonR = new Box(20 * bsm, canvas.height - 120 * bsm, 120 * bsm, canvas.height - 20 * bsm);
		leftButtonR = new Box(140 * bsm, canvas.height - 120 * bsm, 200 * bsm, canvas.height - 20 * bsm);

		// int left = (int) (200 * bsm);
		// int right = (int) (canvas.width - 120 * bsm);
		// int diff = right - left;
		// int start = (int) (left + (diff / 2) - 50 * bsm);
		// powerButton = new Box(start, canvas.height - 120 * bsm, start + 100 *
		// bsm, canvas.height - 20 * bsm);

		powerButton = new Box(canvas.width - 240 * bsm, canvas.height - 120 * bsm, canvas.width - 140 * bsm,
				canvas.height - 20 * bsm);

		lastWidth = canvas.width;
		lastHeight = canvas.height;
	}

	public boolean doJump() {
		return canvas.touchInBox(jumpButton);
		// return wasdKeys[0];
	}

	public void draw(CanvasSurfaceView rend) {
		if (!display)
			return;
		if (gameOver != null && gameOver.time == 0)
			gameOver = null;
		if (gameOver == null) {
			tPos = 0;
			drawFlag(rend, (float) level.finalBox.x, (float) level.finalBox.box.ey);
			for (int d = 0; d < boxes.size(); ++d) {
				boxes.get(d).draw(rend);
			}
			if (reverse) {
				drawBox(leftButtonR);
				drawBox(rightButtonR);
			} else {
				drawBox(leftButton);
				drawBox(rightButton);
			}
			drawBox(jumpButton);

			if (player.getPower() != null && player.getPower().showPowerButton()) {
				float cooldown = 1f;
				cooldown = (player.getPower().cooldownPercent() / 100);
				
				Paint cd = player.getPower().getCooldownPaint();
				if(cd == null) cd = PAINT_COOLDOWN;

				if (cooldown == 1)
					drawBox(powerButton);
				else if (cooldown == 0)
					drawBox(powerButton, cd);
				else {
					double diff = powerButton.ex - powerButton.sx;
					coolBox.set(powerButton.sx, powerButton.sy, powerButton.sx + diff * cooldown, powerButton.ey);
					drawBox(coolBox);
					coolBox.set(powerButton.sx + diff * cooldown, powerButton.sy, powerButton.ex, powerButton.ey);
					drawBox(coolBox, cd);
				}
			}

			statsText(rend, "Level: " + (level.number + 1) + "." + (playerBox + 1));
			statsText(rend, "Score: " + score);
			statsText(rend, "Timer: " + ((TIMER_MAX - timer) / TIMER_SECOND) + " (Total: "
					+ (totalTimer / TIMER_SECOND) + ")");
			statsText(rend, "Lives: " + playerLives);
		} else {
			rend.canvas.drawText(gameOver.message, (canvas.width / 2)
					- (PAINT_GAMEOVER.measureText(gameOver.message) / 2),
					canvas.height / 2 - (canvas.density * 30) / 2, PAINT_GAMEOVER);
		}
	}

	public void drawBox(Box box) {
		drawBox(box, PAINT_BUTTON);
	}

	public void drawBox(Box box, Paint paint) {
		canvas.canvas.drawRect((float) box.sx, (float) box.sy, (float) (box.ex), (float) (box.ey), paint);
	}

	private void drawFlag(CanvasSurfaceView rend, float x, float y) {
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		rend.fillRect(x - 30, y + 30, 30, 30, PAINT_RED);
		rend.fillRect(x, y, 5, 60, PAINT_BROWN);
	}

	public void generateBoxes(int amount) {
		if (nextBox >= level.size)
			return;
		double xe;
		double ye;
		double hs;
		EntityPlatform ent;
		for (int d = 0; d < amount; ++d) {
			if (nextBox >= level.size)
				break;
			xe = 64 + random(128);
			ye = 16 + random(32);
			if(level.lastBoxType != null){
				hs = level.lastBoxType.getMaxYWithJump();
				hs -= bgenY;
				//hs -= ye / 2d;
				
				if(hs < 0){
					bgenY += hs - random(5); //Boxes cannot be out of reach
					Log.i("YellowQuest", "hs: " + hs + ", bn: " + nextBox);
				}
			}
			ent = (EntityPlatform) WeightedTraitFactory.randomPlatform(this).calcBounds(bgenX + xe / 2, bgenY - ye / 2,
					xe, ye);
			
			ent.install();
			level.lastBoxType = ent;
			boxes.add(ent);
			level.finalBox = ent;
			bgenX += xe;
			bgenX += 10;
			bgenX += random(170);
			bgenY += random(230) - 110;
			if (bgenY > bgenYMax)
				bgenYMax = bgenY;
			else if (bgenY < bgenYMin)
				bgenYMin = bgenY;
		}
	}

	public int getScore() {
		return score;
	}

	public boolean goLeft() {
		return reverse ? canvas.touchInBox(leftButtonR) : canvas.touchInBox(leftButton);
		// return wasdKeys[1];
	}

	public boolean goRight() {
		return reverse ? canvas.touchInBox(rightButtonR) : canvas.touchInBox(rightButton);
		// return wasdKeys[3];
	}

	private void updatePowerButton() {
		if (player.getPower() == null || !player.getPower().showPowerButton())
			return;
		boolean pressed = canvas.touchInBox(powerButton);
		if (pressed && !wasPowerPressed){
			timerStarted = true;
			player.getPower().powerButtonPressed();
		}
		wasPowerPressed = pressed;
	}

	private void levelUp() {
		gameOver = new GameOver(1, "Next Level");
		int mult = 10;
		if (shadow) {
			if (timed)
				mult = 25;
			else
				mult = 15;
		} else if (timed)
			mult = 20;
		addScore((level.number + 1) * mult);
		if (timed) {
			addScore(((TIMER_MAX - timer) / TIMER_SECOND) * 75);
		} else {
			addScore(((TIMER_MAX - timer) / TIMER_SECOND));
		}

		gameData.addScore(level.number + 1, levelScore);

		if (Math.abs(player.box.sx - level.finalBox.box.ex) < 1) {
			addAchievement(R.string.achievement_overshot, 5000);
		}
		switch (level.number) {
		case 2:
			addAchievement(R.string.achievement_easy, 1000);
			break;
		case 4:
			PowerInfo.getData("life").unlock(getContext());
			break;
		case 5:
			addAchievement(R.string.achievement_medium, 3000);
			break;
		case 8:
			addAchievement(R.string.achievement_hard, 9000);
			break;
		case 11:
			addAchievement(R.string.achievement_expert, 27000);
			break;
		case 14:
			addAchievement(R.string.achievement_impossible, 81000);
			break;
		}
	}

	public void load() {
		player = new EntityPlayer(this);
		reload();
	}

	public void nextLevel() {
		boxes.clear();
		level.lastBoxType = null;
		level.number += 1;
		level.size = 8 + (level.number * 3);
		nextBox = 0;
		bgenX = -32;
		bgenY = 0;
		bgenYMax = 0;
		bgenYMin = 0;
		playerBox = 0;
		player.reset();
		boxes.add(player);
		generateBoxes(BOX_BUFFER);
		if (timed)
			timer = TIMER_MAX - TIMER_START;
		else
			timer = 0;
		timerStarted = false;
		levelScore = 0;
		canvas.getActivity().setPassedOne();
	}
	
	public MainActivity getContext(){
		return canvas.getActivity();
	}

	public int random(int max) {
		randomSeed ^= (randomSeed << 21);
		randomSeed ^= (randomSeed >>> 35);
		randomSeed ^= (randomSeed << 4);
		int res = (int) ((randomSeed >> 24) % max);
		if (res < 0)
			res += max;
		return res;
	}

	public void reload() {
		gameOver = null;
		boxes.clear();
		level.lastBoxType = null;
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
		if (timed)
			timer = TIMER_MAX - TIMER_START;
		else
			timer = 0;
		totalTimer = 0;
		timerStarted = false;
		score = 0;
		levelScore = 0;
		player.setPower(null);

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
		if (timed)
			timer = TIMER_MAX - TIMER_START;
		else
			timer = 0;
		timerStarted = false;
		levelScore = 0;
	}

	public void setGameMode(boolean shadow, boolean time) {
		this.shadow = shadow;
		this.timed = time;
	}

	public boolean shadowMode() {
		return shadow;
	}

	public void statsText(CanvasSurfaceView rend, String text) {
		rend.canvas.drawText(text, 10 * rend.density, tPos += (20 * rend.density), PAINT_STATS);
	}

	public boolean timeMode() {
		return timed;
	}

	public void gameOver() {
		gameData.addHiScore(score);
		gameData.save(canvas.getActivity().getSettings().edit());
		this.reload();
	}

	public void update() {
		if (gameOver != null && gameOver.time == 0)
			gameOver = null;
		canvas.setReversed(reverse = (player.hasPower("troll") && ((PowerTroll) player.getPower()).isEnabled()));
		if (gameOver == null) {
			if (this.playerBox + BOX_BUFFER > this.nextBox) {
				this.generateBoxes(1);
			}

			updateTimer();
			updateInput();
			updatePowerButton();
			updateFalling();
			updateEntities();

			if (this.playerBox + 1 >= level.size) {
				levelUp();
			}
		} else {
			updateGameOver();
		}
	}

	private void updateEntities() {
		Entity cur;
		for (int d = 0; d < this.boxes.size(); ++d) {
			cur = this.boxes.get(d);
			if (cur == player)
				continue;
			cur.entityPos = d;
			cur.update();
			if (cur.remove) {
				this.boxes.remove(d);
				d -= 1;
			}
		}
		player.update();
	}

	private void updateFalling() {
		if (this.player.fallDist > 1000) {
			if (playerBox == 0 && level.number == 0) {
				addAchievement(R.string.achievement_big_failure, 1);
			} else if (playerBox + 2 >= level.size) {
				addAchievement(R.string.achievement_almost, 99);
			}
			if (this.playerLives > 1) {
				this.playerLives -= 1;
				gameOver = new GameOver(2, "Level Failed");
			} else
				gameOver = new GameOver(0, "Game Over");
		}
	}

	private void updateGameOver() {
		--gameOver.time;
		if (gameOver.time == 0) {
			if (gameOver.type == 0) {
				gameOver();
				setDisplaying(false);
				canvas.getActivity().loadPlayView();
			} else if (gameOver.type == 1)
				this.nextLevel();
			else if (gameOver.type == 2)
				this.restartLevel();
			gameOver = null;
		}
	}

	private void updateInput() {
		double accel = AIR_ACCEL;
		double jump = DEFAULT_JUMP;
		if (this.player.intersecting != null){
			accel = this.player.intersecting.accel;
			jump = this.player.intersecting.jump;
		}
		int maxSpeed = 250; // 45 ups
		if (goLeft()) {
			this.player.x_velocity -= (accel * player.getAccelMultiplier() + player.getAccelIncrease());
			timerStarted = true;
		}
		if (this.player.x_velocity < -maxSpeed)
			this.player.x_velocity = -maxSpeed;
		if (goRight()) {
			this.player.x_velocity += (accel * player.getAccelMultiplier() + player.getAccelIncrease());
			timerStarted = true;
		}
		if (this.player.x_velocity > maxSpeed)
			this.player.x_velocity = maxSpeed;
		if (doJump() && this.player.onGround) {
			this.player.y_velocity = jump * player.getJumpMultiplier() + player.getJumpIncrease();
			player.onGround = false;
			timerStarted = true;
		}
	}

	public void loadData() {
		if(timed){
			PowerInfo.getData("time").unlock(getContext());
		}
		
		String pName = gameData.getNextPower();
		if (pName == null || pName.trim().isEmpty())
			return;
		if (gameData.subtractScorePoints(PowerInfo.buyCost(pName))) {
			player.setPower(PowerInfo.getData(pName).newInstance(player, gameData.getPowerUpgradeLevel(pName)));
		} else
			Toast.makeText(canvas.getContext(), R.string.not_enough, Toast.LENGTH_SHORT).show();
		gameData.setNextPower("");
		canvas.getActivity().saveData();
	}

	public void setDisplaying(boolean display) {
		this.display = display;
	}

	public boolean isDisplaying() {
		return display;
	}

	private void updateTimer() {
		if (this.timerStarted) {
			++this.timer;
			++this.totalTimer;
		}
		if (this.timer > TIMER_MAX) {
			if (this.playerLives > 1) {
				this.playerLives -= 1;
				gameOver = new GameOver(2, "Time's Out");
			} else
				gameOver = new GameOver(0, "Time's Up");
		}
	}
}

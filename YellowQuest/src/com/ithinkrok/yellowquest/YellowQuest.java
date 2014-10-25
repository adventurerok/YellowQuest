package com.ithinkrok.yellowquest;

import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.*;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.ithinkrok.yellowquest.Arrow.Direction;
import com.ithinkrok.yellowquest.challenge.Stat;
import com.ithinkrok.yellowquest.entity.*;
import com.ithinkrok.yellowquest.entity.power.PowerTimeStop;
import com.ithinkrok.yellowquest.entity.power.PowerTroll;
import com.ithinkrok.yellowquest.entity.trait.*;
import com.ithinkrok.yellowquest.ui.PowerInfo;
import com.ithinkrok.yellowquest.util.Box;

public class YellowQuest {
	
	private static final long MS_HIGH = 1000000 * 20;
	private static final long MS = 1000000;

	private static Entity _iterEntity;

	private static final Paint PAINT_RED = new Paint();
	private static final Paint PAINT_BROWN = new Paint();
	private static final Paint PAINT_BROWNER = new Paint();
	public static final Paint PAINT_STATS = new Paint();
	public static final Paint PAINT_GAMEOVER = new Paint();
	private static final Paint PAINT_BUTTON = new Paint();
	private static final Paint PAINT_COOLDOWN = new Paint();

	static {
		PAINT_RED.setColor(0xFFFF0000);
		PAINT_BROWN.setColor(0xFF556644);
		PAINT_BROWNER.setColor(0xFF9E3F00);
		PAINT_STATS.setColor(0xFFFFFFFF);
		PAINT_GAMEOVER.setColor(0xFFFFFFFF);
		PAINT_BUTTON.setColor(0x55ffffff);
		PAINT_COOLDOWN.setColor(0x55ffcccc);

		Typeface tf = Typeface.create("sans-serif", Typeface.BOLD);
		PAINT_STATS.setTypeface(tf);
		PAINT_GAMEOVER.setTypeface(tf);
		PAINT_STATS.setTextSize(12);
		PAINT_GAMEOVER.setTextSize(30);

		PAINT_COOLDOWN.setStyle(Paint.Style.FILL);
	}

	// public static final double DEFAULT_SLIP = 0.82; //45 ups versions
	// public static final double DEFAULT_ACCEL = 2;
	// public static final double DEFAULT_JUMP = 10;

	public static final double DEFAULT_SLIP = 0.82; // 45 ups versions
	public static final double DEFAULT_ACCEL = 3.4;
	public static final double DEFAULT_JUMP = 10; // Doesn't change
	public static final double AIR_SLIP = 0.75;
	public static final double AIR_ACCEL = 3.3;

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
	public ArrayList<Arrow> arrows = new ArrayList<Arrow>();
	public EntityPlayer player;
	public int nextBox = 0;
	public int playerBox = 0;
	public int playerLives = 3;
	public int timer = 0;
	public int totalTimer = 0;
	public int bgenX = -32, bgenY = 0;
	public int bgenYMax = 0, bgenYMin = 0;
	private CanvasSurfaceView canvas;
	

	public int lifeBonusNum = 0;
	
	public boolean isTimeStopped = false;

	public boolean generatingBonus = false;

	private int lastWidth = 0;
	private int lastHeight = 0;
	private boolean lastFullSizedLeft = false;

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

	public boolean reverse = false;

	private Box coolBox = new Box(0, 0, 0, 0);

	private boolean wasPowerPressed = false;

	private boolean fullSizeLeftButton = false;
	
	public int teleportX = -4500;
	public int teleportY = -4500;
	
	public boolean generateUpOnly = false;
	

	// private double pixelDensity = 1d;

	public YellowQuest(CanvasSurfaceView canvas) {
		this.canvas = canvas;
		this.activity = getContext();
		fullSizeLeftButton = canvas.getActivity().fullSizeLeftButton;
		activity = canvas.getActivity();
		gameData = canvas.getActivity().getGameData();
		createButtons(canvas);
		BOX_BUFFER = (canvas.width / 148) + 2;
		if (canvas.density < 2)
			BOX_BUFFER *= 2;
		PAINT_STATS.setTextSize(canvas.density * 12);
		PAINT_GAMEOVER.setTextSize(canvas.density * 30);
	}

	public void addAchievement(int achievement) {
		gameData.addAchievement(activity.getString(achievement));
	}

	public EntityPlatform getPlatform(int num) {
		for (int d = 0; d < boxes.size(); ++d) {
			if (!(boxes.get(d) instanceof EntityPlatform))
				continue;
			if (boxes.get(d).boxNumber == num)
				return (EntityPlatform) boxes.get(d);

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

	public void setFullSizeLeftButton(boolean fullSizeLeftButton) {
		this.fullSizeLeftButton = fullSizeLeftButton;
		createButtons(canvas);
	}

	public boolean renderSmall() {
		return canvas.density < 2;
	}

	public void createButtons(CanvasSurfaceView canvas) {
		if (lastWidth == canvas.width && lastHeight == canvas.height && fullSizeLeftButton == lastFullSizedLeft)
			return;
		double bsm = canvas.density;
		// pixelDensity = bsm;
		double leftExtra = 0;
		if (fullSizeLeftButton)
			leftExtra = 40;
		leftButton = new Box(20 * bsm, canvas.height - 120 * bsm, (80 + leftExtra) * bsm, canvas.height - 20 * bsm);
		rightButton = new Box((100 + leftExtra) * bsm, canvas.height - 120 * bsm, (200 + leftExtra) * bsm,
				canvas.height - 20 * bsm);
		jumpButton = new Box(canvas.width - 120 * bsm, canvas.height - 120 * bsm, canvas.width - 20 * bsm,
				canvas.height - 20 * bsm);

		rightButtonR = new Box(20 * bsm, canvas.height - 120 * bsm, 120 * bsm, canvas.height - 20 * bsm);
		leftButtonR = new Box(140 * bsm, canvas.height - 120 * bsm, (200 + leftExtra) * bsm, canvas.height - 20 * bsm);

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
		lastFullSizedLeft = fullSizeLeftButton;
	}

	public boolean doJump() {
		return canvas.touchInBox(jumpButton);
		// return wasdKeys[0];
	}

	public void drawArrow(CanvasSurfaceView rend, float x, float y, Paint paint, Direction dir) {
		switch (dir) {
		case UP:
			drawUpArrow(rend, x, y, paint);
			break;
		case DOWN:
			drawDownArrow(rend, x, y, paint);
			break;
		case CIRCLE:
			drawCircleArrow(rend, x, y, paint);
			break;
		case BACKWARDS:
			drawLeftArrow(rend, x, y, paint);
			break;
		case FORWARDS:
			drawRightArrow(rend, x, y, paint);
		}
	}

	public void draw(CanvasSurfaceView rend) {
		if (!display)
			return;
		if (gameOver != null && gameOver.time == 0)
			gameOver = null;
		
		long start = System.nanoTime();
		
		
		if (gameOver == null) {
			
			// drawArrow(rend, 100, 100, PAINT_COOLDOWN);
			for (Arrow arr : arrows) {
				drawArrow(rend, arr.x, arr.y, arr.paint, arr.dir);

			}
			tPos = 0;
			drawFlag(rend, (float) level.finalBox.x, (float) level.finalBox.box.ey);
			for (int d = 0; d < boxes.size(); ++d) {
				if (boxes.get(d).relativeArrow == null)
					continue;
				_iterEntity = boxes.get(d);
				drawArrow(rend, (float) (_iterEntity.x + _iterEntity.relativeArrow.x),
						(float) (_iterEntity.y + _iterEntity.relativeArrow.y), _iterEntity.relativeArrow.paint,
						_iterEntity.relativeArrow.dir);
			}
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
				if (cd == null)
					cd = PAINT_COOLDOWN;

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
		
		long time = System.nanoTime() - start;
		if(time > MS_HIGH){
			Log.w("YellowQuest", "draw() took " + (time / MS));
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
		if (level.isBonus) {
			rend.fillRect(x - 30, y + 30, 30, 30, PAINT_GAMEOVER);
			rend.fillRect(x, y, 5, 60, PAINT_BROWNER);
		} else {
			rend.fillRect(x - 30, y + 30, 30, 30, PAINT_RED);
			rend.fillRect(x, y, 5, 60, PAINT_BROWN);
		}
	}

	public void drawUpArrow(CanvasSurfaceView rend, float x, float y, Paint paint) {
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		rend.fillRect(x - 5, y, 10, 15, paint);

		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		
		if(reverse){
			if (rend.density >= 1.99) {
				path.moveTo(rend.width - (x - 15), rend.height - (y + 15));
				path.lineTo(rend.width - x, rend.height - (y + 30));
				path.lineTo(rend.width - (x + 15), rend.height - (y + 15));
				path.lineTo(rend.width - (x - 15), rend.height - (y + 15));
			} else {
				path.moveTo((rend.width - (x - 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y + 30)) / 2 + rend.height / 4);
				path.lineTo((rend.width - (x + 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((rend.width - (x - 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
			}
		} else {
			if (rend.density >= 1.99) {
				path.moveTo(x - 15, rend.height - (y + 15));
				path.lineTo(x, rend.height - (y + 30));
				path.lineTo(x + 15, rend.height - (y + 15));
				path.lineTo(x - 15, rend.height - (y + 15));
			} else {
				path.moveTo((x - 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo(x / 2 + rend.width / 4, (rend.height - (y + 30)) / 2 + rend.height / 4);
				path.lineTo((x + 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((x - 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
			}
		}
		
		path.close();

		rend.canvas.drawPath(path, paint);
	}
	
	
	public void drawLeftArrow(CanvasSurfaceView rend, float x, float y, Paint paint) {
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		rend.fillRect(x - 1, y + 10, 16, 10, paint);

		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		
		if(reverse){
			if (rend.density >= 1.99) {
				path.moveTo(rend.width - x, rend.height - y);
				path.lineTo(rend.width - (x - 15), rend.height - (y + 15));
				path.lineTo(rend.width - x, rend.height - (y + 30));
				path.lineTo(rend.width - x, rend.height - y);
			} else {
				path.moveTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
				path.lineTo((rend.width - (x - 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((rend.width  - x) / 2 + rend.width / 4, (rend.height - (y + 30)) / 2 + rend.height / 4);
				path.lineTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
			}
		} else {
			if (rend.density >= 1.99) {
				path.moveTo(x, rend.height - y);
				path.lineTo(x - 15, rend.height - (y + 15));
				path.lineTo(x, rend.height - (y + 30));
				path.lineTo(x, rend.height - y);
			} else {
				path.moveTo((x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
				path.lineTo((x - 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((x) + rend.width / 4, (rend.height - (y + 30)) / 2 + rend.height / 4);
				path.lineTo((x) + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
			}
		}
		
		path.close();

		rend.canvas.drawPath(path, paint);
	}
	
	public void drawRightArrow(CanvasSurfaceView rend, float x, float y, Paint paint) {
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		rend.fillRect(x - 15, y + 10, 16, 10, paint);

		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		if(reverse){
			if (rend.density >= 1.99) {
				path.moveTo(rend.width - x, rend.height - y);
				path.lineTo(rend.width - (x + 15), rend.height - (y + 15));
				path.lineTo(rend.width - x, rend.height - (y + 30));
				path.lineTo(rend.width - x, rend.height - y);
			} else {
				path.moveTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
				path.lineTo((rend.width - (x + 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y + 30)) / 2 + rend.height / 4);
				path.lineTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
			}
		} else {
			if (rend.density >= 1.99) {
				path.moveTo(x, rend.height - y);
				path.lineTo(x + 15, rend.height - (y + 15));
				path.lineTo(x, rend.height - (y + 30));
				path.lineTo(x, rend.height - y);
			} else {
				path.moveTo((x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
				path.lineTo((x + 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((x) / 2 + rend.width / 4, (rend.height - (y + 30)) / 2 + rend.height / 4);
				path.lineTo((x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
			}
		}
		path.close();

		rend.canvas.drawPath(path, paint);
	}
	
	public void drawCircleArrow(CanvasSurfaceView rend, float x, float y, Paint paint){
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		
		rend.fillRect(x - 10, y, 5, 20, paint);
		rend.fillRect(x + 5, y, 5, 20, paint);
		rend.fillRect(x - 6, y, 12, 5, paint);
		rend.fillRect(x - 6, y + 15, 12, 5, paint);
	}

	public void drawDownArrow(CanvasSurfaceView rend, float x, float y, Paint paint) {
		x -= player.x;
		y -= player.y;
		x += rend.width / 2;
		y += rend.height / 2;
		rend.fillRect(x - 5, y + 15, 10, 15, paint);

		Path path = new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		if(reverse){
			if (rend.density >= 1.99) {
				path.moveTo(rend.width - (x - 15), rend.height - (y + 15));
				path.lineTo(rend.width - x, rend.height - (y));
				path.lineTo(rend.width - (x + 15), rend.height - (y + 15));
				path.lineTo(rend.width - (x - 15), rend.height - (y + 15));
			} else {
				path.moveTo((rend.width - (x - 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((rend.width - x) / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
				path.lineTo((rend.width - (x + 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((rend.width - (x - 15)) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
			}
		} else {
			if (rend.density >= 1.99) {
				path.moveTo(x - 15, rend.height - (y + 15));
				path.lineTo(x, rend.height - (y));
				path.lineTo(x + 15, rend.height - (y + 15));
				path.lineTo(x - 15, rend.height - (y + 15));
			} else {
				path.moveTo((x - 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo(x / 2 + rend.width / 4, (rend.height - (y)) / 2 + rend.height / 4);
				path.lineTo((x + 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
				path.lineTo((x - 15) / 2 + rend.width / 4, (rend.height - (y + 15)) / 2 + rend.height / 4);
			}
		}
		
		path.close();

		rend.canvas.drawPath(path, paint);
	}

	public void generateBonusBoxes(int x, int y) {
		int ox = bgenX;
		int oy = bgenY;
		Level old = level;
		int oldNextBox = nextBox;

		bgenX = x;
		bgenY = y;
		level = new Level();
		level.isBonus = true;
		level.bonusType = old.bonusType;
		old.bonusLevel = level;
		level.number = old.number;
		level.size = 5 + random(5);
		nextBox = 0;

		generatingBonus = true;

		generateBoxes(10);

		generatingBonus = false;

		bgenX = ox;
		bgenY = oy;
		level = old;
		nextBox = oldNextBox;
	}

	public void generateBonusInfo() {
		if (generatingBonus)
			return;
		PowerInfo bType;
		if (lifeBonusNum == 0)
			bType = PowerInfo.generateBonus(this);
		else
			bType = PowerInfo.getData("life");
		if (bType != null) {
			level.bonusType = bType.getName();
			level.bonusPosition = 2 + random(level.size - 4);
			Log.i("YellowQuest", "Bonus Type: " + level.bonusType + ", Bonus Pos: " + level.bonusPosition);
		} else {
			level.bonusType = "";
			level.bonusPosition = -1;
		}
	}

	public void generateBoxBonus(EntityPlatform ent) {
		if (level.bonusType.equals("up")) {
			
			arrows.add(new Arrow(ent.x, ent.box.ey + 1020, Direction.UP, TraitUp.PAINT_GREEN));
			ent.relativeArrow = new Arrow(0, ent.box.ey - ent.y + 20, Direction.UP, TraitUp.PAINT_GREEN);
			
			generateBonusBoxes(bgenX + 150, bgenY + 1500);
			
			if (ent.hasTrait("up"))
				((TraitUp) ent.getTrait("up")).maxUpTime = 1500;
			
			ent.bonusType = "up";
			
		} else if (level.bonusType.equals("life")) {
			
			arrows.add(new Arrow((ent.box.sx + level.lastBoxType.box.ex) / 2,
					(ent.box.ey + level.lastBoxType.box.ey) / 2 + 20, Direction.DOWN, EntityPlayer.PAINT_YELLOW));
			
			level.lastBoxType.bonusType = "life";
			ent.bonusType = "life";
			ent.bonusData = 1;
			
			generateBonusBoxes(-30, 2000);
			
		} else if (level.bonusType.equals("bounce")) {
			
			EntityPlatform plat = getPlatform(ent.boxNumber - 3 + (random(4) == 0 ? 1 : 0));
			if (plat != null) {
				ent.bonusType = "bounce";
				ent.bonusData = 12;
				ent.relativeArrow = new Arrow(0, ent.box.ey - ent.y + 20, Direction.UP, TraitBounce.PAINT_MAGENTA);
				
				plat.bonusType = "bounce";
				plat.bonusData = 1742;
				plat.relativeArrow = new Arrow(0, plat.box.ey - plat.y + 20, Direction.DOWN, TraitBounce.PAINT_MAGENTA);
			}
			
			generateBonusBoxes(-30, 2000);
			
		} else if (level.bonusType.equals("teleport")){
			
			int tx = (int) ((ent.box.sx + level.lastBoxType.box.ex) / 2);
			int ty = (int) ((ent.box.ey + level.lastBoxType.box.ey) / 2 + 20);
			
			arrows.add(new Arrow(tx,
					ty, Direction.DOWN, TraitConveyor.PAINT_GREY));
			
			ty -= 400 + random(400);
			tx -= 20 + random(40);
			
			teleportX = tx;
			teleportY = ty;
			
			arrows.add(new Arrow(tx, ty - 10, Direction.CIRCLE, TraitConveyor.PAINT_GREY));
			arrows.add(new Arrow(0, 2090, Direction.CIRCLE, TraitConveyor.PAINT_GREY));
			
			generateBonusBoxes(-30, 2000);
			
		} else if(level.bonusType.equals("time")){
			
			ent.relativeArrow = new Arrow(0, ent.box.ey - ent.y + 20, Direction.CIRCLE, YellowQuest.PAINT_GAMEOVER);
			
			generateUpOnly = true;
			
			generateBonusBoxes((int)ent.x + 60, (int)ent.y + 120);
			
			bgenY -= 100;
			
			generateUpOnly = false;
			
			
		} else if(level.bonusType.equals("troll")){
			ent.traits = new Trait[]{new TraitTrollBonus(ent)};
		}
	}

	public void generateBoxes(int amount) {
		if (nextBox >= level.size)
			return;
		if (level.bonusPosition == 0)
			generateBonusInfo();
		double xe;
		double ye;
		double hs;
		int maxLifeAward = 0;
		EntityPlatform ent;
		for (int d = 0; d < amount; ++d) {
			if (nextBox >= level.size)
				break;
			int gainWidth = Math.max(0, 48 - level.number * 6);
			xe = 64 + gainWidth + random(127 - gainWidth);
			ye = 24 + random(23);
			if (level.lastBoxType != null) {
				hs = level.lastBoxType.getMaxYWithJump();
				hs -= bgenY;
				// hs -= ye / 2d;

				if (hs < 0) {
					bgenY += hs - random(5); // Boxes cannot be out of reach
					if (MainActivity.DEBUG)
						Log.i("YellowQuest", "hs: " + hs + ", bn: " + nextBox);
				}
			}
			ent = (EntityPlatform) WeightedTraitFactory.randomPlatform(this).calcBounds(bgenX + xe / 2, bgenY - ye / 2,
					xe, ye);

			if (level.isBonus && maxLifeAward < 3 && "life".equals(level.bonusType) && ent.traits.length < 2
					&& random(3) != 0) {
				++maxLifeAward;
				Trait[] after = new Trait[ent.traits.length + 1];
				for (int i = 0; i < ent.traits.length; ++i)
					after[i] = ent.traits[i];
				after[ent.traits.length] = new TraitExtraLife(ent);
				ent.traits = after;
			} else if(level.isBonus && "time".equals(level.bonusType)){
				if(ent.traits.length < 2){
					Trait[] after = new Trait[ent.traits.length + 1];
					for (int i = 0; i < ent.traits.length; ++i)
						after[i] = ent.traits[i];
					after[ent.traits.length] = new TraitTimeHidden(ent);
					ent.traits = after;
				} else {
					Trait[] after = new Trait[2];
					after[0] = ent.traits[random(2)];
					after[1] = new TraitTimeHidden(ent);
					ent.traits = after;
				}
			} 

			// if(!generatingBonus && ent.hasTrait("up"))
			// generateBonusBoxes(bgenX + 120, bgenY + 1510);

			ent.isBonus = generatingBonus;
			if (generatingBonus)
				ent.boxNumber += 9900;

			if (!level.isBonus && level.bonusPosition == ent.boxNumber) {
				generateBoxBonus(ent);
			}

			ent.install();
			level.lastBoxType = ent;
			boxes.add(ent);
			level.finalBox = ent;
			bgenX += xe;
			bgenX += 50;
			bgenX += random(150);
			int sbgenY = bgenY;
			if (generatingBonus)
				bgenY += random(170) - 50;
			else
				bgenY += random(230) - 110;
			
			if(generateUpOnly && bgenY < sbgenY) bgenY = sbgenY + random(120);
			
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
		if (pressed && !wasPowerPressed) {
			timerStarted = true;
			player.getPower().powerButtonPressed();
		}
		wasPowerPressed = pressed;
	}

	private void levelUp() {
		gameOver = new GameOver(1, "Next Level");
		gameData.statTracker.addStat(getContext(), Stat.COMPLETE_LEVEL, 1);
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
			int seconds = ((TIMER_MAX - timer) / TIMER_SECOND);
			seconds = Math.min(seconds, 5);
			addScore(seconds * 75);
		} else {
			addScore(((TIMER_MAX - timer) / TIMER_SECOND));
		}

		gameData.addScore(level.number + 1, levelScore);

		if (Math.abs(player.box.sx - level.finalBox.box.ex) < 1) {
			gameData.statTracker.addStat(getContext(), Stat.RIGHT_SIDE_FLAG, 1);
			addAchievement(R.string.achievement_overshot);
		}
		switch (level.number) {
		case 2:
			addAchievement(R.string.achievement_easy);
			break;
		case 4:
			PowerInfo.getData("life").unlock(getContext());
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

	public void load() {
		player = new EntityPlayer(this);
		reload();
	}

	public void nextLevel() {
		teleportX = -4500;
		teleportY = -4500;
		
		boxes.clear();
		arrows.clear();
		lifeBonusNum = 0;
		int lNum = level.number;
		level = new Level();
		level.number = lNum + 1;
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

	public MainActivity getContext() {
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
		teleportX = -4500;
		teleportY = -4500;
		
		gameOver = null;
		boxes.clear();
		arrows.clear();
		lifeBonusNum = 0;
		level = new Level();
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
		teleportX = -4500;
		teleportY = -4500;

		
		boxes.clear();
		arrows.clear();
		int lNum = level.number;
		level = new Level();
		level.number = lNum;
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
				long start = System.nanoTime();
				this.generateBoxes(1);
				long time = System.nanoTime() - start;
				if(time > MS_HIGH){
					Log.w("YellowQuest", "generateBoxes(1) took " + (time / MS));
				}
			}

			
			updateTimer();
			
			long start = System.nanoTime();
			
			updateInput();
			
			long time = System.nanoTime() - start;
			if(time > MS_HIGH){
				Log.w("YellowQuest", "updateInput() took " + (time / MS));
			}
			start = System.nanoTime();
			
			updatePowerButton();
			
			time = System.nanoTime() - start;
			if(time > MS_HIGH){
				Log.w("YellowQuest", "updatePowerButton() took " + (time / MS));
			}
			start = System.nanoTime();
			
			updateFalling();
			
			time = System.nanoTime() - start;
			if(time > MS_HIGH){
				Log.w("YellowQuest", "updateFalling() took " + (time / MS));
			}
			start = System.nanoTime();
			
			updateEntities();
			
			time = System.nanoTime() - start;
			if(time > MS_HIGH){
				Log.w("YellowQuest", "updateEntities() took " + (time / MS));
			}
			start = System.nanoTime();

			if (!level.isBonus && this.playerBox + 1 == level.size) {
				levelUp();
			} else if (level.isBonus && this.playerBox - 9900 + 1 == level.size) {
				addAchievement(R.string.achievement_bonus_time);
				if("time".equals(level.bonusType)){
					((PowerTimeStop)player.getPower()).resetTimers();
				}
				levelUp();
			}
			
			time = System.nanoTime() - start;
			if(time > MS_HIGH){
				Log.w("YellowQuest", "update level up took " + (time / MS));
			}
			//start = System.nanoTime();
			
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
		if (player.fallDist > 1010) {
			if (playerBox == 0 && level.number == 0) {
				addAchievement(R.string.achievement_big_failure);
			} else if (player.lastIntersected != null && player.lastIntersected.boxNumber + 2 >= level.size) {
				addAchievement(R.string.achievement_almost);
			}
			if (this.playerLives > 1) {
				this.playerLives -= 1;
				boolean lifeInter = false;
				if (player.lastIntersected != null && "life".equals(player.lastIntersected.bonusType)) {
					if (player.lastIntersected.bonusData == 0) {
						lifeInter = player.x > player.lastIntersected.box.sx;
					} else if (player.lastIntersected.bonusData == 1) {
						lifeInter = player.x < player.lastIntersected.box.ex;
					}
				}

				if (lifeInter) {
					lifeBonusNum++;
				} else {
					lifeBonusNum = 0;
				}

				if (lifeBonusNum >= 3) {
					player.fallDist = 0;
					player.teleport(0, 2400);
					gameOver = new GameOver(3, "Level Failed?");
					lifeBonusNum = 0;
				} else {
					gameOver = new GameOver(2, "Level Failed");
				}

			} else
				gameOver = new GameOver(0, "Game Over");
		}
	}

	private void updateGameOver() {
		--gameOver.time;
		if (gameOver.time == 0) {
			if (gameOver.type == 0) {
				gameData.statTracker.addStat(getContext(), Stat.DEATHS, 1);
				gameData.statTracker.lostLife(getContext());
				gameData.statTracker.gameOver(getContext());
				gameOver();
				setDisplaying(false);
				canvas.clearTouches();
				if (canvas.getActivity().passedOne)
					canvas.getActivity().loadPlayView();
				else
					canvas.getActivity().loadMenuView();
				gameData.statTracker.resetGame();
				// gameData.statTracker.generateChallenge();
			} else if (gameOver.type == 1) {
				gameData.statTracker.completeLevel(getContext());
				gameData.statTracker.resetLevel();
				this.nextLevel();
			} else if (gameOver.type == 2) {
				this.restartLevel();
				gameData.statTracker.addStat(getContext(), Stat.DEATHS, 1);
				gameData.statTracker.lostLife(getContext());
				gameData.statTracker.resetLife();
			} else if (gameOver.type == 3) {
				// Do nothing
			}
			gameOver = null;
		}
	}

	private void updateInput() {
		double accel = AIR_ACCEL;
		double jump = DEFAULT_JUMP;
		if (this.player.intersecting != null) {
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
			this.player.y_velocity = jump * player.getJumpMultiplier() + player.getJumpIncrease() + 0.25;
			gameData.statTracker.addStat(getContext(), Stat.JUMPS, 1);
			player.onGround = false;
			timerStarted = true;
		}
	}

	public void loadData() {
		if (timed || shadow) {
			MainActivity context = canvas.getActivity();
			if (!context.usedDifferentModes) {
				context.usedDifferentModes = true;
				context.getSettings().edit().putBoolean("usedmodes", true).commit();
			}
		}

		if (timed) {
			PowerInfo.getData("time").unlock(getContext());
		}

		String pName = gameData.getNextPower();
		if (pName == null || pName.trim().isEmpty())
			return;
		player.setPower(PowerInfo.getData(pName).newInstance(player, gameData.getPowerUpgradeLevel(pName)));
		gameData.setNextPower("");
		canvas.getActivity().saveData();
	}

	public void setDisplaying(boolean display) {
		this.display = display;
	}

	public boolean isDisplaying() {
		return display;
	}

	public void toastText(String text) {
		Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (int) (canvas.height * 0.2));
		toast.show();
	}

	public void toastText(int resId) {
		toastText(getContext().getString(resId));
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

	public void setCanvas(CanvasSurfaceView canvas) {
		this.canvas = canvas;
	}
}

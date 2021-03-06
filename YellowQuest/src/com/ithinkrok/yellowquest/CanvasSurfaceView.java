package com.ithinkrok.yellowquest;

import static android.view.MotionEvent.*;

import java.util.*;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.*;

import com.ithinkrok.yellowquest.util.Box;
import com.ithinkrok.yellowquest.util.Vector2;

public class CanvasSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private static Vector2 currentTouch;

	public static final double UPDATE_PERIOD = 1000 / 45d;
	// public static final double UPDATE_PERIOD = 1000/5d; //so lower ups makes
	// it start faster

	private CanvasThread _thread;
	public int width;
	public int height;
	public float density;
	public Canvas canvas;
	public YellowQuest game;
	private long lastUpdate = -1;
	private double tickTime = 0;
	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, Vector2> touches = new HashMap<Integer, Vector2>();
	public ArrayList<Vector2> touchDowns = new ArrayList<Vector2>(10);

	public boolean paused = true;

	private boolean screenOff = false;

	private boolean reverse = false;

	public int scaledWidth = 0;
	public int scaledHeight = 0;
	public int scaledX = 0;
	public int scaledY = 0;

	public void setReversed(boolean reverse) {
		this.reverse = reverse;
	}

	public boolean isReversed() {
		return reverse;
	}

	// private boolean tapped = false;

	public CanvasSurfaceView(Context context) {
		super(context);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		if (Build.VERSION.SDK_INT > 12) {
			getDisplaySize(size, display);
		} else {
			getOldSize(size, display);
		}
		scaledWidth = width = size.x;
		scaledHeight = height = size.y;
		if (density < 2) {
			scaledWidth *= 2;
			scaledHeight *= 2;
			scaledX = -width;
			scaledY = -height;
		}
		getHolder().addCallback(this);
		setFocusable(true);
		density = getResources().getDisplayMetrics().density;
		if (getActivity().gameHolder != null) {
			game = getActivity().gameHolder;
			game.setCanvas(this);
		} else {
			game = new YellowQuest(this);
			game.load();
			getActivity().gameHolder = game;
		}
	}

	@SuppressWarnings("deprecation")
	private void getOldSize(Point size, Display display) {
		int w = display.getWidth();
		int h = display.getHeight();
		size.set(w, h);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void getDisplaySize(Point size, Display display) {
		display.getSize(size);
	}

	public int floor(float f) {
		int i = (int) f;
		if (i > f)
			--i;
		return i;
	}

	public void fillRect(float x, float y, float w, float h, Paint paint) {
		if (density < 2) {
			fillRectSmall(x, y, w, h, paint);
			return;
		}
		// if(!reverse){
		// canvas.drawRect(floor(x), floor(height - y - h), floor(x + w),
		// floor(height - y), paint);
		// } else {
		// canvas.drawRect(width - floor(x + w), floor(height - y - h), width -
		// floor(x), floor(height - y), paint);
		// }
		if (!reverse) {
			canvas.drawRect(x, height - y - h, x + w, height - y, paint);
		} else {
			canvas.drawRect(width - (x + w), height - y - h, width - x, height - y, paint);
		}
	}

	private void fillRectSmall(float x, float y, float w, float h, Paint paint) {
		// if(!reverse){
		// canvas.drawRect((floor(x) / 2) + width / 4, (floor(height - y - h) /
		// 2) + height / 4, (floor(x + w) / 2) + width / 4, (floor(height - y) /
		// 2) + height / 4, paint);
		// } else {
		// canvas.drawRect((width - floor(x + w) / 2) + width / 4, (floor(height
		// - y - h) / 2) + height / 4, ((width - floor(x)) / 2) + width / 4,
		// (floor(height - y) / 2) + height / 4, paint);
		// }
		if (!reverse) {
			canvas.drawRect((x / 2) + width / 4, ((height - y - h) / 2) + height / 4, ((x + w) / 2) + width / 4,
					((height - y) / 2) + height / 4, paint);
		} else {
			canvas.drawRect((width - (x + w) / 2) + width / 4, ((height - y - h) / 2) + height / 4, ((width - (x)) / 2)
					+ width / 4, ((height - y) / 2) + height / 4, paint);
		}
	}

	public MainActivity getActivity() {
		return (MainActivity) getContext();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (paused || screenOff)
			return;
		// if(tapped){
		if (lastUpdate == -1)
			lastUpdate = System.nanoTime();
		long now = System.nanoTime();
		double diff = (now - lastUpdate) / 1000000;
		tickTime += diff;
		int updatesDone = 0;
		if(tickTime > UPDATE_PERIOD * 4) tickTime = UPDATE_PERIOD + 0.001;
		while (tickTime > UPDATE_PERIOD) {
			tickTime -= UPDATE_PERIOD;
			game.update();
			updatesDone++;
			if (updatesDone > 5) {

				// Attempt to prevent annoying things happening if there is a
				// lag spike
				tickTime = 0;
				// touchDowns.clear();
			}
		}
		lastUpdate = now;
		// }

		this.canvas = canvas;
		// canvas.drawColor(Color.BLACK, Mode.CLEAR);
		game.draw(this);
		this.canvas = null;
	}

	public void onPause() {
		paused = true;
	}

	public void screenOff() {
		screenOff = true;
	}

	public void onResume() {
		paused = false;
		lastUpdate = -1;
	}

	public void screenOn() {
		screenOff = false;
		lastUpdate = -1;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);

		_thread = new CanvasThread(getHolder(), this);
		_thread.setRunning(true);
		_thread.start();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// _thread.setRunning(false);
		// _thread.setPause(true);
		if (MainActivity.DEBUG)
			Log.i("CSV", "New Size: [" + width + "," + height + "]");
		this.width = width;
		this.height = height;
		scaledWidth = width;
		scaledHeight = height;
		if (density < 2) {
			scaledWidth *= 2;
			scaledHeight *= 2;
			scaledX = -width;
			scaledY = -height;
		}
		game.createButtons(this);
		// _thread.setPause(false);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			// getActivity().view = null;
			_thread.setRunning(false);
			_thread.join();
		} catch (InterruptedException e) {
			Log.i("CanvasSurfaceView", "Interupted Exception when ending draw thread");
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// tapped = true;
		int actionIndex = MotionEventCompat.getActionIndex(event);
		int id = MotionEventCompat.getPointerId(event, actionIndex);
		switch (MotionEventCompat.getActionMasked(event)) {
		case ACTION_DOWN:
		case ACTION_POINTER_DOWN:
		case ACTION_MOVE:
			// Log.i("YellowQuest", "Pointer id: " + id + " down/moved");
			double x = MotionEventCompat.getX(event, actionIndex);
			double y = MotionEventCompat.getY(event, actionIndex);
			// Log.i("YellowQuest", "Pointer X: " + x + ", Pointer Y: " + y);
			setTouch(id, x, y);
			break;
		case ACTION_POINTER_UP:
		case ACTION_UP:
			// Log.i("YellowQuest", "Pointer id: " + id + " up");
			removeTouch(id);
			break;
		}
		return true;
	}

	public Collection<Vector2> getTouches() {
		return touches.values();
	}

	public void clearTouches() {
		touches.clear();
		touchDowns.clear();
	}

	private void setTouch(int id, double x, double y) {
		Vector2 touch = touches.get(id);
		if (touch == null) {
			touch = new Vector2(x, y);
			touches.put(id, touch);
			touchDowns.add(touch);
		} else {
			touch.set(x, y);
		}
	}

	private void removeTouch(int id) {
		Vector2 touch = touches.remove(id);
		if (touch != null) {
			touchDowns.remove(touch);
		}
	}

	public boolean touchInBox(Box test) {
		for (int d = 0; d < touchDowns.size(); ++d) {
			currentTouch = touchDowns.get(d);
			if (currentTouch.x < test.sx || currentTouch.x > test.ex)
				continue;
			if (currentTouch.y < test.sy || currentTouch.y > test.ey)
				continue;
			return true;
		}
		return false;
	}

}

package com.ithinkrok.yellowquest;

import static android.view.MotionEvent.*;

import java.util.Collection;
import java.util.HashMap;

import com.ithinkrok.yellowquest.util.Box;
import com.ithinkrok.yellowquest.util.Vector2;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.*;

public class CanvasSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	
	private MainActivity activity;
	private CanvasThread _thread;
	public int width;
	public int height;
	public Canvas canvas;
	public YellowQuest game;
	private long lastUpdate;
	private double tickTime = 0;
	public HashMap<Integer, Vector2> touches = new HashMap<>();

	public CanvasSurfaceView(MainActivity context) {
		super(context);
		width = getWidth();
		height = getHeight();
		activity = context;
		getHolder().addCallback(this);
		setFocusable(true);
		game = new YellowQuest(this);
	}
	
	private int floor(float f){
		int i = (int)f;
		if(i > f) --i;
		return i;
	}
	
	public void fillRect(float x, float y, float w, float h, Paint paint) {
	    //canvas.drawRect(x, height - y - h, x + w, height - y, paint);
		canvas.drawRect(floor(x), floor(height - y - h), floor(x + w), floor(height - y), paint);
	}
	
	public MainActivity getActivity() {
		return activity;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(lastUpdate == -1) lastUpdate = System.nanoTime();
		long now = System.nanoTime();
		double diff = (now - lastUpdate) / 1000000;
		tickTime += diff;
		while(tickTime > (1000/45d)){
			tickTime -= (1000/45d);
			game.update();
		}
		lastUpdate = now;
		
		this.canvas = canvas;
		//canvas.drawColor(Color.BLACK, Mode.CLEAR);
		game.draw(this);
		this.canvas = null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false);
		
		game.load();
		
		_thread = new CanvasThread(getHolder(), this);
		_thread.setRunning(true);
		_thread.start();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//_thread.setRunning(false);
		//_thread.setPause(true);
		Log.i("CSV", "New Size: [" + width + "," + height + "]");
		this.width = width;
		this.height = height;
		game.createButtons();
		//_thread.setPause(false);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			_thread.setRunning(false);
			_thread.join();
		} catch (InterruptedException e) {
			Log.i("CanvasSurfaceView", "Interupted Exception when ending draw thread");
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int actionIndex = MotionEventCompat.getActionIndex(event);
		int id = MotionEventCompat.getPointerId(event, actionIndex);
		switch(MotionEventCompat.getActionMasked(event)){
		case ACTION_DOWN:
		case ACTION_POINTER_DOWN:
		case ACTION_MOVE:
			//Log.i("YellowQuest", "Pointer id: " + id + " down/moved");
			double x = MotionEventCompat.getX(event, actionIndex);
			double y = MotionEventCompat.getY(event, actionIndex);
			//Log.i("YellowQuest", "Pointer X: " + x + ", Pointer Y: " + y);
			setTouch(id, x, y);
			break;
		case ACTION_POINTER_UP:
		case ACTION_UP:
			//Log.i("YellowQuest", "Pointer id: " + id + " up");
			removeTouch(id);
			break;
		}
		return true;
	}
	
	
	public Collection<Vector2> getTouches(){
		return touches.values();
	}
	
	private void setTouch(int id, double x, double y){
		touches.put(id, new Vector2(x, y));
	}
	
	private void removeTouch(int id){
		touches.remove(id);
	}
	
	public boolean touchInBox(Box test){
		for(Vector2 v : getTouches()){
			if(v.x < test.sx || v.x > test.ex) continue;
			if(v.y < test.sy || v.y > test.ey) continue;
			return true;
		}
		return false;
	}

}

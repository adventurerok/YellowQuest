package com.ithinkrok.yellowquest;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class CanvasThread extends Thread {

//	private final static int FRAME_PERIOD = 1000 / 50;
	
	private SurfaceHolder _surfaceHolder;
	private CanvasSurfaceView _panel;
	private boolean _run = false;
	private boolean _pause = false;

	public CanvasThread(SurfaceHolder surfaceHolder, CanvasSurfaceView panel) {
		_surfaceHolder = surfaceHolder;
		_panel = panel;
	}

	public void setRunning(boolean run) { // Allow us to stop the thread
		_run = run;
	}
	
	public void setPause(boolean pause){
		_pause = pause;
	}

	@Override
	public void run() {
		Canvas c;
//		long beginTime = 0;
//		long timeDiff = 0;
//		long sleepTime = 0;
		while (_run) { // When setRunning(false) occurs, _run is
			if(_pause) continue;
			c = null; // set to false and loop ends, stopping thread

			try {
//				beginTime = System.nanoTime();
				c = _surfaceHolder.lockCanvas(null);
				synchronized (_surfaceHolder) {

					// Insert methods to modify positions of items in onDraw()
					_panel.postInvalidate();

				}
			} finally {
				if (c != null) {
					_surfaceHolder.unlockCanvasAndPost(c);
				}
			}
//			timeDiff = (System.nanoTime() - beginTime) / 1000000;
//			sleepTime = FRAME_PERIOD - timeDiff;
//			if(sleepTime < 3) sleepTime = 3;
//			try {
//				Thread.sleep(sleepTime);
//			} catch (InterruptedException e) {
//				
//			}
		}
	}
}

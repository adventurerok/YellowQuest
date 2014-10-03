package com.ithinkrok.yellowquest;

import java.lang.ref.WeakReference;

import android.content.*;

public class ScreenReceiver extends BroadcastReceiver {
	
	WeakReference<MainActivity> activity;
	
	public IntentFilter filter;
	public boolean registered = false;
	
	public ScreenReceiver(MainActivity activity) {
		this.activity = new WeakReference<MainActivity>(activity);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		MainActivity act = activity.get();
		if(act == null) return;
		if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			act.screenOff();
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
			act.screenOn();
		}
	}

}

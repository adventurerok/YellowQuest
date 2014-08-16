package com.ithinkrok.yellowquest;

import android.content.*;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.example.games.basegameutils.BaseGameActivity;


public class MainActivity extends BaseGameActivity {

	private CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = true;

	private boolean paused = false;
	private boolean screenOff = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);

		view = new CanvasSurfaceView(this);
		setContentView(view);

		if (audioEnabled) {

			media = MediaPlayer.create(this, R.raw.boxgameloop);
			media.setLooping(true);

			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			am = (AudioManager) getSystemService(AUDIO_SERVICE);

			OnAudioFocusChangeListener audioListener = new OnAudioFocusChangeListener() {

				@Override
				public void onAudioFocusChange(int focusChange) {
					if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
						media.pause();
					} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
						media.start();
					} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
						// am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
						am.abandonAudioFocus(this);
						media.stop();
						media.release();
						media = null;
					}
				}
			};

			int result = am.requestAudioFocus(audioListener, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				media.start();
			}

		}
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		BroadcastReceiver receiver = new ScreenReceiver(this);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		paused = true;
		super.onPause();
		if (audioEnabled)
			media.pause();
		view.onPause();
	}

	public void screenOff() {
		screenOff = true;
		if (audioEnabled)
			media.pause();
		view.screenOff();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (audioEnabled && !screenOff)
			media.start();
		view.onResume();
	}

	public void screenOn() {
		screenOff = false;
		if (audioEnabled && !paused)
			media.start();
		view.screenOn();
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}


}

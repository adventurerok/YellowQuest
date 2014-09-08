package com.ithinkrok.yellowquest;

import android.content.*;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import com.google.example.games.basegameutils.BaseGameActivity;


public class MainActivity extends BaseGameActivity implements View.OnTouchListener{

	private TextView menu_play;
	private TextView menu_achievements;
	private TextView menu_leaderboards;
	private TextView menu_settings;
	
	
	private CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = false;

	private boolean paused = false;
	private boolean screenOff = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);

		//menu = findViewById(R.layout.game);
		setContentView(R.layout.menu);
		
		menu_play = (TextView) findViewById(R.id.menu_play);
		menu_achievements = (TextView) findViewById(R.id.menu_achievements);
		menu_leaderboards = (TextView) findViewById(R.id.menu_leaderboards);
		menu_settings = (TextView) findViewById(R.id.menu_settings);
		
		menu_play.setOnTouchListener(this);
		menu_achievements.setOnTouchListener(this);
		menu_leaderboards.setOnTouchListener(this);
		menu_settings.setOnTouchListener(this);
		
		view = new CanvasSurfaceView(this);
		//setContentView(view);

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
		if(view != null) view.onPause();
	}

	public void screenOff() {
		screenOff = true;
		if (audioEnabled)
			media.pause();
		if(view != null) view.screenOff();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (audioEnabled && !screenOff)
			media.start();
		if(view != null) view.onResume();
	}

	public void screenOn() {
		screenOff = false;
		if (audioEnabled && !paused)
			media.start();
		if(view != null) view.screenOn();
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.performClick();
		
		return true;
	}
	
	


}

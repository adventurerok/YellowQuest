package com.ithinkrok.yellowquest;

import android.app.AlertDialog;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.view.*;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;


public class MainActivity extends BaseGameActivity implements View.OnClickListener{

	private static enum GameState {
		MENU,
		SETTINGS,
		GAME;
	}
	
	private GameState state;
	
	private GameProgress progress;
	private TextView menu_play;
	private TextView menu_achievements;
	private TextView menu_leaderboards;
	private TextView menu_settings;
	private View sign_in_button;
	private View sign_out_button;
	private CheckBox settings_music;
	private TextView settings_back;
	
	
	private CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	OnAudioFocusChangeListener audioListener;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = false;

	private boolean paused = false;
	private boolean screenOff = false;
	
	private SharedPreferences settings;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		
		progress = new GameProgress(this);
		
		settings = getSharedPreferences("com.ithinkrok.yellowquest", Context.MODE_PRIVATE);

		//menu = findViewById(R.layout.game);
		loadMenuView();
		
		view = new CanvasSurfaceView(this);
		//setContentView(view);

		audioEnabled = settings.getBoolean("music", true);
		if (audioEnabled) {
			audioStart();
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
	
	public GameProgress getProgress() {
		return progress;
	}
	
	@Override
	public GoogleApiClient getApiClient() {
		return super.getApiClient();
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
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
	    findViewById(R.id.sign_out_button).setVisibility(View.GONE);
	}

	@Override
	public void onSignInSucceeded() {
		View signIn = findViewById(R.id.sign_in_button);
	    View signOut = findViewById(R.id.sign_out_button);
	    if(signIn != null) signIn.setVisibility(View.GONE);
	    if(signOut != null) signOut.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.menu_play:
			loadGameView();
			break;
		case R.id.menu_achievements:
			if(getApiClient() == null || !getApiClient().isConnected()){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.cant_connect_to_google);
				builder.setTitle(R.string.cant_view_achievements);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return;
			}
			startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 1);
			break;
		case R.id.menu_leaderboards:
			break;
		case R.id.menu_settings:
			loadSettingsView();
			break;
		case R.id.settings_music:
			audioEnabled = settings_music.isChecked();
			if(audioEnabled){
				audioStart();
			} else audioStop();
			Editor editor = settings.edit();
			editor.putBoolean("music", audioEnabled);
			editor.commit();
			break;
		case R.id.settings_back:
			loadMenuView();
			break;
		case R.id.sign_in_button:
			beginUserInitiatedSignIn();
			break;
		case R.id.sign_out_button:
			signOut();
			sign_out_button.setVisibility(View.GONE);
			sign_in_button.setVisibility(View.VISIBLE);
			break;
		}
		
	}
	
	
	public void loadMenuView(){
		state = GameState.MENU;
		setContentView(R.layout.menu);
		
		menu_play = (TextView) findViewById(R.id.menu_play);
		menu_achievements = (TextView) findViewById(R.id.menu_achievements);
		menu_leaderboards = (TextView) findViewById(R.id.menu_leaderboards);
		menu_settings = (TextView) findViewById(R.id.menu_settings);
		sign_in_button = findViewById(R.id.sign_in_button);
		sign_out_button = findViewById(R.id.sign_out_button);
		
		menu_play.setOnClickListener(this);
		menu_achievements.setOnClickListener(this);
		menu_leaderboards.setOnClickListener(this);
		menu_settings.setOnClickListener(this);
		
		sign_in_button.setOnClickListener(this);
		sign_out_button.setOnClickListener(this);
		
		if(isSignedIn()){
			sign_in_button.setVisibility(View.GONE);
			sign_out_button.setVisibility(View.VISIBLE);
		}
	}
	
	public void loadSettingsView(){
		state = GameState.SETTINGS;
		setContentView(R.layout.settings);
		
		settings_music = (CheckBox) findViewById(R.id.settings_music);
		settings_back = (TextView) findViewById(R.id.settings_back);
		
		settings_music.setOnClickListener(this);
		settings_back.setOnClickListener(this);
		
		settings_music.setChecked(audioEnabled);
	}
	
	public void loadGameView(){
		state = GameState.GAME;
		setContentView(view);
	}
	
	@Override
	public void onBackPressed() {
		if(state == GameState.GAME || state == GameState.SETTINGS){
			loadMenuView();
		} else super.onBackPressed();
	}
	
	public void audioStart(){
		media = MediaPlayer.create(this, R.raw.boxgameloop);
		media.setLooping(true);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		am = (AudioManager) getSystemService(AUDIO_SERVICE);

		audioListener = new OnAudioFocusChangeListener() {

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
	
	public void audioStop(){
		if(media == null) return;
		media.stop();
		media.release();
		media = null;
		if(audioListener != null){
			am.abandonAudioFocus(audioListener);
			audioListener = null;
		}
	}
	


}

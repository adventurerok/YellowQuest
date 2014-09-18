package com.ithinkrok.yellowquest;

import android.app.AlertDialog;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.ithinkrok.yellowquest.entity.power.Power;
import com.ithinkrok.yellowquest.ui.PowerAdapter;


public class MainActivity extends BaseGameActivity implements View.OnClickListener{

	private static enum GameState {
		MENU,
		SETTINGS,
		SETUP,
		GAME;
	}
	
	private GameState state;
	
	private GameData gameData;
	private TextView menu_play;
	private View menu_achievements;
	private View menu_leaderboards;
	private View menu_settings;
	private View sign_in_button;
	private View sign_out_button;
	private CheckBox settings_music;
	private TextView settings_back;
	private ListView play_powers;
	
	private TextView play_play;
	private TextView play_back;
	private CheckBox play_shadow;
	private CheckBox play_time;
	private TextView play_score;
	
	
	private CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	OnAudioFocusChangeListener audioListener;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = false;
	private boolean shadowMode = false;
	private boolean timeMode = false;

	private boolean paused = false;
	private boolean screenOff = false;
	
	private SharedPreferences settings;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		
		gameData = new GameData(this);
		
		settings = getSharedPreferences("com.ithinkrok.yellowquest", Context.MODE_PRIVATE);
		
		gameData.load(settings);

		//menu = findViewById(R.layout.game);
		loadMenuView();
		
		view = new CanvasSurfaceView(this);
		//setContentView(view);

		audioEnabled = settings.getBoolean("music", true);
		shadowMode = settings.getBoolean("shadow", false);
		timeMode = settings.getBoolean("time", false);
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
	
	public SharedPreferences getSettings() {
		return settings;
	}
	
	public GameData getGameData() {
		return gameData;
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
			loadPlayView();
			break;
		case R.id.play_play:
			view.game.setGameMode(play_shadow.isChecked(), play_time.isChecked());
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
			Editor editorMusic = settings.edit();
			editorMusic.putBoolean("music", audioEnabled);
			editorMusic.commit();
			break;
		case R.id.play_back:
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
		case R.id.play_shadow:
			shadowMode = play_shadow.isChecked();
			Editor editorShadow = settings.edit();
			editorShadow.putBoolean("shadow", shadowMode);
			editorShadow.commit();
			break;
		case R.id.play_time:
			shadowMode = play_shadow.isChecked();
			Editor editorTime = settings.edit();
			editorTime.putBoolean("time", timeMode);
			editorTime.commit();
			break;
		}
		
	}
	
	
	public void loadMenuView(){
		state = GameState.MENU;
		setContentView(R.layout.menu);
		
		menu_play = (TextView) findViewById(R.id.menu_play);
		menu_achievements = findViewById(R.id.menu_achievements);
		menu_leaderboards = findViewById(R.id.menu_leaderboards);
		menu_settings = findViewById(R.id.menu_settings);
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
	
	public void loadPlayView(){
		state = GameState.SETUP;
		setContentView(R.layout.play);
		
		play_play = (TextView) findViewById(R.id.play_play);
		play_back = (TextView) findViewById(R.id.play_back);
		play_shadow = (CheckBox) findViewById(R.id.play_shadow);
		play_time = (CheckBox) findViewById(R.id.play_time);
		play_score = (TextView) findViewById(R.id.play_score);
		play_powers = (ListView) findViewById(R.id.play_powers);
		
		play_play.setOnClickListener(this);
		play_back.setOnClickListener(this);
		play_shadow.setOnClickListener(this);
		play_time.setOnClickListener(this);
		
		play_shadow.setChecked(shadowMode);
		play_time.setChecked(timeMode);
		
		
		PowerAdapter adapter = new PowerAdapter(this);
		play_powers.setAdapter(adapter);
		
		
		int hiscore = gameData.getHiScore();
		int previous = gameData.getPreviousScore();
		String text = getString(R.string.hiscore_x_previous_y);
		text = String.format(text, hiscore, previous);
		play_score.setText(text);
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
		view.game.setDisplaying(true);
	}
	
	@Override
	public void onBackPressed() {
		if(state == GameState.GAME){
			view.game.gameOver();
			loadPlayView();
		} else if(state == GameState.SETTINGS || state == GameState.SETUP){
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

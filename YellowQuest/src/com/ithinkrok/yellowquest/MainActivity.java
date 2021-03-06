package com.ithinkrok.yellowquest;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.widget.*;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.ithinkrok.yellowquest.entity.EntityPlayer;
import com.ithinkrok.yellowquest.ui.*;

public class MainActivity extends BaseGameActivity implements View.OnClickListener {
	
	public static final boolean DEBUG = false;

	public static enum GameState {
		MENU, SETTINGS, PLAY, GAME, BUY, LEVELS, HISCORES;
	}

	public GameState state;

	private GameData gameData;
	private TextView menu_play;
	private View menu;
	private View menu_achievements;
	private View menu_leaderboards;
	private View menu_settings;
	private View sign_in_button;
	private View sign_out_button;
	private CheckBox settings_music;
	private CheckBox settings_leftbutton;
	private CheckBox settings_tips;
	private TextView settings_back;
	private ListView play_powers;
	private Button play_money;

	private TextView play_play;
	private TextView play_back;
	private ImageButton play_shadow;
	private ImageButton play_time;
	private TextView play_score;
	private AdView play_ad;

	private TextView buy_money;
	private TextView buy_back;
	private ListView buy_list;
	
	private TextView play_level;
	
	private TextView levels_money;
	private TextView levels_back;
	private TextView levels_play;
	private ListView levels_list;
	
	private TextView hiscores_scores;
	private TextView hiscores_ranks;
	private TextView hiscores_shadowtime;
	private TextView hiscores_back;

	public IInAppBillingService buyService;
	public Intent buyIntent;
	
	public boolean passedOne = false;
	
	public YellowQuest gameHolder = null;
	
	public boolean buyConnected = false;
	
	public boolean usedDifferentModes = false;

	ServiceConnection buyConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			buyConnected = false;
			buyService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			buyConnected = true;
			buyService = IInAppBillingService.Stub.asInterface(service);
			if(buyAdapter == null) buyAdapter = new BuyAdapter(MainActivity.this);
			else buyAdapter.query();
			checkPurchases();
		}
	};


	public CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	private MediaPlayer mediaNormal;
	private MediaPlayer mediaTime;
	OnAudioFocusChangeListener audioListener;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = false;
	public boolean fullSizeLeftButton = false;
	private boolean shadowMode = false;
	private boolean timeMode = false;

	private boolean paused = false;
	private boolean screenOff = false;

	private SharedPreferences settings;
	private SharedPreferences challenges;

	private PowerAdapter powerAdapter;
	private BuyAdapter buyAdapter;
	private LevelsAdapter levelsAdapter;
	
	private ScreenReceiver screenReciever;
	
	public SharedPreferences rateSettings;
	public boolean enableTips = true;
	
	
	private SparseArray<Drawable> preloadedDrawables = new SparseArray<Drawable>();
	
	public YellowQuest getGame(){
		return view.game;
	}
	
	public EntityPlayer getPlayer(){
		return view.game.player;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ToastSystem.setContext(this);
		ToastSystem.generateCache();
		AchievementInfo.load(this);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


		gameData = new GameData(this);

		settings = getSharedPreferences("com.ithinkrok.yellowquest", Context.MODE_PRIVATE);
		challenges = getSharedPreferences("challenge", Context.MODE_PRIVATE);
		rateSettings = getSharedPreferences("rate_app", 0);

		gameData.load(settings, challenges);

		buyIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		buyIntent.setPackage("com.android.vending");

		bindService(buyIntent, buyConnection, Context.BIND_AUTO_CREATE);

		// menu = findViewById(R.layout.game);
		
		//enableDebugLog(true);
		setRequestedClients(BaseGameActivity.CLIENT_GAMES);
		super.onCreate(savedInstanceState);
		
		loadMenuView();

		// setContentView(view);

		usedDifferentModes = settings.getBoolean("usedmodes", false);
		audioEnabled = settings.getBoolean("music", true);
		fullSizeLeftButton = settings.getBoolean("fullsizeleft", false);
		shadowMode = settings.getBoolean("shadow", false);
		timeMode = settings.getBoolean("time", false);
		passedOne = settings.getBoolean("passed", false);
		enableTips = settings.getBoolean("tips", true);
		if (audioEnabled) {
			audioStart();
		}

		view = new CanvasSurfaceView(this);

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		screenReciever = new ScreenReceiver(this);
		screenReciever.filter = filter;
		registerReceiver(screenReciever, filter);
		screenReciever.registered = true;

		powerAdapter = new PowerAdapter(this);
		levelsAdapter = new LevelsAdapter(this);
		
		PowerInfo.unlockNewAchievements(this);
		
		AppRater.app_launched(this);
	}
	
	public void saveAll(){
		gameData.save(settings.edit(), challenges.edit());
	}
	
	
	public void preloadDrawables(){
		loadDrawable(R.drawable.achievement_almost);
		loadDrawable(R.drawable.achievement_big_failure);
		loadDrawable(R.drawable.achievement_bonus_time);
		loadDrawable(R.drawable.achievement_easy);
		loadDrawable(R.drawable.achievement_expert);
		loadDrawable(R.drawable.achievement_impossible);
		loadDrawable(R.drawable.achievement_hard);
		loadDrawable(R.drawable.achievement_jumpman);
		loadDrawable(R.drawable.achievement_medium);
		loadDrawable(R.drawable.achievement_overshot);
		loadDrawable(R.drawable.achievement_upgrade);
		loadDrawable(R.drawable.achievement_unlocker);
		loadDrawable(R.drawable.achievement_omnipotent);
		loadDrawable(R.drawable.achievement_headstart);
		loadDrawable(R.drawable.achievement_bonus_exploiter);
		
		loadDrawable(R.drawable.unlock_backwards);
		loadDrawable(R.drawable.unlock_bounce);
		loadDrawable(R.drawable.unlock_extra_life);
		loadDrawable(R.drawable.unlock_powers);
		loadDrawable(R.drawable.unlock_teleport);
		loadDrawable(R.drawable.unlock_time_stop);
		loadDrawable(R.drawable.unlock_up);
		loadDrawable(R.drawable.unlock_doublejump);
		loadDrawable(R.drawable.unlock_stick);
		
		loadDrawable(R.drawable.bonus_bounce);
		loadDrawable(R.drawable.bonus_life);
		loadDrawable(R.drawable.bonus_up);
		loadDrawable(R.drawable.bonus_teleport);
		loadDrawable(R.drawable.bonus_time_stop);
		loadDrawable(R.drawable.bonus_troll);
		loadDrawable(R.drawable.bonus_doublejump);
		loadDrawable(R.drawable.bonus_stick);
		
		loadDrawable(R.drawable.challenge_dont_go_left);
		loadDrawable(R.drawable.challenge_dont_go_right);
		loadDrawable(R.drawable.challenge_level_up);
		loadDrawable(R.drawable.challenge_failed);
		loadDrawable(R.drawable.challenge_restart);
		
		loadDrawable(R.drawable.new_hiscore);
	}
	
	
	public Drawable loadDrawable(int res){
		Drawable d = preloadedDrawables.get(res);
		if(d == null){
			d = getResources().getDrawable(res);
			preloadedDrawables.put(res, d);
		}
		return d;
	}
	
	public void setPassedOne() {
		if(!this.passedOne){
			settings.edit().putBoolean("passed", true).apply();
			
			ToastSystem.showUnlockToast(R.drawable.unlock_powers, R.string.powers_view_unlock);
		}
		this.passedOne = true;
	}

	@Override
	protected void onPause() {
		paused = true;
		if(state == GameState.PLAY && play_ad != null) play_ad.pause();
		view.clearTouches();
		super.onPause();
		if (audioEnabled)
			media.pause();
		if (view != null)
			view.onPause();
		if (buyService != null) {
			unbindService(buyConnection);
		}

	}
	
	public void saveData(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				gameData.saveOnly(settings.edit());
			}
		});
	}

	public SharedPreferences getSettings() {
		return settings;
	}

	public GameData getGameData() {
		return gameData;
	}

	@Override
	protected void onDestroy() {
		saveAll();
		if(screenReciever.registered){
			unregisterReceiver(screenReciever);
			screenReciever.registered = false;
		}
		super.onDestroy();
		if (buyConnected) {
			try{
				unbindService(buyConnection);
			} catch(Exception e){
				Log.w("YellowQuest", "Failed ton unbind buyConnection");
			}
		}
		ToastSystem.setContext(null);
	}

	@Override
	public GoogleApiClient getApiClient() {
		return super.getApiClient();
	}

	public void screenOff() {
		view.clearTouches();
		screenOff = true;
		if (audioEnabled)
			media.pause();
		if (view != null)
			view.screenOff();
	}

	@Override
	protected void onResume() {
		if(state == GameState.PLAY && play_ad != null) play_ad.resume();
		super.onResume();
		if (audioEnabled && !screenOff)
			media.start();
		if (view != null)
			view.onResume();
		bindService(buyIntent, buyConnection, Context.BIND_AUTO_CREATE);
	}

	public void screenOn() {
		screenOff = false;
		if (audioEnabled && !paused)
			media.start();
		if (view != null)
			view.screenOn();
	}

	@Override
	public void onSignInFailed() {
		//Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
		View signIn = findViewById(R.id.sign_in_button);
		View signOut = findViewById(R.id.sign_out_button);
		if (signIn != null)
			signIn.setVisibility(View.VISIBLE);
		if (signOut != null)
			signOut.setVisibility(View.GONE);
	}

	@Override
	public void onSignInSucceeded() {
		//Toast.makeText(this, "Sign in success", Toast.LENGTH_SHORT).show();
		View signIn = findViewById(R.id.sign_in_button);
		View signOut = findViewById(R.id.sign_out_button);
		if (signIn != null)
			signIn.setVisibility(View.GONE);
		if (signOut != null)
			signOut.setVisibility(View.VISIBLE);
		gameData.updateOnlineHiScore();
		gameData.addOfflineAchievements();
		try{
			gameData.saveOnly(settings.edit());
		} catch(Exception e){}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_level:
			loadLevelsView();
			break;
		case R.id.menu:
		case R.id.menu_play:
		case R.id.buy_back:
			if(passedOne) loadPlayView();
			else loadGameView();
			break;
		case R.id.levels_back:
			loadPlayView();
			break;
		case R.id.levels_play:
			int level = levelsAdapter.selected;
			if(level < 1){
				view.game.setGameMode(shadowMode, timeMode);
				loadGameView();
				break;
			}
			gameData.subtractScorePoints(levelsAdapter.price(level));
			view.game.setGameMode(shadowMode, timeMode);
			loadGameView(level);
			
			view.game.addAchievement(R.string.achievement_headstart);
			
			String sub = getString(R.string.scorepoints_subtracted);
			sub = StringFormatter.format(sub, levelsAdapter.price(level));
			ToastSystem.showTextToast(sub);
			levelsAdapter.selected = -1;
			break;
		case R.id.play_play:
			view.game.setGameMode(shadowMode, timeMode);
			loadGameView();
			break;
		case R.id.menu_achievements:
			if (getApiClient() == null || !getApiClient().isConnected()) {
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
			gameData.addOfflineAchievements();
			startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 1);
			break;
		case R.id.menu_leaderboards:
			loadHiscoresView();
			break;
		case R.id.hiscores_score:
			if (getApiClient() == null || !getApiClient().isConnected()) {
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
			gameData.updateOnlineHiScore();
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(),
					getString(R.string.leaderboard_yellowquest_hiscores)), 1);
			break;
		case R.id.hiscores_rank:
			if (getApiClient() == null || !getApiClient().isConnected()) {
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
			gameData.updateOnlineHiScore();
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(),
					getString(R.string.leaderboard_yellowquest_ranks)), 1);
			break;
		case R.id.hiscores_shadowtime:
			if (getApiClient() == null || !getApiClient().isConnected()) {
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
			gameData.updateOnlineHiScore();
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(),
					getString(R.string.leaderboard_yellowquest_shadowtime_hiscores)), 1);
			break;
		case R.id.menu_settings:
			loadSettingsView();
			break;
		case R.id.settings_music:
			audioEnabled = settings_music.isChecked();
			if (audioEnabled) {
				audioStart();
			} else
				audioStop();
			Editor editorMusic = settings.edit();
			editorMusic.putBoolean("music", audioEnabled);
			editorMusic.apply();
			break;
		case R.id.settings_leftbutton:
			fullSizeLeftButton = settings_leftbutton.isChecked();
			view.game.setFullSizeLeftButton(fullSizeLeftButton);
			Editor editorLeft = settings.edit();
			editorLeft.putBoolean("fullsizeleft", fullSizeLeftButton);
			editorLeft.apply();
			break;
		case R.id.settings_tips:
			enableTips = settings_tips.isChecked();
			Editor editorTips = settings.edit();
			editorTips.putBoolean("tips", enableTips);
			editorTips.apply();
			break;
		case R.id.play_back:
		case R.id.settings_back:
		case R.id.hiscores_back:
			loadMenuView();
			break;
		case R.id.sign_in_button:
			beginUserInitiatedSignIn();
			sign_in_button.setVisibility(View.GONE);
			break;
		case R.id.sign_out_button:
			signOut();
			sign_out_button.setVisibility(View.GONE);
			sign_in_button.setVisibility(View.VISIBLE);
			break;
		case R.id.play_shadow:
			shadowMode = !shadowMode;
			audioChange();
			if (shadowMode) {
				Toast.makeText(this, R.string.shadow_on, Toast.LENGTH_SHORT).show();
				play_shadow.setImageResource(R.drawable.shadow_on);
			} else {
				Toast.makeText(this, R.string.shadow_off, Toast.LENGTH_SHORT).show();
				;
				play_shadow.setImageResource(R.drawable.shadow_off);
			}
			Editor editorShadow = settings.edit();
			editorShadow.putBoolean("shadow", shadowMode);
			editorShadow.apply();
			break;
		case R.id.play_time:
			timeMode = !timeMode;
			audioChange();
			if (timeMode) {
				Toast.makeText(this, R.string.time_on, Toast.LENGTH_SHORT).show();
				play_time.setImageResource(R.drawable.time_off);
			} else {
				Toast.makeText(this, R.string.time_off, Toast.LENGTH_SHORT).show();
				play_time.setImageResource(R.drawable.time_on);
			}
			Editor editorTime = settings.edit();
			editorTime.putBoolean("time", timeMode);
			editorTime.apply();
			break;
		case R.id.play_money:
			ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo net = cm.getActiveNetworkInfo();
			if(net != null && net.isConnected()) loadBuyView();
			else Toast.makeText(this, R.string.no_purchase_without_internet, Toast.LENGTH_SHORT).show();;
		}

	}

	public void loadMenuView() {
		state = GameState.MENU;
		setContentView(R.layout.menu);
		
		menu = findViewById(R.id.menu);
		menu_play = (TextView) findViewById(R.id.menu_play);
		menu_achievements = findViewById(R.id.menu_achievements);
		menu_leaderboards = findViewById(R.id.menu_leaderboards);
		menu_settings = findViewById(R.id.menu_settings);
		sign_in_button = findViewById(R.id.sign_in_button);
		sign_out_button = findViewById(R.id.sign_out_button);

		menu_play.setOnClickListener(this);
		menu.setOnClickListener(this);
		menu_achievements.setOnClickListener(this);
		menu_leaderboards.setOnClickListener(this);
		menu_settings.setOnClickListener(this);

		sign_in_button.setOnClickListener(this);
		sign_out_button.setOnClickListener(this);

		if (isSignedIn()) {
			sign_in_button.setVisibility(View.GONE);
			sign_out_button.setVisibility(View.VISIBLE);
		}
	}

	public void loadPlayView() {
		state = GameState.PLAY;
		gameData.statTracker.generateChallenge();
		setContentView(R.layout.play);

		play_play = (TextView) findViewById(R.id.play_play);
		play_back = (TextView) findViewById(R.id.play_back);
		play_shadow = (ImageButton) findViewById(R.id.play_shadow);
		play_time = (ImageButton) findViewById(R.id.play_time);
		play_score = (TextView) findViewById(R.id.play_score);
		play_powers = (ListView) findViewById(R.id.play_powers);
		play_money = (Button) findViewById(R.id.play_money);
		play_ad = (AdView) findViewById(R.id.play_ad);
		play_level = (TextView) findViewById(R.id.play_level);

		play_play.setOnClickListener(this);
		play_back.setOnClickListener(this);
		play_shadow.setOnClickListener(this);
		play_time.setOnClickListener(this);
		play_money.setOnClickListener(this);
		play_level.setOnClickListener(this);

		if (shadowMode)
			play_shadow.setImageResource(R.drawable.shadow_on);
		else
			play_shadow.setImageResource(R.drawable.shadow_off);
		if (timeMode)
			play_time.setImageResource(R.drawable.time_off);
		else
			play_time.setImageResource(R.drawable.time_on);

		if(gameData.getPowerUnlocks() != 0) play_money.setText(BoxMath.formatNumberWithoutSuffix(gameData.getScorePoints()) + " SP");
		else{
			play_money.setVisibility(View.GONE);
			play_level.setVisibility(View.GONE);
		}

		if (powerAdapter == null)
			powerAdapter = new PowerAdapter(this);
		play_powers.setAdapter(powerAdapter);
		powerAdapter.refreshItems();
		powerAdapter.setView(play_powers);

		if(gameData.getPowerUnlocks() != 0){
			int hiscore = gameData.getHiScore();
			int previous = gameData.getPreviousScore();
			int rank = gameData.getPlayerRank();
			String text = getString(R.string.hiscore_x_previous_y);
			text = StringFormatter.format(text, hiscore, previous, rank);
			play_score.setText(text);
		} else {
			play_score.setText(R.string.no_powers);
		}
		
		if(gameData.hasMadePurchase()){
			play_ad.setVisibility(View.GONE);
		} else {
			play_ad.resume();
			AdRequest request = new AdRequest.Builder().build();
			//play_ad.setAdSize(AdSize.BANNER);
			//play_ad.setAdUnitId("ca-app-pub-3997067583457090/3896801162");
			play_ad.loadAd(request);
		}
	}

	public void loadSettingsView() {
		state = GameState.SETTINGS;
		setContentView(R.layout.settings);

		settings_music = (CheckBox) findViewById(R.id.settings_music);
		settings_leftbutton = (CheckBox) findViewById(R.id.settings_leftbutton);
		settings_back = (TextView) findViewById(R.id.settings_back);
		settings_tips = (CheckBox) findViewById(R.id.settings_tips);

		settings_music.setOnClickListener(this);
		settings_leftbutton.setOnClickListener(this);
		settings_back.setOnClickListener(this);
		settings_tips.setOnClickListener(this);

		settings_music.setChecked(audioEnabled);
		settings_leftbutton.setChecked(fullSizeLeftButton);
		settings_tips.setChecked(enableTips);
	}
	
	public void loadLevelsView(){
		state = GameState.LEVELS;
		setContentView(R.layout.levels);
		
		levels_back = (TextView) findViewById(R.id.levels_back);
		levels_play = (TextView) findViewById(R.id.levels_play);
		levels_money = (TextView) findViewById(R.id.levels_money);
		levels_list = (ListView) findViewById(R.id.levels_list);
		
		levels_back.setOnClickListener(this);
		levels_play.setOnClickListener(this);
		
		levels_money.setText(BoxMath.formatNumberWithoutSuffix(gameData.getScorePoints()) + " SP");
		
		if (levelsAdapter == null)
			levelsAdapter = new LevelsAdapter(this);
		levelsAdapter.selected = -1;
		levels_list.setAdapter(levelsAdapter);
	}

	public void loadBuyView() {
		state = GameState.BUY;
		setContentView(R.layout.buy);

		buy_back = (TextView) findViewById(R.id.buy_back);
		buy_money = (TextView) findViewById(R.id.buy_money);
		buy_list = (ListView) findViewById(R.id.buy_list);

		buy_back.setOnClickListener(this);

		buy_money.setText(BoxMath.formatNumberWithoutSuffix(gameData.getScorePoints()) + " SP");

		if (buyAdapter == null)
			buyAdapter = new BuyAdapter(this);
		buy_list.setAdapter(buyAdapter);
		// powerAdapter.setView(buy_list);
	}
	
	public void loadGameView(){
		if(play_ad != null) play_ad.pause();
		loadGameView(0);
	}
	
	public void loadHiscoresView(){
		state = GameState.HISCORES;
		setContentView(R.layout.hiscores);
		
		hiscores_scores = (TextView) findViewById(R.id.hiscores_score);
		hiscores_ranks = (TextView) findViewById(R.id.hiscores_rank);
		hiscores_shadowtime = (TextView) findViewById(R.id.hiscores_shadowtime);
		hiscores_back = (TextView) findViewById(R.id.hiscores_back);
		
		hiscores_scores.setOnClickListener(this);
		hiscores_ranks.setOnClickListener(this);
		hiscores_shadowtime.setOnClickListener(this);
		hiscores_back.setOnClickListener(this);
	}

	public void loadGameView(int level) {
		state = GameState.GAME;
		setContentView(view);
		view.game.setGameMode(shadowMode, timeMode);
		view.game.setDisplaying(true);
		view.game.reload();
		view.game.loadData();
		if(level > 0) view.game.loadLevel(level);
	}

	@Override
	public void onBackPressed() {
		if (state == GameState.GAME) {
			view.game.gameOver();
			if(passedOne) loadPlayView();
			else loadMenuView();
		} else if (state == GameState.BUY || state == GameState.LEVELS) {
			loadPlayView();
		} else if (state == GameState.SETTINGS || state == GameState.PLAY || state == GameState.HISCORES) {
			loadMenuView();
		} else
			super.onBackPressed();
	}
	
	public MediaPlayer getMedia() {
		return media;
	}
	
	public void audioChange(){
		MediaPlayer old = media;
		if(timeMode){
			media = mediaTime;
		} else {
			media = mediaNormal;
		}
		if(old == media) return;
		int oldPos = old.getCurrentPosition();
		old.pause();
		double oldTempo = 140;
		if(old == mediaTime) oldTempo = 200;
		double newTempo = 140;
		if(media == mediaTime) newTempo = 200;
		int newPos = (int) (oldPos * (oldTempo / newTempo));
		media.seekTo(newPos);
		media.start();
	}

	public void audioStart() {
		if(mediaNormal == null){
			mediaNormal = MediaPlayer.create(this, R.raw.boxgameloop);
			mediaNormal.setLooping(true);
		}
		if(mediaTime == null){
			mediaTime = MediaPlayer.create(this, R.raw.timemode);
			mediaTime.setLooping(true);
		}
		if(timeMode){
			media = mediaTime;
		} else {
			media = mediaNormal;
		}

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		am = (AudioManager) getSystemService(AUDIO_SERVICE);

		audioListener = new OnAudioFocusChangeListener() {

			@Override
			public void onAudioFocusChange(int focusChange) {
				if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
					getMedia().pause();
				} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
					getMedia().start();
				} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
					audioStop();
				}
			}
		};

		int result = am.requestAudioFocus(audioListener, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			media.start();
		}
	}

	public void audioStop() {
		if (media == null)
			return;
		media.stop();
		mediaNormal.release();
		mediaTime.release();
		mediaNormal = null;
		mediaTime = null;
		if (audioListener != null) {
			am.abandonAudioFocus(audioListener);
			audioListener = null;
		}
	}

	public void invalidateUI() {
		switch (state) {
		case MENU:
			loadMenuView();
			break;
		case BUY:
			loadBuyView();
			break;
		case SETTINGS:
			loadSettingsView();
			break;
		case PLAY:
			loadPlayView();
			break;
		}
	}
	
	public void updateScorePointCounter(){
		if(state == GameState.PLAY){
			play_money.setText(BoxMath.formatNumberWithoutSuffix(gameData.getScorePoints()) + " SP");
		} else if(state == GameState.BUY){
			buy_money.setText(BoxMath.formatNumberWithoutSuffix(gameData.getScorePoints()) + " SP");
		} else if(state == GameState.LEVELS){
			levels_money.setText(BoxMath.formatNumberWithoutSuffix(gameData.getScorePoints()) + " SP");
		}
	}

	public void checkPurchases() {
		Thread check = new Thread() {

			@Override
			public void run() {
				if(buyService == null) return;
				try {
					Bundle ownedItems = buyService.getPurchases(3, getPackageName(), "inapp", null);
					int response = ownedItems.getInt("RESPONSE_CODE");
					boolean alerted = false;
					if (response == 0) {
						ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
						ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
						//ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");

						for (int i = 0; i < purchaseDataList.size(); ++i) {
							String purchaseData = purchaseDataList.get(i);
							JSONObject jo = new JSONObject(purchaseData);
							//String signature = signatureList.get(i);
							String sku = ownedSkus.get(i);
							
							if(!alerted){
								//buyAdapter.toast("You had some unconsumed purchases. Trying to consume");
								alerted = true;
							}
							
							buyAdapter.consume(sku, jo.getString("purchaseToken"));
						}

					}
				} catch (RemoteException e) {
					Log.w("YellowQuest", e);
				} catch (JSONException e) {
					Log.w("YellowQuest", e);
				}
			}
		};
		check.start();
	}

	@Override
	protected void onActivityResult(int request, int response, Intent data) {
		if (request != 1001){
			super.onActivityResult(request, response, data);
			return;
		}
		//buyAdapter.toast("Yo! You tried to make a purchase!");
		int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
		String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
		//String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
		//int i = RESULT_CANCELED;
		if (response == RESULT_OK && responseCode == 0) {
			try {
				JSONObject jo = new JSONObject(purchaseData);
				String sku = jo.getString("productId");
				String token = jo.getString("purchaseToken");
				if (buyAdapter != null)
					//buyAdapter.toast("consummer");
					buyAdapter.consume(sku, token);
				//Log.i("YellowQuest", "You have bought the " + sku + ". Excellent choice, adventurer!");
			} catch (JSONException e) {
				//Log.i("YellowQuest", "Failed to parse purchase data.");
				//e.printStackTrace();
			}
		} else {
			//buyAdapter.toast("Other response: " + responseCode);
		}
	}
}

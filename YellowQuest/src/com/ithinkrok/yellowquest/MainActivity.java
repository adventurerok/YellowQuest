package com.ithinkrok.yellowquest;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.ithinkrok.yellowquest.ui.BuyAdapter;
import com.ithinkrok.yellowquest.ui.PowerAdapter;

public class MainActivity extends BaseGameActivity implements View.OnClickListener {
	
	public static final boolean DEBUG = false;

	private static enum GameState {
		MENU, SETTINGS, SETUP, GAME, BUY;
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
	private CheckBox settings_leftbutton;
	private TextView settings_back;
	private ListView play_powers;
	private Button play_money;

	private TextView play_play;
	private TextView play_back;
	private ImageButton play_shadow;
	private ImageButton play_time;
	private TextView play_score;

	private TextView buy_money;
	private TextView buy_back;
	private ListView buy_list;

	public IInAppBillingService buyService;
	public Intent buyIntent;
	
	public boolean passedOne = false;
	
	public YellowQuest gameHolder = null;
	
	public boolean buyConnected = false;

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

	private PowerAdapter powerAdapter;

	public CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	OnAudioFocusChangeListener audioListener;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = false;
	public boolean fullSizeLeftButton = false;
	private boolean shadowMode = false;
	private boolean timeMode = false;

	private boolean paused = false;
	private boolean screenOff = false;

	private SharedPreferences settings;

	private BuyAdapter buyAdapter;
	
	private ScreenReceiver screenReciever;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


		gameData = new GameData(this);

		settings = getSharedPreferences("com.ithinkrok.yellowquest", Context.MODE_PRIVATE);

		gameData.load(settings);

		buyIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		buyIntent.setPackage("com.android.vending");

		bindService(buyIntent, buyConnection, Context.BIND_AUTO_CREATE);

		// menu = findViewById(R.layout.game);
		
		//enableDebugLog(true);
		setRequestedClients(BaseGameActivity.CLIENT_GAMES);
		super.onCreate(savedInstanceState);
		
		loadMenuView();

		// setContentView(view);

		audioEnabled = settings.getBoolean("music", true);
		fullSizeLeftButton = settings.getBoolean("fullsizeleft", false);
		shadowMode = settings.getBoolean("shadow", false);
		timeMode = settings.getBoolean("time", false);
		passedOne = settings.getBoolean("passed", false);
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
		
		AppRater.app_launched(this);
	}
	
	public void setPassedOne() {
		if(!this.passedOne){
			settings.edit().putBoolean("passed", true).commit();
			//Toast.makeText(this, R.string.powers_view_unlock, Toast.LENGTH_SHORT).show();
			view.game.toastText(R.string.powers_view_unlock);
		}
		this.passedOne = true;
	}

	@Override
	protected void onPause() {
		paused = true;
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
				gameData.save(settings.edit());
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
	}

	@Override
	public GoogleApiClient getApiClient() {
		return super.getApiClient();
	}

	public void screenOff() {
		screenOff = true;
		if (audioEnabled)
			media.pause();
		if (view != null)
			view.screenOff();
	}

	@Override
	protected void onResume() {
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
		gameData.addOfflineAchievements();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_play:
		case R.id.buy_back:
			if(passedOne) loadPlayView();
			else loadGameView();
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
			startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 1);
			break;
		case R.id.menu_leaderboards:
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
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(),
					getString(R.string.leaderboard_yellowquest_hiscores)), 1);
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
			editorMusic.commit();
			break;
		case R.id.settings_leftbutton:
			fullSizeLeftButton = settings_leftbutton.isChecked();
			view.game.setFullSizeLeftButton(fullSizeLeftButton);
			Editor editorLeft = settings.edit();
			editorLeft.putBoolean("fullsizeleft", fullSizeLeftButton);
			editorLeft.commit();
			break;
		case R.id.play_back:
		case R.id.settings_back:
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
			editorShadow.commit();
			break;
		case R.id.play_time:
			timeMode = !timeMode;
			if (timeMode) {
				Toast.makeText(this, R.string.time_on, Toast.LENGTH_SHORT).show();
				play_time.setImageResource(R.drawable.time_off);
			} else {
				Toast.makeText(this, R.string.time_off, Toast.LENGTH_SHORT).show();
				play_time.setImageResource(R.drawable.time_on);
			}
			Editor editorTime = settings.edit();
			editorTime.putBoolean("time", timeMode);
			editorTime.commit();
			break;
		case R.id.play_money:
			loadBuyView();
		}

	}

	public void loadMenuView() {
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

		if (isSignedIn()) {
			sign_in_button.setVisibility(View.GONE);
			sign_out_button.setVisibility(View.VISIBLE);
		}
	}

	public void loadPlayView() {
		state = GameState.SETUP;
		setContentView(R.layout.play);

		play_play = (TextView) findViewById(R.id.play_play);
		play_back = (TextView) findViewById(R.id.play_back);
		play_shadow = (ImageButton) findViewById(R.id.play_shadow);
		play_time = (ImageButton) findViewById(R.id.play_time);
		play_score = (TextView) findViewById(R.id.play_score);
		play_powers = (ListView) findViewById(R.id.play_powers);
		play_money = (Button) findViewById(R.id.play_money);

		play_play.setOnClickListener(this);
		play_back.setOnClickListener(this);
		play_shadow.setOnClickListener(this);
		play_time.setOnClickListener(this);
		play_money.setOnClickListener(this);

		if (shadowMode)
			play_shadow.setImageResource(R.drawable.shadow_on);
		else
			play_shadow.setImageResource(R.drawable.shadow_off);
		if (timeMode)
			play_time.setImageResource(R.drawable.time_off);
		else
			play_time.setImageResource(R.drawable.time_on);

		if(gameData.getPowerUnlocks() != 0) play_money.setText(BoxMath.formatNumber(gameData.getScorePoints()) + " SP");
		else play_money.setVisibility(View.GONE);

		if (powerAdapter == null)
			powerAdapter = new PowerAdapter(this);
		play_powers.setAdapter(powerAdapter);
		powerAdapter.setView(play_powers);

		if(gameData.getPowerUnlocks() != 0){
			int hiscore = gameData.getHiScore();
			int previous = gameData.getPreviousScore();
			String text = getString(R.string.hiscore_x_previous_y);
			text = String.format(text, hiscore, previous);
			play_score.setText(text);
		} else {
			play_score.setText(R.string.no_powers);
		}
	}

	public void loadSettingsView() {
		state = GameState.SETTINGS;
		setContentView(R.layout.settings);

		settings_music = (CheckBox) findViewById(R.id.settings_music);
		settings_leftbutton = (CheckBox) findViewById(R.id.settings_leftbutton);
		settings_back = (TextView) findViewById(R.id.settings_back);

		settings_music.setOnClickListener(this);
		settings_leftbutton.setOnClickListener(this);
		settings_back.setOnClickListener(this);

		settings_music.setChecked(audioEnabled);
		settings_leftbutton.setChecked(fullSizeLeftButton);
	}

	public void loadBuyView() {
		state = GameState.BUY;
		setContentView(R.layout.buy);

		buy_back = (TextView) findViewById(R.id.buy_back);
		buy_money = (TextView) findViewById(R.id.buy_money);
		buy_list = (ListView) findViewById(R.id.buy_list);

		buy_back.setOnClickListener(this);

		buy_money.setText(BoxMath.formatNumber(gameData.getScorePoints()) + " SP");

		if (buyAdapter == null)
			buyAdapter = new BuyAdapter(this);
		buy_list.setAdapter(buyAdapter);
		// powerAdapter.setView(buy_list);
	}

	public void loadGameView() {
		state = GameState.GAME;
		setContentView(view);
		view.game.setGameMode(shadowMode, timeMode);
		view.game.setDisplaying(true);
		view.game.reload();
		view.game.loadData();
	}

	@Override
	public void onBackPressed() {
		if (state == GameState.GAME) {
			view.game.gameOver();
			if(passedOne) loadPlayView();
			else loadMenuView();
		} else if (state == GameState.BUY) {
			loadPlayView();
		} else if (state == GameState.SETTINGS || state == GameState.SETUP) {
			loadMenuView();
		} else
			super.onBackPressed();
	}

	public void audioStart() {
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

	public void audioStop() {
		if (media == null)
			return;
		media.stop();
		media.release();
		media = null;
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
		case SETUP:
			loadPlayView();
			break;
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

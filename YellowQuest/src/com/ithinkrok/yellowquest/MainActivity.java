package com.ithinkrok.yellowquest;

import android.app.Activity;
import android.media.*;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	
	private CanvasSurfaceView view;
	private AudioManager am;
	private MediaPlayer media;
	public boolean[] wasdKeys = new boolean[4];
	private boolean audioEnabled = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		view = new CanvasSurfaceView(this);
		setContentView(view);
		
		if(audioEnabled){
		
		media = MediaPlayer.create(this, R.raw.boxgameloop);
		media.setLooping(true);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		am = (AudioManager) getSystemService(AUDIO_SERVICE);
		
		
		OnAudioFocusChangeListener audioListener = new OnAudioFocusChangeListener() {
			
			 @Override
			public void onAudioFocusChange(int focusChange) {
			        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
			            media.pause();
			        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			            media.start();
			        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			            //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
			            am.abandonAudioFocus(this);
			            media.stop();
			            media.release();
			            media = null;
			        }
			    }
		};
		
		int result = am.requestAudioFocus(audioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
		if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
			media.start();
		}
			
		}
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if(audioEnabled)media.pause();
		//touches.clear();
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(audioEnabled)media.start();
		//touches.clear();
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_W) wasdKeys[0] = true;
		else if(keyCode == KeyEvent.KEYCODE_A) wasdKeys[1] = true;
		else if(keyCode == KeyEvent.KEYCODE_S) wasdKeys[2] = true;
		else if(keyCode == KeyEvent.KEYCODE_D) wasdKeys[3] = true;
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_W) wasdKeys[0] = false;
		else if(keyCode == KeyEvent.KEYCODE_A) wasdKeys[1] = false;
		else if(keyCode == KeyEvent.KEYCODE_S) wasdKeys[2] = false;
		else if(keyCode == KeyEvent.KEYCODE_D) wasdKeys[3] = false;
		return super.onKeyUp(keyCode, event);
	}*/
	
	
}

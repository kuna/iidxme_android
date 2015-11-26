package com.kuna.iidxme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.Window;

public class SplashActivity extends Activity {
	private Thread mSplashThread;
	private Context _c = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		mSplashThread =  new Thread(){
			@Override
			public void run(){
				try {
					synchronized(this){
						wait(2000);
					}
				}
				catch(InterruptedException ex){                    
				}
				
				finish();
				
				Intent intent = new Intent();
				intent.setClass(_c, MainviewActivity.class);
				startActivity(intent);
				//Thread.currentThread().interrupt();
				finish();                    
			}
		};

		mSplashThread.start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           return false;
        }
		return super.onKeyDown(keyCode, event);
	}
}

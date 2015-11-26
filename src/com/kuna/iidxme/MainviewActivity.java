package com.kuna.iidxme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainviewActivity extends ActionBarActivity {
	private Context _c = this;
	private WebView wv = null;
	private ProgressDialog progressDialog = null; // for loading screen
	private Handler loginHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainview);
		
		// Handler for checking login
		// set activity title with current login status
		loginHandler = new Handler() {
			String appname = getResources().getString(R.string.app_name);
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					// logout
					getSupportActionBar().setTitle(appname + " (not logined)");
				} else if (msg.what == 1) {
					// login
					getSupportActionBar().setTitle(appname + " (logined)");
				}
				super.handleMessage(msg);
			}
		};
		Injector.setLoginHandler(loginHandler);

		// load preference & default setting (change listener)
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				Injector.updateSetting(sharedPreferences);
			}
		});
		Injector.updateSetting(prefs);

		// basic setting (webview - js injection)
		WebView wView = (WebView) findViewById(R.id.webview);
		wv = wView;
		wView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
				new AlertDialog.Builder(_c).setTitle("AlertDialog").setMessage(message)
						.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				}).setCancelable(false).create().show();
				return true;
			};
		});
		wView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				// show loading status
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(_c);
					progressDialog.setTitle("Loading ...");
					progressDialog.setMessage("페이지를 읽고 있습니다, 잠시만 기다려 주세요.\n로그인 상태가 제대로 표시되지 않는다면 페이지를 리프래시 해 주세요..");
					progressDialog.setCancelable(false);
					progressDialog.show();
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.d("webview_url", url);

				Injector.InjectJSFile(_c, view, "jquery-2.1.4.min.js");
				Injector.InjectJSFile(_c, view, "inject_mobile.js"); // inject
																		// basic
																		// js... (jquery)
				Injector.doTask(view, url); // check is there any task

				// close loading screen
				progressDialog.dismiss();
				progressDialog = null;
			}
		});
		WebSettings webSettings = wView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowUniversalAccessFromFileURLs(true);
		// we can check login status only on PC screen
		webSettings
				.setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");

		wView.loadUrl("http://p.eagate.573.jp");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mainview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_help) {
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			return true;
		} else {
			/*
			 * custom function menus
			 */
			if (id == R.id.action_login_me) {
				Injector.makeTask(wv, "login_myid");
			} else if (id == R.id.action_login_master) {
				Injector.makeTask(wv, "login_masterid");
			} else if (id == R.id.action_add_mycard) {
				Injector.makeTask(wv, "add_mycard");
			} else if (id == R.id.action_remove_mycard) {
				Injector.makeTask(wv, "remove_card");
			} else if (id == R.id.action_use_mastercard) {
				Injector.makeTask(wv, "use_mastercard");
			} else if (id == R.id.action_iidxme) {
				Injector.makeTask(wv, "iidxme");
			}
		}
		return super.onOptionsItemSelected(item);
	}
}

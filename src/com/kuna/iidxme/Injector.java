package com.kuna.iidxme;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public class Injector {
	private static String status = "";		// this indicates what will be done
	private static Boolean islogined = false;
	
	// login handler
	private static Handler loginHandler = null;
	private static void setLoginStatus(boolean login) {
		islogined = login;
		if (loginHandler != null) {
			loginHandler.obtainMessage(login?1:0).sendToTarget();
			Log.d("webview_login", login?"true":"false");
		}
	}
	public static void setLoginHandler(Handler h) {
		loginHandler = h;
	}
	
	// these are all about settings
	private static String myid = "";
	private static String mypass = "";
	private static String masterid = "";
	private static String masterpass = "";
	private static String mycardid = "";
	private static String mycardpass = "";
	private static String mastercardid = "";
	private static String iidxmeid = "";
	private static String iidxmepass = "";
	
	public static void updateSetting(SharedPreferences prefs) {
		myid = prefs.getString("myid", "please do setting");
		mypass = prefs.getString("mypass", "");
		masterid = prefs.getString("masterid", "please do setting");
		masterpass = prefs.getString("masterpass", "");
		mycardid = prefs.getString("mycardid", "");
		mycardpass = prefs.getString("mycardpass", "");
		mastercardid = prefs.getString("mastercardid", "");
		iidxmeid = prefs.getString("iidxmeid", "");
		iidxmepass = prefs.getString("iidxmepass", "");
	}
	
	public static void redirectTask(WebView wv, String targetUrl, String taskname) {
		status = taskname;
		wv.loadUrl(targetUrl);
	}
	
	public static void makeTask(WebView wv, String taskname) {
		status = taskname;
		doTask(wv, wv.getUrl());
	}
	
	public static boolean isLogined() {
		return islogined;
	}
	
	public static void doTask(WebView wv, String url) {
		// if url includes logout.html, then it must be logouted!
		if (url.indexOf("logout.html") >= 0)
			islogined = false;
		
		if (status.compareTo("login_myid")==0) {
			// if already logined, then ignore (must logout first)
			if (islogined) {
				showJSAlert(wv, "로그아웃을 한 후 다시 시도합니다");
				wv.loadUrl("http://p.eagate.573.jp/gate/p/logout.html");
				return;
			}
			// if not currently login page then redirect
			if (url.indexOf("p.eagate.573.jp/gate/p/login.html") < 0) {
				wv.loadUrl("https://p.eagate.573.jp/gate/p/login.html");
				return;
			}
			// enter id/pass. logging in is on your own.
			InjectJS(wv, String.format("enterIDPASS('%s', '%s');", myid, mypass));
		} else if (status.compareTo("login_masterid")==0) {
			// if already logined, then logout first
			if (islogined) {
				showJSAlert(wv, "로그아웃을 한 후 다시 시도합니다");
				wv.loadUrl("http://p.eagate.573.jp/gate/p/logout.html");
				return;
			}
			// if not currently login page then redirect
			if (url.indexOf("p.eagate.573.jp/gate/p/login.html") < 0) {
				wv.loadUrl("https://p.eagate.573.jp/gate/p/login.html");
				return;
			}
			// enter id/pass. logging in is on your own.
			InjectJS(wv, String.format("enterIDPASS('%s', '%s');", masterid, masterpass));
		} else if (status.compareTo("add_mycard")==0) {
			if (!islogined) {
				showJSAlert(wv, "로그인을 먼저 해 주세요");
			} else if (url.indexOf("p.eagate.573.jp/gate/p/login.html") >= 0) {
				// for preventing infinite load, we won't do anything in login page
			} else {
				// check current page
				if (url.indexOf("p.eagate.573.jp/gate/p/eamusement/attach/index.html") < 0) {
					wv.loadUrl("https://p.eagate.573.jp/gate/p/eamusement/attach/index.html");
					return;
				}
				InjectJS(wv, String.format("addCard('%s', '%s', 0);", mycardid, mycardpass));
			}
		} else if (status.compareTo("remove_card")==0) {
			if (!islogined) {
				showJSAlert(wv, "로그인을 먼저 해 주세요");
			} else if (url.indexOf("p.eagate.573.jp/gate/p/login.html") >= 0) {
				// for preventing infinite load, we won't do anything in login page
			} else {
				// check current page
				if (url.indexOf("p.eagate.573.jp/gate/p/eamusement/detach/setting1.html") < 0) {
					wv.loadUrl("http://p.eagate.573.jp/gate/p/eamusement/detach/setting1.html?ucdto="
							+ mycardid);
					return;
				}
				InjectJS(wv, "removeCard();");
			}
		} else if (status.compareTo("use_mastercard")==0) {
			if (!islogined) {
				showJSAlert(wv, "로그인을 먼저 해 주세요");
			} else if (url.indexOf("p.eagate.573.jp/gate/p/login.html") >= 0) {
				// for preventing infinite load, we won't do anything in login page
			} else {
				// check current page
				if (url.indexOf("p.eagate.573.jp/gate/p/eamusement/change/index.html") < 0) {
					wv.loadUrl("http://p.eagate.573.jp/gate/p/eamusement/change/index.html");
					return;
				}
				InjectJS(wv, String.format("useCard('%s');", mastercardid));
			}
		} else if (status.compareTo("iidxme")==0) {
			if (!islogined) {
				showJSAlert(wv, "로그인을 먼저 해 주세요");
			} else if (url.indexOf("p.eagate.573.jp/gate/p/login.html") >= 0) {
				// for preventing infinite load, we won't do anything in login page
			} else {
				// check current page
				if (url.indexOf("p.eagate.573.jp/game/2dx/23") < 0) {
					wv.loadUrl("http://p.eagate.573.jp/game/2dx/23");
					return;
				}
				InjectJS(wv, String.format("iidxme('%s', '%s');", iidxmeid, iidxmepass));
			}
		} 
		status = "";
	}
	
	public static void showJSAlert(WebView wv, String message) {
		Log.d("webview_test", String.format("alert('%s');", message));
		InjectJS(wv, String.format("alert('%s');", message));
	}
	
	/* common - about js */
	
	public static void InjectJS(WebView wv, String script) {
        wv.evaluateJavascript(script, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String value) {
				if (value.indexOf("logined_true")>=0) {
					setLoginStatus(true);
				} else if (value.indexOf("logined_false")>=0) {
					setLoginStatus(false);
				}
			}
		});
	}
	
	public static void InjectJSFile(Context c, WebView wv, String assetpath) {
        InputStream input;
        try {
           input = c.getAssets().open(assetpath);
           byte[] buffer = new byte[input.available()];
           input.read(buffer);
           input.close();
           String code = new String(buffer);
           
           InjectJS(wv, code);
        } catch (IOException e) {
           e.printStackTrace();
        }
		
	}
}

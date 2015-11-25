package com.kuna.iidxme;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainview);
		
		WebView wView = (WebView)findViewById(R.id.webview);
		wView.loadUrl("file:///android_asset/help.html");
	}
}

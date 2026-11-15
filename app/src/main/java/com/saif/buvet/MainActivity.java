package com.saif.buvet;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** One screen: a WebView showing the shop page bundled in assets. Works offline. */
public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // the page keeps the shop's data in localStorage
        s.setDatabaseEnabled(true);
        web.setWebViewClient(new WebViewClient());
        setContentView(web);
        if (state == null) {
            web.loadUrl("file:///android_asset/index.html");
        } else {
            web.restoreState(state);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    /** Back closes an open sheet inside the page first; otherwise it sends the app to the background. */
    @Override
    public void onBackPressed() {
        String js = "(function(){if(typeof sheet!=='undefined'&&sheet){closeSheet();return 'closed';}return 'none';})()";
        web.evaluateJavascript(js, value -> {
            if (value == null || !value.contains("closed")) {
                moveTaskToBack(true);
            }
        });
    }
}

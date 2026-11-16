package com.saif.minimarkeet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        if (state == null) {
            web.loadUrl("file:///android_asset/index.html");
        } else {
            web.restoreState(state);
        }
    }

    /** What the page can ask the phone to do. */
    private class Bridge {
        /** Opens the camera to read a barcode; the result comes back through onScan() in the page. */
        @JavascriptInterface
        public void scan() {
            runOnUiThread(() -> {
                IntentIntegrator i = new IntentIntegrator(MainActivity.this);
                i.setCaptureActivity(ScanActivity.class);
                i.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                i.setPrompt("");
                i.setBeepEnabled(true);
                i.setOrientationLocked(true);
                i.initiateScan();
            });
        }

        /** Hands the backup text to another app (messages, mail, notes). */
        @JavascriptInterface
        public void share(String text) {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, "Saif Mini Markeet");
            send.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(send, "Saif Mini Markeet"));
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        IntentResult r = IntentIntegrator.parseActivityResult(request, result, data);
        if (r != null) {
            if (r.getContents() != null) {
                // keep only characters a barcode can hold, so the text is safe inside the script call
                String code = r.getContents().replaceAll("[^A-Za-z0-9\\-]", "");
                web.evaluateJavascript("onScan('" + code + "')", null);
            }
            return;
        }
        super.onActivityResult(request, result, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    /** Back closes an open sheet or returns to the first tab; otherwise it sends the app to the background. */
    @Override
    public void onBackPressed() {
        web.evaluateJavascript("(function(){return typeof appBack==='function'?appBack():'none'})()", value -> {
            if (value == null || !value.contains("closed")) {
                moveTaskToBack(true);
            }
        });
    }
}

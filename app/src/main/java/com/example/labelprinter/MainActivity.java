package com.example.labelprinter;

import android.app.Activity;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        setContentView(web);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void print() { runOnUiThread(() -> doPrint()); }
        }, "AndroidPrint");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView v, String url) {
                v.evaluateJavascript("window.print=function(){AndroidPrint.print()};", null);
            }
        });
        web.loadUrl("file:///android_asset/index.html");
    }

    private void doPrint() {
        PrintManager pm = (PrintManager) getSystemService(PRINT_SERVICE);
        PrintAttributes attrs = new PrintAttributes.Builder()
            .setMediaSize(new PrintAttributes.MediaSize("LBL55X30", "Label 55x30", 2165, 1181))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build();
        pm.print("Label", web.createPrintDocumentAdapter("Label"), attrs);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}

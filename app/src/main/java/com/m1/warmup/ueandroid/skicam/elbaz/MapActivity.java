package com.m1.warmup.ueandroid.skicam.elbaz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MapActivity extends AppCompatActivity {

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        setContentView(R.layout.activity_map);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("https://www.google.com/maps/search/" + bundle.get("coord").toString());
    }
}

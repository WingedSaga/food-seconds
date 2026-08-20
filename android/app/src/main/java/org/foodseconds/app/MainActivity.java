package org.foodseconds.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public final class MainActivity extends Activity {
  private static final String HOME_URL = "https://grammar-helicopter-elect-necklace.trycloudflare.com/";
  private static final int GREEN = Color.rgb(47, 139, 69);
  private WebView webView;
  private ProgressBar progress;
  private LinearLayout errorPanel;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    buildScreen();
    configureWebView();
    loadHome();
  }

  private void buildScreen() {
    FrameLayout root = new FrameLayout(this);
    root.setBackgroundColor(Color.rgb(245, 251, 245));
    webView = new WebView(this);
    webView.setBackgroundColor(Color.rgb(245, 251, 245));
    root.addView(webView, new FrameLayout.LayoutParams(-1, -1));

    progress = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
    root.addView(progress, new FrameLayout.LayoutParams(96, 96, Gravity.CENTER));

    errorPanel = new LinearLayout(this);
    errorPanel.setOrientation(LinearLayout.VERTICAL);
    errorPanel.setGravity(Gravity.CENTER);
    errorPanel.setPadding(48, 48, 48, 48);
    errorPanel.setVisibility(View.GONE);
    TextView title = new TextView(this);
    title.setText("Не удалось открыть Еду секунды");
    title.setTextColor(Color.rgb(23, 53, 31));
    title.setTextSize(24);
    title.setGravity(Gravity.CENTER);
    TextView text = new TextView(this);
    text.setText("Проверьте интернет и нажмите кнопку ниже.");
    text.setTextColor(Color.rgb(65, 91, 69));
    text.setTextSize(17);
    text.setGravity(Gravity.CENTER);
    text.setPadding(0, 18, 0, 30);
    Button retry = new Button(this);
    retry.setText("Повторить");
    retry.setTextColor(Color.WHITE);
    retry.setTextSize(18);
    retry.setBackgroundColor(GREEN);
    retry.setOnClickListener(view -> loadHome());
    errorPanel.addView(title, new LinearLayout.LayoutParams(-1, -2));
    errorPanel.addView(text, new LinearLayout.LayoutParams(-1, -2));
    errorPanel.addView(retry, new LinearLayout.LayoutParams(-1, -2));
    root.addView(errorPanel, new FrameLayout.LayoutParams(-1, -1));
    setContentView(root);
  }

  private void configureWebView() {
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
    settings.setAllowFileAccess(false);
    settings.setAllowContentAccess(false);
    settings.setMediaPlaybackRequiresUserGesture(true);
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        errorPanel.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
      }
      @Override public void onPageFinished(WebView view, String url) { progress.setVisibility(View.GONE); }
      @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        if ("https".equals(uri.getScheme()) && isTrustedHost(uri.getHost())) return false;
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
        return true;
      }
      @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (request.isForMainFrame()) showError();
      }
      @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) { handler.cancel(); showError(); }
    });
  }

  private void loadHome() {
    errorPanel.setVisibility(View.GONE);
    progress.setVisibility(View.VISIBLE);
    webView.clearCache(false);
    webView.loadUrl(HOME_URL);
  }
  private void showError() { progress.setVisibility(View.GONE); errorPanel.setVisibility(View.VISIBLE); }
  @Override public void onBackPressed() { if (webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
  private static boolean isTrustedHost(String host) { return "grammar-helicopter-elect-necklace.trycloudflare.com".equals(host); }
}

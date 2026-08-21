package org.foodseconds.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
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
  private TextView errorText;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private boolean pageReady;
  private final Runnable timeout = () -> {
    if (!pageReady) showError("Сайт отвечает слишком долго. Проверьте интернет и попробуйте ещё раз.");
  };

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
    errorText = new TextView(this);
    errorText.setTextColor(Color.rgb(65, 91, 69));
    errorText.setTextSize(17);
    errorText.setGravity(Gravity.CENTER);
    errorText.setPadding(0, 18, 0, 16);
    Button retry = new Button(this);
    retry.setText("Повторить");
    retry.setTextColor(Color.WHITE);
    retry.setTextSize(18);
    retry.setBackgroundColor(GREEN);
    retry.setOnClickListener(view -> loadHome());
    Button browser = new Button(this);
    browser.setText("Открыть в браузере");
    browser.setTextSize(16);
    browser.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(HOME_URL))));
    errorPanel.addView(title, new LinearLayout.LayoutParams(-1, -2));
    errorPanel.addView(errorText, new LinearLayout.LayoutParams(-1, -2));
    errorPanel.addView(retry, new LinearLayout.LayoutParams(-1, -2));
    errorPanel.addView(browser, new LinearLayout.LayoutParams(-1, -2));
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
    webView.setWebChromeClient(new WebChromeClient());
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        pageReady = false;
        handler.removeCallbacks(timeout);
        handler.postDelayed(timeout, 15000);
        errorPanel.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
      }
      @Override public void onPageFinished(WebView view, String url) {
        // PageFinished only means HTML arrived. It does not mean that the React app rendered.
        handler.postDelayed(() -> view.evaluateJavascript(
          "(function(){var r=document.getElementById('root');return !!(r&&r.textContent&&r.textContent.trim().length>0)})()",
          value -> { if ("true".equals(value)) showPage(); else showError("Сайт загрузился не полностью. Нажмите «Повторить»."); }
        ), 1200);
      }
      @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        if ("https".equals(uri.getScheme()) && isTrustedHost(uri.getHost())) return false;
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
        return true;
      }
      @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (request.isForMainFrame()) showError("Нет соединения с сайтом. Проверьте интернет.");
      }
      @Override public void onReceivedHttpError(WebView view, WebResourceRequest request, android.webkit.WebResourceResponse response) {
        if (request.isForMainFrame() && response.getStatusCode() >= 400) showError("Сервер временно недоступен. Повторите попытку позже.");
      }
      @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) { handler.cancel(); showError("Не удалось проверить защищённое соединение с сайтом."); }
      @Override public boolean onRenderProcessGone(WebView view, android.webkit.RenderProcessGoneDetail detail) { showError("Страница была перезапущена системой. Нажмите «Повторить»."); return true; }
    });
  }

  private void loadHome() {
    pageReady = false;
    handler.removeCallbacks(timeout);
    errorPanel.setVisibility(View.GONE);
    progress.setVisibility(View.VISIBLE);
    webView.clearCache(false);
    webView.loadUrl(HOME_URL);
  }
  private void showPage() { pageReady = true; handler.removeCallbacks(timeout); progress.setVisibility(View.GONE); errorPanel.setVisibility(View.GONE); }
  private void showError(String message) { pageReady = false; handler.removeCallbacks(timeout); progress.setVisibility(View.GONE); errorText.setText(message); errorPanel.setVisibility(View.VISIBLE); }
  @Override protected void onDestroy() { handler.removeCallbacksAndMessages(null); webView.destroy(); super.onDestroy(); }
  @Override public void onBackPressed() { if (webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
  private static boolean isTrustedHost(String host) { return "grammar-helicopter-elect-necklace.trycloudflare.com".equals(host); }
}

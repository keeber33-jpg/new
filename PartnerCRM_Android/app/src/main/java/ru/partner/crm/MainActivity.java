package ru.partner.crm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.Toast;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final int REQUEST_SAVE_FILE = 4101;
    private static final int REQUEST_OPEN_FILE = 4102;
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private byte[] pendingBytes;
    private String pendingMime = "application/octet-stream";
    private String pendingName = "crm-export.json";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(23,43,77));
        getWindow().setNavigationBarColor(android.graphics.Color.BLACK);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                try { startActivityForResult(intent, REQUEST_OPEN_FILE); }
                catch (Exception e) { filePathCallback = null; callback.onReceiveValue(null); }
                return true;
            }
        });
        if (savedInstanceState == null) webView.loadUrl("file:///android_asset/index.html");
        else webView.restoreState(savedInstanceState);
    }

    private class AndroidBridge {
        @JavascriptInterface public void saveFile(String base64, String filename, String mime) {
            try {
                pendingBytes = Base64.decode(base64, Base64.DEFAULT);
                pendingName = (filename == null || filename.trim().isEmpty()) ? "crm-export" : filename;
                pendingMime = (mime == null || mime.trim().isEmpty()) ? "application/octet-stream" : mime;
                runOnUiThread(() -> {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(pendingMime);
                    intent.putExtra(Intent.EXTRA_TITLE, pendingName);
                    startActivityForResult(intent, REQUEST_SAVE_FILE);
                });
            } catch (Exception e) { runOnUiThread(() -> Toast.makeText(MainActivity.this, "Не удалось подготовить файл", Toast.LENGTH_LONG).show()); }
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_FILE) {
            if (filePathCallback != null) {
                Uri[] result = (resultCode == RESULT_OK && data != null && data.getData() != null) ? new Uri[]{data.getData()} : null;
                filePathCallback.onReceiveValue(result);
                filePathCallback = null;
            }
        } else if (requestCode == REQUEST_SAVE_FILE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null && pendingBytes != null) {
                try (OutputStream out = getContentResolver().openOutputStream(data.getData())) {
                    if (out != null) out.write(pendingBytes);
                    Toast.makeText(this, "Файл сохранён", Toast.LENGTH_SHORT).show();
                } catch (Exception e) { Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_LONG).show(); }
            }
            pendingBytes = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        if (webView != null) { webView.removeJavascriptInterface("AndroidBridge"); webView.destroy(); }
        super.onDestroy();
    }
}

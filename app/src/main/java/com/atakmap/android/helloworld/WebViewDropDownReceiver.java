
package com.atakmap.android.helloworld;

import com.atakmap.android.helloworld.plugin.R;
import com.atakmap.android.maps.MapView;

import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.ConsoleMessage;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;

import com.atakmap.coremap.log.Log;
import com.atakmap.android.helloworld.plugin.R;
import com.atakmap.android.maps.MapView;

// Import OkHttp
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class WebViewDropDownReceiver extends DropDownReceiver implements
        OnStateListener {
    public static final String SHOW_WEBVIEW = "helloworld.example.webview";
    public static final String TAG = "WebViewDropDownReceiver";

    final Context pluginContext;
    final Context appContext;

    private WebView htmlViewer;

    private final LinearLayout ll;

    private String sendPostRequest(String url, String jsonPayload) {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Define MediaType for JSON
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        // Create RequestBody
        RequestBody body = RequestBody.create(jsonPayload, MEDIA_TYPE_JSON);

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Execute the request and fetch the response
        try (Response response = client.newCall(request).execute()) {
            return response.body().string(); // Convert response to String
        } catch (IOException e) {
            Log.e(TAG, "Error during HTTP POST request", e);
            return null; // In case of error, return null or handle it as needed
        }
    }

    // Example usage of the method
    public void exampleUsage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://192.168.1.202:8000/drive";
                String jsonPayload = "{\"x\":\"1\", \"y\":\"1\" }";

                String response = sendPostRequest(url, jsonPayload);
                String url1 = "http://apr9.ngrok.app/drive";
                String url2 = "http://orin.local:8000";
                sendPostRequest(url1, jsonPayload);
                sendPostRequest(url2, jsonPayload);

                Log.d(TAG, "Response from server: " + response);
            }
        }).start();
    }

    public WebViewDropDownReceiver(final MapView mapView,
            final Context context) {
        super(mapView);
        this.pluginContext = context;
        this.appContext = mapView.getContext();
        LayoutInflater inflater = LayoutInflater.from(pluginContext);
        ll = (LinearLayout) inflater.inflate(R.layout.blank_linearlayout, null);
        exampleUsage();
        return;
        // must be created using the application context otherwise this will fail
//        mapView.post(new Runnable() {
//            @Override
//            public void run() {
//
//                htmlViewer = new WebView(mapView.getContext());
//                htmlViewer.setVerticalScrollBarEnabled(true);
//                htmlViewer.setHorizontalScrollBarEnabled(true);
//
//                WebSettings webSettings = htmlViewer.getSettings();
//
//                // do not enable per security guidelines
//                //webSettings.setAllowFileAccessFromFileURLs(true);
//                //webSettings.setAllowUniversalAccessFromFileURLs(true);
//
//                webSettings.setBuiltInZoomControls(true);
//                webSettings.setDisplayZoomControls(false);
//                webSettings.setJavaScriptEnabled(true);
//                webSettings.setDomStorageEnabled(true);
//                webSettings.setAllowContentAccess(true);
//                webSettings.setDatabaseEnabled(true);
//                webSettings.setGeolocationEnabled(true);
//                htmlViewer.setWebChromeClient(new ChromeClient());
//
//                // cause subsequent calls to loadData not to fail - without this
//                // the web view would remain inconsistent on subsequent concurrent opens
//                htmlViewer.loadUrl("about:blank");
//                htmlViewer.setWebViewClient(new Client());
//
//                htmlViewer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                        LayoutParams.MATCH_PARENT));
//                ll.addView(htmlViewer);
//            }
//        });

    }

    public static class Client extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "started retrieving: " + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverride: " + url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "ended retrieving: " + url);
            super.onPageFinished(view, url);
        }

    }

    private static class ChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d(TAG, consoleMessage.message() + " -- From line "
                    + consoleMessage.lineNumber() + " of "
                    + consoleMessage.sourceId());
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.d(TAG, "loading progress: " + newProgress);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(SHOW_WEBVIEW)) {
            exampleUsage();
            showDropDown(ll, HALF_WIDTH, FULL_HEIGHT,
                    FULL_WIDTH, .7, false, this);
            this.htmlViewer.loadUrl("about:blank");
            //this.htmlViewer.loadUrl("http://192.168.43.174:3000");
            //https://80e6-4-17-224-160.ngrok-free.app
            //this.htmlViewer.loadUrl("https://adnan-thinkpad-x1-carbon-gen-11.jerboa-kokanue.ts.net/");
            //this.htmlViewer.loadUrl("https://adnan-thinkpad-x1-carbon-gen-11.jerboa-kokanue.ts.net/"); //this works
            this.htmlViewer.loadUrl("https://apr9.ngrok.app"); //this works


            this.htmlViewer.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    Log.d("MyApplication", consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId());
                    return super.onConsoleMessage(consoleMessage);
                }
            });
        }
    }

    @Override
    public void disposeImpl() {
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }
}

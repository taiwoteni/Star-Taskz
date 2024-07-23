package com.theteam.taskz.presentation.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.theteam.taskz.R;
import com.theteam.taskz.domain.repositories.Jira;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JiraWebView extends AppCompatActivity {
    private static final String AUTHORIZATION_URL = "https://auth.atlassian.com/authorize";
    private static final String CLIENT_ID = Jira.jiraInt;
    private static final String REDIRECT_URI = "https://star-taskz.vercel.app/";
    private static final String RESPONSE_TYPE = "code";
    private static final String SCOPE = "read:jira-work read:jira-user";
    private static final String AUTH_URL = "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=aG2HygzaXKij4bKXAdcQppsUVSjzWEuO&scope=read%3Ajira-work%20read%3Ajira-user&redirect_uri=https%3A%2F%2Fstar-taskz.vercel.app%2F&state=sTaR_TaSkZ30_May&response_type=code&prompt=consent";
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.webview_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true);  // Enable DOM storage
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Pixel 3 XL) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Mobile Safari/537.36");


        // Set a WebViewClient to handle the redirect
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("API_RESPONSE", "Page finished loading: " + url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri.toString().startsWith("https://star-taskz.vercel.app")) {
                    handleRedirectUri(uri);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        // Load the GitHub authorization URL in the WebView
        String authUrl = AUTHORIZATION_URL + "?response_type=" + RESPONSE_TYPE +
                "&client_id=" + CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&state=sTaRtAsKz" +
                "&scope=" + SCOPE +
                "&prompt=consent";
        Log.v("API_RESPONSE", authUrl);
        webView.loadUrl(authUrl);
    }
    @SuppressLint("SetTextI18n")
    private void handleRedirectUri(Uri uri) {
        String code = uri.getQueryParameter("code");
        if (code != null) {
            // Exchange the code for an access token
            exchangeCodeForToken(code);
        } else {
            // Handle error or deny access
            webView.loadData("<html><body>Authorization failed</body></html>", "text/html", "UTF-8");
        }
    }

    private void exchangeCodeForToken(String code) {
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grant_type","authorization_code");
            jsonObject.put("client_id", Jira.jiraInt);
            jsonObject.put("client_secret", Jira.jiraString);
            jsonObject.put("code",code);
            jsonObject.put("redirect_uri","https://star-taskz.vercel.app/");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final Response.Listener<JSONObject> response = jsonObject1 -> {
            Log.v("API_RESPONSE", "Got token successfully");
            Log.v("API_RESPONSE", jsonObject1.toString());


            try {
                final Intent intent = new Intent();
                String accessToken = jsonObject1.getString("access_token");
                intent.putExtra("token",accessToken);
                setResult(RESULT_OK, intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        final Response.ErrorListener error = volleyError -> {
            Log.v("API_RESPONSE", volleyError.toString());
        };

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://auth.atlassian.com/oauth/token/",
                jsonObject,
                response,
                error
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header = new HashMap<>();
                header.put("Accept", "application/json");
                return header;
            }
        };

        queue.add(request);



    }
}
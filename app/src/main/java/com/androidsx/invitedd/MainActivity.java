package com.androidsx.invitedd;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private WebView webView;
    private ProgressBar progressBar;

    class InviteddUrlBuilder {
        private static final String INVITEDD_URL = "http://www.invitedd.com/";

        private Locale locale;

        InviteddUrlBuilder locale(Locale locale) {
            this.locale = locale;
            return this;
        }
        String build() {
            String language = ("es".equals(locale.getLanguage())) ? "es" : "en";
            return INVITEDD_URL + language + "?cont=androidapp";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_actionbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        configureWebview();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getString(R.string.support_email), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Invited user");
            startActivity(Intent.createChooser(emailIntent, "Send support email"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void configureWebview() {
        final String url = new InviteddUrlBuilder().locale(Locale.getDefault()).build();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setUseWideViewPort(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                Log.e(TAG, "Error loading the url. Error code: " + errorCode);

                findViewById(R.id.error_msg).setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // remove icon & language
                if (url.startsWith(url)) {
                    webView.loadUrl("javascript:$('nav').first().remove()");

                    // timer as it takes some time to render the changes
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        progressBar.setVisibility(View.INVISIBLE);

                                        YoYo.with(Techniques.FadeIn)
                                                .duration(800)
                                                .playOn(webView);
                                        webView.setVisibility(View.VISIBLE);
                                    } catch (Exception e) {
                                        // in case the user ran out
                                    }
                                }
                            });
                        }
                    }, 200);
                }
            }
        });
        webView.loadUrl(url);
        webView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

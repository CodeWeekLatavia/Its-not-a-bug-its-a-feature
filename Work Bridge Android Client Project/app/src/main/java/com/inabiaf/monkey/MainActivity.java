package com.inabiaf.monkey;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.commons.text.StringEscapeUtils;

public class MainActivity extends AppCompatActivity {

    public WebView webView;
    public Context mainActivity;
    public ConnectTask connectTask;

    public String email = "";
    public String city = "";
    public String interests = "";
    public String about = "";
    public String name = "";
    public String surname = "";
    public boolean isLoggedIn = false;
    public boolean isWorker;
    public boolean finishReg;


    //JOB offer variables
    public String base64Image = "";
    public String description = "";
    public String location = "";
    public String pay = "";

    public boolean mainPage = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        webView = (WebView) findViewById(R.id.webView);

        connectTask = new ConnectTask();
        connectTask.main = this;
        connectTask.execute("");

        webView.setWebViewClient(new WebViewClient(){

            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Do something
                ShowNoInternetConnectionAlert("Can't connect to web server!");

            }


            public String boolToStr(boolean b)
            {
                if(b) return "true";
                return "false";
            }

            public void onPageFinished(WebView view, String url) {
                if(url.endsWith("index"))
                {
                    Log.i("MONKEY","Main page loaded");
                    mainPage = true;
                    String payload ="javascript:onLoggedInResult("+boolToStr(isLoggedIn)+")";
                    Log.i("MONKEY",payload);
                    loadUrl(payload);
                }
                else if(url.endsWith("login"))
                {
                    Log.i("MONKEY","login page loaded");
                    mainPage = false;
                }
                else if(url.endsWith("register"))
                {
                    Log.i("MONKEY","register page loaded");
                    mainPage = false;
                }
                else if(url.endsWith("profile"))
                {
                    Log.i("MONKEY","profile page loaded");
                    mainPage = false;
                    if(about.equals("null")) about = "";
                    if(interests.equals("null")) interests = "";

                    String payload = "javascript:onProfileDataResult(\""+name+"\",\""+surname+"\",\""+email+"\",\""+city+"\",\""+about+"\",\""+interests+"\","+boolToStr(isWorker)+")";
                    Log.i("MONKEY",payload);
                    loadUrl(payload);
                }
                else if (url.endsWith("offer"))
                {
                    Log.i("MONKEY","job offer page loaded");
                    mainPage = false;
                    String payload = "javascript:onJobOfferResult(\""+description+"\",\""+location+"\",\""+pay+"\",\""+ StringEscapeUtils.escapeJava(base64Image)+"\")";

                    //document.getElementById("image").src = base64Image;
                    Log.i("MONKEY",payload);
                    loadUrl(payload);
                }
            }
        });
        webView.setInitialScale(80);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new JavaScriptInterface(connectTask, this),"Monkey");
        webView.loadUrl("https://codeweeklatvia.github.io/Its-not-a-bug-its-a-feature/index");
    }

   @Override
   public void onBackPressed() {
        Log.d("MONKEY", "[BackButton] onBackPressed: Back button pressed!");
    }
    public void WarnUser(final String title, final String message)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {

                new AlertDialog.Builder(mainActivity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    public void ShowNoInternetConnectionAlert(final String title)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                //webView.loadData("","text/plain","UTF-8");
                new AlertDialog.Builder(mainActivity)
                        .setTitle(title)
                        .setMessage("Do you want to try connecting again?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public void loadUrl(final String url)
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

    public boolean isInternetAvailable() {
        ConnectivityManager con_manager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }



}


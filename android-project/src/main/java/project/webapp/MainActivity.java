package project.webapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.java_websocket.server.WebSocketServer;
import org.nanohttpd.protocols.http.NanoHTTPD;
import project.xplat.launcher.AssetsCopy;
import project.xplat.launcher.pxprpcapi.ApiServer;
import xplatj.gdxconfig.core.PlatCoreConfig;

import java.io.IOException;
import java.util.TreeMap;

public class MainActivity extends Activity {
    void bgThread(){
        if(ApiServer.defaultAndroidContext==null){
            ApiServer.funcMap=new TreeMap<String, Object>();
            ApiServer.defaultAndroidContext=this.getApplicationContext();
            ApiServer srv = new ApiServer();
            try {
                srv.start();
            } catch (IOException e) {
            }
        }
    }
    static NanoHTTPD httpd;
    void initWebServer(){
        try {
            if(httpd==null){
                Log.d("webapp","create new web server.");
                httpd=new NanoHTTPD() {
                };
                wss.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://www.baidu.com");
        intent.setData(content_url);
        startActivity(intent);
    }

    protected WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        PlatCoreConfig.get().executor.execute(
            new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.bgThread();
                }
            }
        );

    }

    protected void initWebView(){
        mWebView=new WebView(this);
        setContentView(mWebView);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.loadUrl("file://"+ AssetsCopy.assetsDir+"/index.html");
    }

    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wss.stop();
                    wss=null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        super.onDestroy();
    }
}

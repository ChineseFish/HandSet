package com.tongda.ccb_direct_bank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ccb.js.CcbAndroidJsInterface;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;


/**
 * create by wutw on ${date}
 */
public class CcbWebViewActivity extends Activity implements View.OnClickListener{
    String TAG = "CcbWebViewActivity";
    private WebView myWebView = null;
    /**  保存WEBVIEW cookie*/
    CookieStore cookieStore = new BasicCookieStore();
    //初始化JS键盘对象
    WebAppInterface webAppInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ccb_direct_bank_activity_web_view);

        myWebView = (WebView) findViewById(R.id.webView);
        findViewById(R.id.back).setOnClickListener(this);
        initWebView();
    }

    private void initWebView(){
        //获取当前Activity的Layout
        LinearLayout mainActivityLayout = (LinearLayout)findViewById(R.id.mainActivity);
        //初始化JS键盘对象，传入当前Activity的this对象
        webAppInterface = new WebAppInterface(CcbWebViewActivity.this);
        // 得到设置属性的对象
        WebSettings webSettings = myWebView.getSettings();
        // 使能JavaScript
        webSettings.setJavaScriptEnabled(true);

        // 支持中文，否则页面中中文显示乱码
        // 设置字符集编码
        webSettings.setDefaultTextEncodingName("UTF-8");
        // 设置响应JS
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setBuiltInZoomControls(true);// 设置缩放工具
        webSettings.setDisplayZoomControls(false);// 不显示webview缩放按钮
        webSettings.setSupportZoom(true);// 设置支持缩放
        webSettings.setDefaultFontSize(18);//设置字体大小

        // 网页自适应大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setTextZoom(100);

        // 禁用 file 协议；
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        //Http和Https混合问题
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/
        myWebView.setHorizontalScrollBarEnabled(false);//禁止水平滚动
        myWebView.setVerticalScrollBarEnabled(true);//允许垂直滚动

        // 限制在WebView中打开网页，而不用默认浏览器
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String cookiesString = CookieManager.getInstance().getCookie(url);
                if(null!=cookiesString){
                    Log.i(TAG+"polling ",cookiesString);
                    String []cookies = cookiesString.split(";");
                    for(String cookie:cookies){
                        String []cook = cookie.split("=");
                        if(null!=cook&&cook.length>1){
                            Cookie cookie1 = new BasicClientCookie(cook[0],cook[1]);
                            cookieStore.addCookie(cookie1);
                        }
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG,"polling:shouldOverrideUrlLoading");
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG,"polling:shouldOverrideUrlLoading request");
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        // 如果不设置这个，JS代码中的按钮会显示，但是按下去却不弹出对话框
        // Sets the chrome handler. This is an implementation of WebChromeClient
        // for use in handling JavaScript dialogs, favicons, titles, and the
        // progress. This will replace the current handler.
        myWebView.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result)
            {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }



        });


        myWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView,true);
        }
        // 用JavaScript调用Android函数：
        // 先建立桥梁类，将要调用的Android代码写入桥梁类的public函数
        // 绑定桥梁类和WebView中运行的JavaScript代码
        // 将一个对象起一个别名传入，在JS代码中用这个别名代替这个对象，可以调用这个对象的一些方法CcbAndroidJsInterface.CCB_JS_OBJECT
        myWebView.addJavascriptInterface(webAppInterface, CcbAndroidJsInterface.CCB_JS_OBJECT);
        // 载入页面
        if(!TextUtils.isEmpty(getIntent().getStringExtra("url")))
            myWebView.loadUrl(getIntent().getStringExtra("url"));

        myWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        myWebView.removeJavascriptInterface("accessibility");
        myWebView.removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void onClick(View v) {
        if(R.id.back == v.getId()){
            back("");
        }
    }

    /**openWebView
     * 自定义的Android代码和JavaScript代码之间的桥梁类
     *
     * @author 1
     *
     */
    public class WebAppInterface
    {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context context)
        {
            mContext = context;
        }

        /** Show a toast from the web page */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void showToast(String toast)
        {
            Toast tost =   Toast.makeText(mContext, toast, Toast.LENGTH_LONG);
            tost.show();
        }

        /*关闭页面，返回结果*/
        @JavascriptInterface
        public void closeWebView(String params)
        {
            back(params);
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void startWebView(String url)
        {
            Intent intent = new Intent(mContext, CcbWebViewActivity.class);
            intent.putExtra("url", url);
            mContext.startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        back("");
        super.onBackPressed();
    }

    private void back(String params){
        if(TextUtils.isEmpty(params))
            params = "";
        Intent intent = new Intent();
        intent.putExtra("PARAMS",params);
        setResult(4,intent);
        finish();
    }
}

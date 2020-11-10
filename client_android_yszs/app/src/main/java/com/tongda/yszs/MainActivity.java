package com.tongda.yszs;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tongda.base.Transfer;
import com.tongda.base.Utils;
import com.tongda.ccb_direct_bank.CCBMainActivity;
import com.tongda.ccb_pay.CCBPay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

import com.tongda.wechat.WechatPay;
import com.tongda.yszs.wxapi.WXEntryActivity;
import com.tongda.yszs.wxapi.WXPayEntryActivity;
import com.tongda.ali.AliPay;
import com.tongda.ums.UmsPay;
import com.tongda.debug.DragFloatActionButton;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    //
    private static MainActivity mainActivity;

    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    //
    private static WebView myWebview;

    //
    private long exitTime = 0;

    public static String detailLcId;
    public static String detailTradeno;

    //
    WebBackForwardList list = null;
    boolean isFromBack = false;

    public static void loadMainPage()
    {
        MainActivity.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myWebview.loadUrl("file:///android_asset/web/index.html");
            }
        });

    }

    public static void loadUrl(String url)
    {
        myWebview.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.ziubao_activity_main);

        //
        DragFloatActionButton mBtn = findViewById(R.id.ziubao_float_button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Transfer.startActivity(MainActivity.this, "ziubao_debug/main", new Intent());
            }
        });

        //
        if(!BuildConfig.DEBUG)
        {
            mBtn.setVisibility(View.INVISIBLE);
        }

        /**
         * init webView
         */
        myWebview = findViewById(R.id.ziubao_webview);

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // disable rotate
        myWebview.setHorizontalScrollBarEnabled(false);
        myWebview.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = myWebview.getSettings();
        // reset user agent string
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua+";ziubaoAndroid");
        //
        webSettings.setAllowFileAccess(true);
        // enable javascript
        webSettings.setJavaScriptEnabled(true);
        // add javascript to android native interface
        myWebview.addJavascriptInterface(new InJavaScriptLocalObj(), "WeChatTongDaPay");
        // enable web cache
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // WebChromeClient is used to handle Javascript dialogs, favicons, titles, and the progress.
        myWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activity和Webview根据加载程度决定进度条的进度大小
                // 当加载到100%的时候 进度条自动消失
                MainActivity.this.setProgress(progress * 100);
            }
        });
        // WebViewClient is used fo rendering HTML
        myWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                // fetch web history
                list = view.copyBackForwardList();
                // check if this is the latest web
                isFromBack = (list.getCurrentIndex() != (list.getSize() - 1));

                // it is a outer web go back operation, try to jump back to home web
                if (isFromBack
                        && (url.startsWith("weixin://wap/pay?")
                        || url.startsWith("http://weixin/wap/pay")
                        || url.startsWith("https://weixin/wap/pay")
                        || url.startsWith("https://ibsbjstar.ccb.com.cn"))) {
                    //
                    myWebview.goBack();

                    //
                    return false;
                }

                // open tel panel
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);

                    //
                    return true;
                }

                /**
                 * weixin h5 pay
                 */
                if(WechatPay.interceptWechatPay(MainActivity.this, url))
                {
                    return true;
                }

                /**
                 * Alipay H5 支付过滤
                 */
                if(AliPay.interceptAliPay(MainActivity.this, url))
                {
                    return true;
                }

                /**
                 * ccb h5 pay
                 */
                if(CCBPay.interceptCcbPay(MainActivity.this, url))
                {
                    return true;
                }

                /**
                 * 建行惠懂你h5请求打开外部app
                 */
                if(url.startsWith("leye:")) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));

                        //
                        startActivity(intent);
                    } catch (Exception e) {
                        //
                        Toast.makeText(MainActivity.this, "打开客户端失败，请安装后重试。", Toast.LENGTH_SHORT).show();
                    }

                    //
                    return true;
                }
                if(url.contains("https://static.leye.ccb.com/static/leyeapp/app-hdn.apk"))
                {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));

                        //
                        startActivity(intent);
                    } catch (Exception e) {
                        //
                        Toast.makeText(MainActivity.this, "下载惠懂你失败，请安装后重试。", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }

                //
                Map<String, String> extraHeaders = new HashMap<>();
                extraHeaders.put("Referer", "http://dh.zsghtky.com/");
                view.loadUrl(url, extraHeaders);

                //
                return true;
            }
        });

        //
        CCBPay.init(MainActivity.this, myWebview);

        //
        WXEntryActivity.init(myWebview);

        //
        WXPayEntryActivity.init(myWebview);

        //
        com.tongda.base.Constants.init(MainActivity.this);

        //
        UmsPay.init(myWebview);

        //
        if(BuildConfig.DEBUG)
        {
            myWebview.loadUrl(com.tongda.base.Constants.Home_Page);
        }
        else
        {
            loadMainPage();
        }
        /**
         * 获取权限
         */
        initPermission();

        /**
         * 检测版本更新
         */
        Utils.checkApkUpdate(this);
    }

    /**
     * 手动申请权限（特定的权限只能在app内部申请）
     */
    private void initPermission() {
        String[] perms =
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES
                };

        //
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // 第二个参数是被拒绝后再次申请该权限的解释
            // 第三个参数是请求码
            // 第四个参数是要申请的权限
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myWebview != null) {
            myWebview.removeAllViews();

            //
            try {
                myWebview.destroy();
            } catch (Throwable t) {

            }

            //
            myWebview = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // some mobile phone has backup item which needed to be handled
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // locate at main page
            if ((myWebview.getUrl().equals(com.tongda.base.Constants.Home_Page) || myWebview.getUrl().equals("http://m.ziubao.com/index.htm"))
                    && (System.currentTimeMillis() - exitTime) > 2000) {
                //
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();

                //
                exitTime = System.currentTimeMillis();
            } else {
                // locate at child page
                if (!myWebview.getUrl().equals(com.tongda.base.Constants.Home_Page)
                        && !myWebview.getUrl().equals("http://m.ziubao.com/index.htm")) {

                    // at cancel page, direct jump to main page
                    if (myWebview.getUrl().equals(Constants.OrderDetail + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))
                            || myWebview.getUrl().equals(Constants.OrderDetailBf + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))
                            || myWebview.getUrl().equals(Constants.OrderDetailCz + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))
                            || myWebview.getUrl().equals(Constants.OrderDetailTh + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))
                            || myWebview.getUrl().equals(Constants.OrderDetailLh + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))
                            || myWebview.getUrl().equals(Constants.OrderDetailDh + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))
                            || myWebview.getUrl().equals(Constants.OrderDetailHf + "?cancel=0&id=" + (null == detailLcId ? '0' : detailLcId)
                            + "&sessionId=" + (null == detailTradeno ? '0' : detailTradeno))) {
                        myWebview.clearHistory();

                        //
                        myWebview.loadUrl(com.tongda.base.Constants.Home_Page);
                    } else if (myWebview.getUrl().equals("http://m.ziubao.com/Menu.asp")) {
                        myWebview.clearHistory();

                        //
                        myWebview.loadUrl(com.tongda.base.Constants.Home_Page);
                    } else {
                        if (myWebview.canGoBack()) {
                            // 防止微信H5支付重复调起
                            if (myWebview.getUrl().startsWith("http://dh.zsghtky.com/gt/orderddh.do")
                                    || myWebview.getUrl().startsWith("http://dt.zsghtky.com/dt/orderddh.do")) {
                                myWebview.goBackOrForward(-2);
                            } else {
                                myWebview.goBack();
                            }
                        }
                    }
                } else {
                    //
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e("app MainActivity", "获取成功的权限" + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("app MainActivit", "获取失败的权限" + perms);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //
        UmsPay.checkPayResult(MainActivity.this);

        //
        WXEntryActivity.handleWXResult(MainActivity.this);

        //
        WXPayEntryActivity.handleWXResult(MainActivity.this);
    }

    /**
     * android sdk wxApi >= 17 时需要加@JavascriptInterface
     */
    public final class InJavaScriptLocalObj {
        //
        @JavascriptInterface
        public void loadMainPage()
        {
            MainActivity.loadMainPage();
        }

        @JavascriptInterface
        public void openDirectCCB(String mobilePhone)
        {
            //
            CCBMainActivity.setMobilePhone(mobilePhone);

            //
            Transfer.startActivity(MainActivity.this, "ccbDirectBank/main", new Intent());
        }

        @JavascriptInterface
        public void checkAndroidVersion(String tradeno, String price) {
            //
            Utils.checkApkUpdate(MainActivity.this);
        }

        // 微信授权登录
        @JavascriptInterface
        public void wechatAuth(String type)
        {
            WXEntryActivity.sendAuthRequest(MainActivity.this, type);
        }

        // 微信支付
        @JavascriptInterface
        public void wechatPay(String type, String orderNo, String price)
        {
            WXEntryActivity.sendPayRequest(MainActivity.this, type, orderNo, price);
        }

        // 建行支付
        @JavascriptInterface
        public void ccbPay(String type, String originOrderNo, String orderNo, String price)
        {
            CCBPay.sendPayRequest(MainActivity.this, type, originOrderNo, orderNo, price);
        }

        // 阿里支付
        @JavascriptInterface
        public void umsAliPay(String type, String orderNo, String price)
        {
            UmsPay.sendPayRequest(MainActivity.this, type, orderNo, price);
        }

        /******** these interface will be deprecated later ********/
        // 海通客运龙支付
        @JavascriptInterface
        public void startCCBPayNative(String tradeno, String price) {
            CCBPay.sendPayRequest(MainActivity.this, "haitong",  "", tradeno, price);
        }

        // 普陀山龙支付
        @JavascriptInterface
        public void startPTSCCBPayNative(String tradeno, String price) {
            CCBPay.sendPayRequest(MainActivity.this, "putuo", "", tradeno, price);
        }

        // 特色商城龙支付
        @JavascriptInterface
        public void startShopCCBPayNative(String tradeno, String price) {
            CCBPay.sendPayRequest(MainActivity.this, "shop", "", tradeno, price);
        }
    }
}

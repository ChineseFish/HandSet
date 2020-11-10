package com.tongda.ccb_direct_bank;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tongda.base.Transfer;
import com.tongda.ccb_direct_bank.Listener.CheckPermissionListener;
import com.tongda.ccb_direct_bank.constant.HostAddress;
import com.tongda.ccb_direct_bank.controller.MainController;
import com.tongda.ccb_direct_bank.entity.FileUploadEntity;
import com.tongda.ccb_direct_bank.entity.IDCardEntity;
import com.tongda.ccb_direct_bank.entity.SecurityReq;
import com.tongda.ccb_direct_bank.entity.SecurityReqBody;
import com.tongda.ccb_direct_bank.utils.CameraApi;
import com.tongda.ccb_direct_bank.utils.DateUtils;
import com.tongda.ccb_direct_bank.utils.DeviceUtils;
import com.tongda.ccb_direct_bank.utils.EsafeUtils;
import com.tongda.ccb_direct_bank.utils.FileUtils;
import com.tongda.ccb_direct_bank.utils.JsonUtils;
import com.tongda.ccb_direct_bank.utils.LoadingDialogUtils;
import com.ccb.crypto.tp.tool.eSafeLib;
import com.ccb.js.CcbAndroidJsInterface;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tendyron.liveness.impl.LivenessInstance;
import com.tendyron.liveness.impl.LivenessInterface;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

/**
 * 没有OCR控件的直销银行
 * create by wutw on ${date}
 */
public class CCBMainActivity extends Activity implements View.OnClickListener {
    private String TAG = "CCBMainActivity";
    Context mContext;
    private WebView myWebView = null;
    //是否使用本地的测试代码
    private boolean isUseLocal = false;
    //初始化JS键盘对象
    CcbAndroidJsInterface ccbAndroidJsInterface;
    //人脸识别object
    LivenessInterface face;
    //人脸识别保存图片
    String photo="";
    //安全中心请求
    SecurityReq securityReq;
    //身份认证请求
    SecurityReqBody securityReqBody;
    //网络请求返回结果
    String result = "";
    /** 启动身份证正面扫描请求码 */
    public static final int REQUEST_CODE_IDCARD_SIDE_FRONT = 1;
    /** 启动身份证背面扫描请求码 */
    public static final int REQUEST_CODE_IDCARD_SIDE_BACK = 2;
    /** 启动银行卡扫描请求码 */
//    public static final int REQUEST_CODE_BANK_CARD = 3;
    /** 身份证数据 */
    private IDCardEntity idCardEntity;
    /** 银行卡数据 */
   /* private BankEntity bankEntity;
    byte[] imageBytesBank;*/
    /** 身份证数据 */
    byte[] imageBytesFront ;
    byte[] imageBytesBack ;
    /** 身份证头像 */
    String cardFace = "";

    /**  保存图片地址*/
    JSONObject jsonPicPath = new JSONObject();
    /**  文件上传参数*/
    FileUploadEntity fileUploadEntity;
    /**  文件名*/
    String fileNm = "";
    /**  保存WEBVIEW cookie*/
    CookieStore cookieStore = new BasicCookieStore();
    //声明并初始化对象
    eSafeLib safe;
    public static final int WEBVIEW_ACTIVITY_CODE = 4;
    //读取手机状态请求码
    private static final int READ_PHONE_STATE_CODE = 5;
    //手机相机权限请求码
    private static final int OCR_CAMERA_REQUEST_CODE = 6;
    //手机相机权限请求码
    private static final int FACE_CAMERA_REQUEST_CODE = 7;

    /** 人脸识别请求吗 */
    public static final int REQUEST_CODE_SCAN_FACE = 8;
    //ocr 临时照片
    String tempFileName;
    File tempFile;
    //适配高版本
    Uri temUri;

    //
    static private String mobilePhone = null;
    public static void setMobilePhone(String mobilePhone)
    {
        CCBMainActivity.mobilePhone = mobilePhone;
    }

    //检查权限 监听器
    CheckPermissionListener permissionListener = new CheckPermissionListener() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode){
                case READ_PHONE_STATE_CODE:
                    break;
                case FACE_CAMERA_REQUEST_CODE:
                    startfaceScan();
                    break;
                case REQUEST_CODE_IDCARD_SIDE_FRONT:
                    scanIDCard(REQUEST_CODE_IDCARD_SIDE_FRONT);
                    break;
                case REQUEST_CODE_IDCARD_SIDE_BACK:
                    scanIDCard(REQUEST_CODE_IDCARD_SIDE_BACK);
                    break;
             /*   case REQUEST_CODE_BANK_CARD:
                    scanIDCard(REQUEST_CODE_BANK_CARD);
                    break;*/
            }
        }

        @Override
        public void onPermissionDeny(int requestCode) {

        }

        @Override
        public void onPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults) {

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        face = com.tendyron.facelib.impl.IBank.getInstance(this);
        face = LivenessInstance.getInstance ();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.ccb_direct_bank_activity_main);

        //
        mContext = this;
        myWebView = (WebView) findViewById(R.id.webView);
        //获取当前Activity的Layout
        LinearLayout mainActivityLayout = (LinearLayout)findViewById(R.id.mainActivity);
        //初始化JS键盘对象，传入当前Activity的this对象
        ccbAndroidJsInterface = new WebAppInterface(mContext.getApplicationContext(), mainActivityLayout, myWebView);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        initWebView();
        requestPermission(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.READ_EXTERNAL_STORAGE},READ_PHONE_STATE_CODE);
//        DeviceUtils.getSignature(this);
    }

    private void requestPermission(String []permissions,int requestCode){
        ActivityCompat.requestPermissions(this,permissions, requestCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }
    }

    private void initWebView(){
        // 得到设置属性的对象
        WebSettings webSettings = myWebView.getSettings();
        // 使能JavaScript
        webSettings.setJavaScriptEnabled(true);

        // 支持中文，否则页面中中文显示乱码
        // 设置字符集编码
        webSettings.setDefaultTextEncodingName("UTF-8");
        //支持js调用window.open方法
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webview 支持打开多窗口
//        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setBuiltInZoomControls(true);// 设置缩放工具
        webSettings.setDisplayZoomControls(false);// 不显示webview缩放按钮
        webSettings.setSupportZoom(true);// 设置支持缩放
        webSettings.setDefaultFontSize(18);//设置字体大小


        // 网页自适应大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);

      /*  DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;

        if (mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }else if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }*/

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
                Log.i(TAG,"polling:shouldInterceptRequest request url:"+request.getUrl().toString());
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG,"polling:onPageFinished request url:"+url);
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
                  Log.i(TAG,"polling:shouldOverrideUrlLoading request url:"+url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG,"polling:shouldOverrideUrlLoading request url:"+request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.i(TAG,"polling:onReceivedSslError");
                handler.proceed();
//                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i(TAG,"polling:onReceivedError");
                super.onReceivedError(view, errorCode, description, failingUrl);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView,true);
        }

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // 用JavaScript调用Android函数：
        // 先建立桥梁类，将要调用的Android代码写入桥梁类的public函数
        // 绑定桥梁类和WebView中运行的JavaScript代码
        // 将一个对象起一个别名传入，在JS代码中用这个别名代替这个对象，可以调用这个对象的一些方法CcbAndroidJsInterface.CCB_JS_OBJECT
//        myWebView.addJavascriptInterface(ccbAndroidJsInterface,"identification");
        myWebView.addJavascriptInterface(ccbAndroidJsInterface,CcbAndroidJsInterface.CCB_JS_OBJECT);
        // 载入页面：本地html资源文件
        if(isUseLocal)
            myWebView.loadUrl("file:///android_asset/www/index.html");
        else {
            LemonHelloInfo alertInstallDialog;

            //
            if(mobilePhone != null && mobilePhone.length() > 0)
            {
                //
                final String directCCBUrl = UrlProcessor.encrypt(mobilePhone);

                //
                myWebView.loadUrl(directCCBUrl);
            }
            else
            {
                alertInstallDialog = new LemonHelloInfo()
                        .setTitle("重要提示")
                        .setContent("请先登录并绑定手机号码！")
                        .addAction(new LemonHelloAction("确定", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();

                                Transfer.startActivity(CCBMainActivity.this, "app/main", new Intent());
                            }
                        }));
                //
                alertInstallDialog.show(CCBMainActivity.this);
            }

        }
//        myWebView.postUrl(HostAddress.postUrl, EncodingUtils.getBytes(HostAddress.params, Global.DEFAULT_ENCORD));


        myWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        myWebView.removeJavascriptInterface("accessibility");
        myWebView.removeJavascriptInterface("accessibilityTraversal");
    }

    public void initPermissiont(String permission,int requestCode){
        int permissionCheck = ContextCompat.checkSelfPermission(this,permission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if(null!=permissionListener)
                permissionListener.onPermissionDeny(requestCode);
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            //TODO
            if(null!=permissionListener)
                permissionListener.onPermissionGranted(requestCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK&&myWebView.canGoBack()){
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

         if(R.id.back == v.getId()){
                finish();
        }else if(R.id.button==v.getId()){
             //检查相机权限，权限结果permissionListener进行监听，并执行相应操作
             initPermissiont(Manifest.permission.CAMERA,REQUEST_CODE_IDCARD_SIDE_FRONT);
            /*initESafe();
            initSecurityReq("{\n" +
                    "                    \"StrUsInd_2\":\"2\",\n" +
                    "                    \"Cst_Nm\":\"马零五\",\n" +
                    "                    \"Crdt_TpCd\":\"1010\",\n" +
                    "                    \"Crdt_No\":\"210250193710096711\",\n" +
                    "                    \"Cst_ID\":\"015190000103192241\",\n" +
                    "                    \"CHNL_CUST_NO\":\"210250193710096711\",\n" +
                    "                    \"Gnd_Cd\":\"\",\n" +
                    "                    \"Ethnct_Cd\":\"\",\n" +
                    "                    \"Brth_Dt\":\"\",\n" +
                    "                    \"AvlDt_Dt\":\"\",\n" +
                    "                    \"AvlDt_EdDt\":\"\",\n" +
                    "                    \"Inst_Chn_FullNm\":\"\",\n" +
                    "                    \"Dtl_Adr\":\"\",\n" +
                    "                    \"Sign_OrCd\":\"\",\n" +
                    "                    \"base64_Ecrp_Txn_Inf\":\"\",\n" +
                    "                    \"SYS_CODE\":\"0250\",\n" +
                    "                    \"BRANCHID\":\"211000000\",\n" +
                    "                    \"base64_Ecrp_Txn_Inf\":\"\"\n" +
                    "                }");
            sendSecurityReq();*/
        }


    }

    /**
     * 自定义的Android代码和JavaScript代码之间的桥梁类
     *
     * @author 1
     *
     */
    public class WebAppInterface extends CcbAndroidJsInterface
    {

        /** Instantiate the interface and set the context */
        WebAppInterface(Context context, LinearLayout activityLayout, WebView webView)
        {
            super(context, HostAddress.eSafeKey,activityLayout, webView);
        }

        /** Show a toast from the web page */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void showToast(String toast)
        {
            Toast tost =   Toast.makeText(mContext, toast, Toast.LENGTH_LONG);
            tost.show();
        }

        /** 初始化ESAFE */
        @JavascriptInterface
        public void createESafe(String json)
        {
            initESafe();
        }

        /** S人脸识别 */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void scanFace(String ...toast)
        {
            //检查相机权限，权限结果permissionListener进行监听，并执行相应操作
            initPermissiont(Manifest.permission.CAMERA,FACE_CAMERA_REQUEST_CODE);
           /* if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
                startfaceScan();
            }else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
            }*/
        }

        /* 发送网关安全请求1*/
        @JavascriptInterface
        public void requestFaceInfo(String json)
        {
            initSecurityReq(json);
            sendSecurityReq();
        }

        /* 发送网关安全请求02*/
        @JavascriptInterface
        public void requestFaceInfoZX02(String json)
        {
            initSecurityReq(json,"02");
            sendSecurityReq();
        }

        /* 返回人脸扫描结果*/
        @JavascriptInterface
        public void sendParams(final String params)
        {
            //网络请求参数
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript:scanFaceResult('"+params+" success"+"')");
                }
            });
        }

        /* 扫描身份认证请求*/
        @JavascriptInterface
        public void scanIdCardFront()
        {
            //检查相机权限，权限结果permissionListener进行监听，并执行相应操作
            initPermissiont(Manifest.permission.CAMERA,REQUEST_CODE_IDCARD_SIDE_FRONT);
//            scanIDCard(REQUEST_CODE_IDCARD_SIDE_FRONT);
        }

        /* 扫描身份认证请求*/
        @JavascriptInterface
        public void scanIdCardBack()
        {
            //检查相机权限，权限结果permissionListener进行监听，并执行相应操作
            initPermissiont(Manifest.permission.CAMERA,REQUEST_CODE_IDCARD_SIDE_BACK);
//            scanIDCard(REQUEST_CODE_IDCARD_SIDE_BACK);
        }

        /* 扫描银行卡请求*/
        @JavascriptInterface
        public void scanBankCard()
        {
            //检查相机权限，权限结果permissionListener进行监听，并执行相应操作
//            initPermissiont(Manifest.permission.CAMERA,REQUEST_CODE_BANK_CARD);
        }

        /* 保存身份证*/
        @JavascriptInterface
        public void createPicture(String json)
        {
            saveIdCardPic2Cache(json);
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void openWebView(String url)
        {
            Intent intent = new Intent(mContext,CcbWebViewActivity.class);
            intent.putExtra("url",url);
            startActivityForResult(intent,WEBVIEW_ACTIVITY_CODE);


        }
        /**
         * 复制到剪贴板
         */
        @JavascriptInterface
        public void copyToClipboard(String data)
        {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", data);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }

        @JavascriptInterface
        public void forBidScreenCap(String data)
        {

            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ((Activity)mContext).getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_SECURE;
                    Window window = ((Activity)mContext).getWindow();
                    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
       /*               window.getAttributes().flags = WindowManager.LayoutParams.FLAG_SECURE;
                    window.setAttributes(window.getAttributes());
                    window.getDecorView().invalidate();*/
//                    MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

                }
            });
        }

        @JavascriptInterface
        public void closeMainWebview(String data)
        {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("closeMainWebview","closeMainWebview with data");
                    finish();
                }
            });

        }

        @JavascriptInterface
        public void closeMainWebview()
        {
            Log.i("closeMainWebview","closeMainWebview with no data");
            finish();
        }
    }

    public void initESafe(){
        safe = EsafeUtils.getESafeLib(mContext);

        securityReq = new SecurityReq();
        securityReq.SYS_CODE = safe.getSYS_CODE();
        securityReq.APP_NAME = safe.getAPP_NAME();
        securityReq.MP_CODE = safe.getMP_CODE();
        securityReq.SEC_VERSION = safe.getVersion();
        securityReq.APP_IMEI = TextUtils.isEmpty(safe.getIMEI()) ? "" : safe.getIMEI();
        securityReq.GPS_INFO = TextUtils.isEmpty(safe.getGPS()) ? "" : safe.getGPS();

        final String json = JsonUtils.toJson(securityReq);
        //网络请求参数
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:createESafeResult('"+json+"')");
            }
        });
    }

    @SuppressLint("Override") @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            permissionListener.onPermissionGranted(requestCode);
        }else{
            permissionListener.onPermissionDeny(requestCode);
        }

        permissionListener.onPermissionRequestResult(requestCode,permissions,grantResults);
    }

    public void startfaceScan(){
        try {

            int[] seq = {LivenessInterface.LIVENESS_BLINK};
            face.startLivenessActivityForResult(CCBMainActivity.this,REQUEST_CODE_SCAN_FACE,LivenessInterface.LIVENESS_NORMAL,true,seq);
        } catch (Exception e) {
            Log.i("MainActivity","Exception:"+e.toString());
        }
    }
    /**
     * 处理人脸识别结果
     *
     * @param resultCode
     *            int 结果码
     * @param t
     *            faceInfo[] 人脸数据
     */
    private void handleFaceResult(final int resultCode, List<byte[]> t) {
        if (resultCode == CCBMainActivity.RESULT_OK) {
            photo = Base64.encodeToString(t.get(0), Base64.DEFAULT).replaceAll("\r|\n", "");
            final String returnResult = "{\"picture\":\"\"}";
            //网络请求参数
            //网络请求参数
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript:scanFaceResult('" + returnResult + "')");
                }
            });
        } else {
            // 失败
            new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String strErrorMessage =face.getLivenessErrorMessage(resultCode);
                            myWebView.loadUrl("javascript:errorHandle(" + strErrorMessage + "," + "'用户取消操作。'" + ")");
                        }
                    });
                }
            }.run();
        }
    }

    private void initSecurityReq(String json){
        initSecurityReq(json,null);
    }

    private void initSecurityReq(String json,String type){
        Log.i(TAG,"Security json from page:");
        initSecurityReqBody(json);
        if(null==securityReq)
            initESafe();
        securityReq.BRANCHID = JsonUtils.getString(json,"BRANCHID","");
       int faceType = JsonUtils.getInt(json,"FaceType",2);
        if(null!=type){
            securityReq.TXCODE = HostAddress.TXCODE_ZX02;
        }else {
            securityReq.TXCODE =  HostAddress.TXCODE;
        }

        if(faceType==1&&null!=idCardEntity){
            cardFace = JsonUtils.getString(json,"CardFace","");
            Log.i(TAG,"get the cardFace image from server.");

            securityReqBody.base64_Ecrp_Txn_Inf = cardFace;
        }else{
            securityReqBody.base64_Ecrp_Txn_Inf = photo;
        }
    }

    private void initSecurityReqBody(String json){
        securityReqBody = JsonUtils.jsonToBean(json,SecurityReqBody.class);
        securityReqBody.SYSTEM_TIME = DateUtils.getCurrentDate(DateUtils.dateFormatTimeStamp);
        securityReqBody.HARDWARESN = DeviceUtils.getDeviceId(mContext);
//        securityReqBody.base64_Ecrp_Txn_Inf = photo;
    }

    private void sendSecurityReq(){
        MainController.getInstance().postSecurity(mContext,securityReq, securityReqBody,new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Log.i("Polling", "send security failure responString" +arg0.toString() +arg1);
                LoadingDialogUtils.getInstance().dismissLoading();
                new Runnable(){
                    @Override
                    public void run() {
                        //上传安全网关失败
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:errorHandle("+null+","+"'网络异常，请稍后尝试。'"+")");
                            }
                        });
                    }
                }.run();
//				tv_welcome.setText(String.format(getResources().getString(R.string.welcome),"***"));
            }

            @Override
            public void onSuccess(ResponseInfo<String> responString) {
                Log.i("Polling", "xutils post security success." +responString.toString());
                LoadingDialogUtils.getInstance().dismissLoading();

                if (null!=responString&&!TextUtils.isEmpty(responString.result)) {
                    result = responString.result.toString();
                    Log.i("解密前Polling", "responString.result ：" +result );
                    try {
                        JSONObject json =new JSONObject(responString.result);
                        if("000000".equals(json.getString("Res_Rtn_Code"))){
                            result = safe.tranDecrypt(json.getString("Ret_Enc_Inf"));
                            result = TextUtils.isEmpty(result) ? "" : result;
                            Log.i("解密后Polling", "responString.result ：" +result );
                            //网络请求参数
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWebView.loadUrl("javascript:requestFaceInfoResult('"+result+"')");
                                }
                            });
                        }else{
                            result = json.getString("Res_Rtn_Msg");
                            final String code = json.getString("Res_Rtn_Code");
//                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                            new Runnable(){
                                @Override
                                public void run() {
                                    //安全网关请求失败
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            myWebView.loadUrl("javascript:errorHandle('"+code+"','"+result+"')");
                                        }
                                    });
                                }
                            }.run();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    new Runnable(){
                        @Override
                        public void run() {
                            //上传身份证结果
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWebView.loadUrl("javascript:errorHandle("+null+","+"'网络异常，请稍后尝试。'"+")");
                                }
                            });
                        }
                    }.run();
                }
            }
        });
    }

    public void uploadFiles(final int type){
        String filePath = "";
        try {
            if(type == REQUEST_CODE_IDCARD_SIDE_FRONT) {
                filePath = jsonPicPath.getString("FrontPicPath");
                fileUploadEntity.File_Nm = fileNm+"_ZM.jpg";
            }
            else  if(type == REQUEST_CODE_IDCARD_SIDE_BACK) {
                filePath = jsonPicPath.getString("BackPicPath");
                fileUploadEntity.File_Nm = fileNm+"_FM.jpg";

            }

            if(TextUtils.isEmpty(jsonPicPath.getString("FrontPicPath")))
                return;
            File imageFile = new File(filePath);
            LoadingDialogUtils.getInstance().showLoading(this);
            MainController.getInstance().uploadFiles(cookieStore,imageFile,fileUploadEntity,new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo responString) {
                    Log.i("Polling", "upload file success responString" +responString.result.toString() );
                    LoadingDialogUtils.getInstance().dismissLoading();

                    boolean isSuccess = false;
                    if (null!=responString.result){
                        String result = responString.result.toString();
                        isSuccess = JsonUtils.getBoolean(result,"SUCCESS",false);
                    }

                    if(isSuccess&&REQUEST_CODE_IDCARD_SIDE_FRONT == type) {
                        uploadFiles(REQUEST_CODE_IDCARD_SIDE_BACK);
                        try {
                            FileUtils.deleteFile(jsonPicPath.getString("FrontPicPath"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else  if(isSuccess&&REQUEST_CODE_IDCARD_SIDE_BACK==type){
                        try {
                            FileUtils.deleteFile(jsonPicPath.getString("BackPicPath"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new Runnable(){
                            @Override
                            public void run() {
                                //上传身份证结果
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myWebView.loadUrl("javascript:createPictureResult()");
                                    }
                                });
                            }
                        }.run();
                    }else {
//                        Toast.makeText(mContext,"上传图片失败，请稍后尝试。",Toast.LENGTH_SHORT).show();
                        new Runnable(){
                            @Override
                            public void run() {
                                //上传身份证结果
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myWebView.loadUrl("javascript:errorHandle("+null+","+"'上传图片失败，请稍后尝试。'"+")");
                                    }
                                });
                            }
                        }.run();
                    }
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Log.i("Polling", "upload file failure responString" +arg0.toString() +arg1);
                    LoadingDialogUtils.getInstance().dismissLoading();
                    new Runnable(){
                        @Override
                        public void run() {
                            //上传身份证结果
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myWebView.loadUrl("javascript:errorHandle("+null+","+"'网络异常，请稍后尝试。'"+")");
                                }
                            });
                        }
                    }.run();
                }

                @Override
                public void onStart() {
                    super.onStart();

                    Log.i(TAG,"--------upload file start------");
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.i(TAG,"--------onLoading file ,total is"+total+";current is "+current+"------");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initFileUpload(String json){
        fileUploadEntity = new FileUploadEntity();
        fileUploadEntity.USERID = JsonUtils.getString(json,"USERID","");
        fileUploadEntity.BRANCHID = JsonUtils.getString(json,"BRANCHID","");
        fileUploadEntity.TXCODE = JsonUtils.getString(json,"TXCODE","");
        fileUploadEntity.File_Date =JsonUtils.getString(json,"File_Date","");
        fileUploadEntity.CCB_IBSVersion = JsonUtils.getString(json,"CCB_IBSVersion","");
        fileUploadEntity.ACTION = JsonUtils.getString(json,"ACTION","");
        fileNm =JsonUtils.getString(json,"File_Nm","");
    }

    private void scanIDCard(int code){
        takePhotoFromCamera(code);
    }


    /**
     * 拍照
     * @param code    正反面
     */
    private void takePhotoFromCamera(int code){
        if(FileUtils.checkSDCard()){
            File dir = new File(Environment.getExternalStorageDirectory()+"/ccb");
            if(!dir.exists())
                dir.mkdir();
            tempFileName = Environment.getExternalStorageDirectory()+"/ccb/temp.jpg";
            tempFile =new File(tempFileName);
            temUri =  CameraApi.getTakenPhoto(this,tempFile);
            CameraApi.requestCameraTakePhoto(this,code,temUri);
        }else{
            Toast.makeText(this,"您的手机不存在sd卡，无法保存相片",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_IDCARD_SIDE_FRONT==requestCode){
            if(idCardEntity == null)
                idCardEntity = new IDCardEntity();
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG,"BitmapFactory.decodeFile");

                Bitmap bm = getCameraBitmap();
                if(null==bm) {
                    Toast.makeText(this,"相机拍照失败，未获取到照片。",Toast.LENGTH_SHORT).show();
                    return;
                }
//                findViewById(R.id.button).setBackground(new BitmapDrawable(getResources(),bm));
                Log.i(TAG,"FileUtils.compressBitmap");
                imageBytesFront = FileUtils.compressBitmap(bm,350);
                Log.i(TAG,"Base64.encodeToString");
                idCardEntity.cardImage = Base64.encodeToString(imageBytesFront,Base64.DEFAULT).replaceAll("\r|\n","");
                idCardEntity.scanType = "TakePicture";
                new Runnable(){
                    @Override
                    public void run() {
                        final String json = JsonUtils.toJson(idCardEntity);
//                        Log.i(TAG,json);
                        //返回身份证查询结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:scanIdCardFrontResult('"+json+"')");
                            }
                        });
                    }
                }.run();
            }else{
                Toast.makeText(this,"相机拍照失败，未获取到照片。",Toast.LENGTH_SHORT).show();
            }


        }else if(REQUEST_CODE_IDCARD_SIDE_BACK==requestCode){
            if(idCardEntity == null)
                idCardEntity = new IDCardEntity();
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bm = getCameraBitmap();
//                findViewById(R.id.button).setBackground(new BitmapDrawable(getResources(),bm));
                if(null==bm) {
                    Toast.makeText(this,"相机拍照失败，未获取到照片。",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG,"FileUtils.compressBitmap");
                imageBytesBack = FileUtils.compressBitmap(bm,350);
//                imageBytesBack = FileUtils.bitmapToBytes(bm);

                Log.i(TAG,"Base64.encodeToString");
                idCardEntity.cardImage = Base64.encodeToString(imageBytesBack,Base64.DEFAULT).replaceAll("\r|\n","");
                idCardEntity.scanType = "TakePicture";
                new Runnable(){
                    @Override
                    public void run() {
                        final String json = JsonUtils.toJson(idCardEntity);
//                        Log.i(TAG,json);
                        //返回身份证查询结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:scanIdCardBackResult('"+json+"')");
                            }
                        });
                    }
                }.run();
            }else{
                Toast.makeText(this,"相机拍照失败，未获取到照片。",Toast.LENGTH_SHORT).show();
            }
        }/*else if(REQUEST_CODE_BANK_CARD==requestCode){
            if(bankEntity == null)
                bankEntity = new BankEntity();
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bm = BitmapFactory.decodeFile(tempFileName);
                findViewById(R.id.button).setBackground(new BitmapDrawable(getResources(),bm));
                byte[] b = FileUtils.bitmapToBytes(bm);
                imageBytesBank = FileUtils.compressBitmap(b,290);
                bankEntity.bankcardBitmap = Base64.encodeToString(imageBytesBank,Base64.DEFAULT).replaceAll("\r|\n","");
                new Runnable(){
                    @Override
                    public void run() {
                        final String json = JsonUtils.toJson(bankEntity);
                        Log.i(TAG,json);
                        //返回身份证查询结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:scanBankCardResult('"+json+"')");
                            }
                        });
                    }
                }.run();
            }
        }*/else if (requestCode == WEBVIEW_ACTIVITY_CODE){
            final String params = data.getStringExtra("PARAMS");
            //关闭webview回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript:closeWebViewResult('"+params+"')");
                }
            });
        }else if (requestCode == REQUEST_CODE_SCAN_FACE){
            List<byte[]> faceList = face.getLivenessResultImages(data,50);
            handleFaceResult(resultCode,faceList);
        }/*else if (requestCode == REQUEST_CODE_IDCARD_SIDE_BACK || requestCode == REQUEST_CODE_IDCARD_SIDE_FRONT  ){
            //camera拍照返回
            if (resultCode != Activity.RESULT_OK) {
                LogUtils.d("TAG:" + TAG+ " 相机拍照失败，未获取到照片。 ");
//                Toast.makeText(this,"相机拍照失败，未获取到照片。",Toast.LENGTH_SHORT).show();
            }else{
               *//* //获取照片缩微图
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                //获取缩微图URI
                Uri uri;
                if(null==data.getData()){
                    uri =  Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bm, null,null));
                }else
                    uri = data.getData();
                LogUtils.d("TAG:" + TAG+ " 拍照图片URI: " + uri);*//*
//                Uri imageUri = getImageFileUri();
                Bitmap bm = BitmapFactory.decodeFile(tempFileName);//FileUtils.readBitmap2Cache(this,tempFile.getAbsolutePath(),false);
                findViewById(R.id.button).setBackground(new BitmapDrawable(getResources(),bm));
                byte[] b = FileUtils.bitmapToBytes(bm);
                b = FileUtils.compressBitmap(b,290);
            }
        }*/
    }

    public void saveIdCardPic2Cache(String json){
        String backPic = "",frontPic = "" ,name="";
        try {
            if(isUseLocal)
                name = json;
            else
                name = JsonUtils.getString(json,"File_Nm","");
            if(null!=imageBytesFront) {
//                imageBytesFront = FileUtils.compressBitmap(imageBytesFront,290);
                frontPic = FileUtils.writeImage2Cache(mContext, imageBytesFront, name + "_ZM");
//                FileUtils.writeImage2Disk(mContext, imageBytesFront, name + "_ZM");
            }
            if(null!=imageBytesBack) {
//                imageBytesBack = FileUtils.compressBitmap(imageBytesBack,290);
                backPic = FileUtils.writeImage2Cache(mContext, imageBytesBack, name + "_FM");
//                FileUtils.writeImage2Disk(mContext, imageBytesBack, name + "_FM");
            }
            jsonPicPath.put("FrontPicPath",frontPic);
            jsonPicPath.put("BackPicPath",backPic);
            initFileUpload(json);
            uploadFiles(REQUEST_CODE_IDCARD_SIDE_FRONT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getCameraBitmap(){
        Bitmap bm ;
        if(null==tempFileName)
            tempFileName = Environment.getExternalStorageDirectory()+"/ccb/temp.jpg";
        int degree = FileUtils.readPictureDegree(tempFileName);
        bm = BitmapFactory.decodeFile(tempFileName);
        if(0!=degree)
            bm = FileUtils.rotaingImageView(degree,bm);
        return bm;

    }

    @Override
    protected void onDestroy() {
        if(myWebView!=null){
            ViewGroup parent = (ViewGroup) myWebView.getParent();
            if(null!=parent)
                parent.removeView(myWebView);
            myWebView.removeAllViews();
            myWebView.destroy();
        }
        super.onDestroy();
    }


}

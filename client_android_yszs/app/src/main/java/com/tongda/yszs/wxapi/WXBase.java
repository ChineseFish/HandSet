package com.tongda.yszs.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tongda.wechat.Constants;

import com.tongda.base.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WXBase  extends Activity implements IWXAPIEventHandler {
    private static IWXAPI api = null;
    private static WebView webView = null;

    //
    private static ArrayList<ArrayList<String>> wxResultList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        if(api == null)
        {
            api = WXAPIFactory.createWXAPI(this, null);
        }

        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(WebView _webView)
    {
        webView = _webView;
    }

    public static void sendAuthRequest(Context context, String type)
    {
        //
        Constants.init(context);

        //
        if(api == null)
        {
            api = WXAPIFactory.createWXAPI(context, null);
        }

        //
        api.unregisterApp();
        api.registerApp(Constants.getConfig(context, type, "wechat_appid_auth"));

        //
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";

        //
        api.sendReq(req);
    }

    public static void sendPayRequest(final Context context, final String type, final String orderNo, String price)
    {
        //
        Constants.init(context);

        //
        if(api == null)
        {
            api = WXAPIFactory.createWXAPI(context, null);
        }

        //
        api.unregisterApp();
        api.registerApp(Constants.getConfig(context, type, "wechat_appid_pay"));

        //
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("正在加载。。。");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //
        Thread thread = new Thread() {
            @Override
            public void run() {

                //
                URL url;
                HttpURLConnection connection = null;

                //
                try {
                    String requestUrl =  String.format("%s?description=iosWechatPay&tradeno=%s", Constants.getConfig(context, type, "wechat_pay_order_info_url"), orderNo);

                    //
                    url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);

                    //
                    InputStream in = connection.getInputStream();

                    // read response data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // translate to JSON format
                    JSONObject json = new JSONObject(response.toString());

                    //
                    PayReq req = new PayReq();
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.sign = json.getString("sign");
                    req.packageValue = "Sign=WXPay";

                    //
                    api.sendReq(req);


                } catch (MalformedURLException e) {
                    Utils.alert(context, true, "微信支付失败", e.toString());
                } catch (IOException | JSONException e) {
                    Utils.alert(context, true, "微信支付失败", e.toString());
                } catch (Exception e) {
                    Utils.alert(context, true, "微信支付失败", e.toString());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                    //
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(final BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 授权登录
                if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                    SendAuth.Resp authResp = (SendAuth.Resp)resp;
                    final String code = authResp.code;

                    //
                    final String jsInterface = String.format("javascript:androidGtzn.wechatAuthCb(\"%s\")", code);
                    webView.loadUrl(jsInterface);
                }
                // 支付
                else if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                    //
                    final String jsInterface = String.format("javascript:androidGtzn.wechatPayCb()");
                    webView.loadUrl(jsInterface);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                wxResultList.add(new ArrayList<String>() {{
                    add("微信");
                    add("用户取消");
                }});
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                wxResultList.add(new ArrayList<String>() {{
                    add("微信");
                    add("用户拒绝");
                }});
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                wxResultList.add(new ArrayList<String>() {{
                    add("微信");
                    add("微信不支持");
                }});
                break;
            default:
                wxResultList.add(new ArrayList<String>() {{
                    add("微信");
                    add("未知错误: " + resp.errCode);
                }});
                break;
        }

        //
        finish();
    }

    public static void handleWXResult(Context context)
    {
        //
        for(ArrayList<String> value:wxResultList)
        {
            Utils.alert(context, false, value.get(0), value.get(1));
        }

        //
        wxResultList.clear();
    }
}

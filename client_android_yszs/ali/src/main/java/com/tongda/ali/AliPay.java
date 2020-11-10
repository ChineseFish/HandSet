package com.tongda.ali;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebView;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import java.util.Map;

public class AliPay {

    public static WebView webView = null;

    public static void init(WebView _webView)
    {
        webView = _webView;
    }

    private static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Map<String, String> payResult = (Map<String, String>) msg.obj;

            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String memo = payResult.get("memo");
            String result = payResult.get("result");
            String resultStatus = payResult.get("resultStatus");

            //
            String jsInterface = String.format("javascript:androidGtzn.aliPayCb(\"%s\", \"%s\", \"%s\")", memo, result, resultStatus);
            webView.loadUrl(jsInterface);
        };
    };

    public static void sendPayRequest(final Context context, final String orderInfo)
    {
        Thread payThread = new Thread(new Runnable() {

            @Override
            public void run() {
                PayTask alipayTask = new PayTask((Activity) context);
                Map<String, String> result = alipayTask.payV2(orderInfo,true);

                //
                Message msg = new Message();
                msg.obj = result;

                //
                mHandler.sendMessage(msg);
            }
        });

        payThread.start();
    }

    public static boolean interceptAliPay(final Context context, String url)
    {
        if (!(url.startsWith("http") || url.startsWith("https"))) {
            return true;
        }

        /**
         * Alipay H5 支付过滤
         */
        if(url.startsWith("alipays:") || url.startsWith("alipay") || url.contains("mobilecodec.alipay.com") || url.contains("platformapi/startapp")) {
            try {
                context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            } catch (Exception e) {
                new AlertDialog.Builder(context)
                        .setMessage("未检测到支付宝客户端，请安装后重试。")
                        .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                context.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                            }
                        }).setNegativeButton("取消", null).show();
            }

            //
            return true;
        }

        /**
         * 推荐采用的新的二合一接口
         */
        final PayTask task = new PayTask((Activity) context);
        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
            @Override
            public void onPayResult(final H5PayResultModel result) {
                // 支付结果返回
                final String url = result.getReturnUrl();
                if (!TextUtils.isEmpty(url)) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(url);
                        }
                    });
                }
            }
        });

        /**
         * 判断是否成功拦截
         * 若成功拦截，则无需继续加载该URL；否则继续加载
         */
        if (isIntercepted) {
            return true;
        }

        //
        return false;
    }
}

package com.tongda.ccb_pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.WebView;
import android.widget.Toast;

//
import com.ccb.ccbnetpay.message.CcbPayResultListener;
import com.ccb.ccbnetpay.platform.CcbPayPlatform;
import com.ccb.ccbnetpay.platform.Platform;

//
import com.tongda.ccb_pay.bean.CcbAccountBean;

//
import java.util.List;
import java.util.Map;

//
public class CCBPay {

    //
    private static CcbPayResultListener ccbPayLResponseistener = null;
    private static CcbAccountBean ccbPayQequestParams = null;

    //
    private static WebView mainWebview;

    //
    private static String SDK_CANCEL_FINISH_RECEIVE_ACTION = "sdk_cancel_finish_action";
    private static SdkMsgBroadcastReceiver receiver;

    //
    private static class SdkMsgBroadcastReceiver extends BroadcastReceiver {
        private Context context;

        public SdkMsgBroadcastReceiver(Context _context)
        {
            context = _context;
        }

        @Override
        public void onReceive(Context _context, Intent intent) {
            String sdkremind = intent.getStringExtra("sdkremind");

            //
            Toast.makeText(context, sdkremind, Toast.LENGTH_SHORT).show();
        }
    }

    //
    public static void init(Context context, WebView webView)
    {
        //
        mainWebview = webView;

        //
        registerPayCancleBroadCast(context);
    }


    public static void sendPayRequest(final Context context, String type, String originOrderNo, String orderNo, String price)
    {
        //
        Constants.init(context);

        //
        if(!checkAppInstalled(context,"com.chinamworld.main")){
            new AlertDialog.Builder(context)
                    .setTitle("建行支付提示")
                    .setMessage("请下载建行app,并开通龙支付!\n点击安装立享优惠!")
                    .setPositiveButton("点击安装", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("market://search?q=中国建设银行"));

                                //
                                context.startActivity(i);
                            } catch (Exception e) {
                                Toast.makeText(context, "您的手机上没有安装应用市场", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                            //
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("更换支付", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing - it will close on its own
                        }
                    })
                    .setCancelable(false)
                    .show();

            //
            return;
        }

        //
        ccbPayQequestParams = new CcbAccountBean();
        ccbPayQequestParams.setMerchantId(Constants.getConfig(context, type, "CCB_MERCHANTID"));
        ccbPayQequestParams.setPosId(Constants.getConfig(context, type, "CCB_POSID"));
        ccbPayQequestParams.setBankId(Constants.getConfig(context, type, "CCB_BRANCHID"));
        ccbPayQequestParams.setPubNo(Constants.getConfig(context, type, "CCB_PUB_LOWER_30"));
        ccbPayQequestParams.setInstallNum("");

        //
        UrlProcessor urlTest = new UrlProcessor();
        final String params = urlTest.make(ccbPayQequestParams, price, originOrderNo, orderNo);

        // 支付回调
        ccbPayLResponseistener = new CcbPayResultListener() {
            @Override
            public void onSuccess(Map<String, String> result) {
                if(result.get("SUCCESS").equals("Y")) {
                    mainWebview.loadUrl("javascript:jsSDKPaymentResult(0)");
                } else if(result.get("SUCCESS").equals("N")){
                    mainWebview.loadUrl("javascript:jsSDKPaymentResult(1)");
                }
            }

            @Override
            public void onFailed(String msg) {
                mainWebview.loadUrl("javascript:jsSDKPaymentResult(1)");
            }
        };

        Runnable payRunnable = new Runnable(){
            @Override
            public void run() {
                Platform ccbPayPlatform = new CcbPayPlatform
                        .Builder()
                        .setActivity((Activity) context)
                        // 支付回调
                        .setListener(ccbPayLResponseistener)
                        // 商户串 格式见 （3.1）
                        .setParams(params)
                        // 支付模式
                        .setPayStyle(Platform.PayStyle.APP_PAY)
                        .build();

                //
                ccbPayPlatform.pay();
            }
        };

        //
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 根据包名判断apk是否已经安装
     * @param context
     * @param pkgName
     * @return
     */
    private static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName== null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if(info == null || info.isEmpty())
            return false;
        for ( int i = 0; i < info.size(); i++ ) {
            if(pkgName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean interceptCcbPay(Context context, String url)
    {
        if(url.startsWith("mbspay://")
                || url.startsWith("http://mbspay://")
                || url.startsWith("https://mbspay://")){
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                //
                context.startActivity(intent);
            } catch (Exception e) {
                //
                Toast.makeText(context, "建行相关请求处理失败", Toast.LENGTH_SHORT).show();
            }

            //
            return true;
        }

        //
        return false;
    }

    private static void registerPayCancleBroadCast(Context context)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDK_CANCEL_FINISH_RECEIVE_ACTION);

        //
        receiver = new SdkMsgBroadcastReceiver(context);

        //
        context.registerReceiver(receiver, filter);
    }
}

package com.tongda.wechat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class WechatPay {
    public static boolean interceptWechatPay(Context context, String url)
    {
        if (url.startsWith("weixin://")
                || url.startsWith("http://weixin/wap/pay")
                || url.startsWith("https://weixin/wap/pay")) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                //
                context.startActivity(intent);
            } catch (Exception e) {
                //
                Toast.makeText(context, "微信相关请求处理失败", Toast.LENGTH_SHORT).show();
            }

            //
            return true;
        }

        //
        return false;
    }
}

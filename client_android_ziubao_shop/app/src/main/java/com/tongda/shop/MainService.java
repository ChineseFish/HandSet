package com.tongda.shop;

import android.content.Context;

import com.tongda.base.Service;

public class MainService extends Service {
    @Override
    public void app_jumpToUrl(final Context context, String url)
    {
        MainActivity.loadUrl(url);
    }
}

package com.tongda.yszs;

import com.tongda.base.IService;

public class MainService implements IService {
    public void mainServieSetMainPage()
    {
        MainActivity.loadMainPage();
    }

    public void jumpToUrl(String url) {
        MainActivity.loadUrl(url);
    }
}

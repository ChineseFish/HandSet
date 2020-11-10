package com.tongda.base;


import android.app.Activity;

import java.util.Map;

public interface GroupLoader {
    Map<String, GroupLoader> injectModule();

    Map<String, Class<? extends IService>> injectService();

    Class<? extends Activity> getActivity(String activityName);
}

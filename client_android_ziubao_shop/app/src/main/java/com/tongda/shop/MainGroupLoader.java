package com.tongda.shop;

import android.app.Activity;

import com.tongda.base.GroupLoader;
import com.tongda.base.Service;

import java.util.HashMap;
import java.util.Map;

public class MainGroupLoader implements GroupLoader {
    private Map<String, Class<? extends Activity>> sActivityMap = null;

    @Override
    public Map<String, GroupLoader> injectModule() {
        Map<String, GroupLoader> result = new HashMap<>();

        //
        result.put("app", new MainGroupLoader());

        //
        return result;
    }

    @Override
    public Map<String, Class<? extends Service>> injectService() {
        Map<String, Class<? extends Service>> result = new HashMap<>();

        //
        result.put("app", MainService.class);

        //
        return result;
    }

    @Override
    public Class<? extends Activity> getActivity(String activityName) {
        if (sActivityMap == null) {
            sActivityMap = new MainActivityLoader().injectActivity();
        }
        if (sActivityMap == null) {
            throw new IllegalStateException(activityName + "not found!");
        }

        return sActivityMap.get(activityName);
    }
}

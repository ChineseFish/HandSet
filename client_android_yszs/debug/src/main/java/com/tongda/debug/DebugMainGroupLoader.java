package com.tongda.debug;
import android.app.Activity;

import com.tongda.base.IService;
import com.tongda.base.GroupLoader;

import java.util.HashMap;
import java.util.Map;

public class DebugMainGroupLoader implements GroupLoader {
    private Map<String, Class<? extends Activity>> sActivityMap = null;

    @Override
    public Map<String, GroupLoader> injectModule() {
        Map<String, GroupLoader> result = new HashMap<>();

        //
        result.put("ziubao_debug", new DebugMainGroupLoader());

        //
        return result;
    }

    @Override
    public Map<String, Class<? extends IService>> injectService() {
        return null;
    }

    @Override
    public Class<? extends Activity> getActivity(String activityName) {
        if (sActivityMap == null) {
            sActivityMap = new DebugMainActivityLoader().injectActivity();
        }
        if (sActivityMap == null) {
            throw new IllegalStateException(activityName + "not found!");
        }

        return sActivityMap.get(activityName);
    }
}

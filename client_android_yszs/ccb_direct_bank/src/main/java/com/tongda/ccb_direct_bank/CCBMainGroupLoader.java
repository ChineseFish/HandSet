package com.tongda.ccb_direct_bank;
import android.app.Activity;

import com.tongda.base.IService;
import com.tongda.base.GroupLoader;

import java.util.HashMap;
import java.util.Map;

public class CCBMainGroupLoader implements GroupLoader {
    private Map<String, Class<? extends Activity>> sActivityMap = null;

    @Override
    public Map<String, GroupLoader> injectModule() {
        Map<String, GroupLoader> result = new HashMap<>();

        result.put("ccbDirectBank", new CCBMainGroupLoader());
        return result;
    }

    @Override
    public Map<String, Class<? extends IService>> injectService() {
        return null;
    }


    @Override
    public Class<? extends Activity> getActivity(String activityName) {
        if (sActivityMap == null) {
            sActivityMap = new CCBMainActivityLoader().injectActivity();
        }
        if (sActivityMap == null) {
            throw new IllegalStateException(activityName + "not found!");
        }

        return sActivityMap.get(activityName);
    }
}

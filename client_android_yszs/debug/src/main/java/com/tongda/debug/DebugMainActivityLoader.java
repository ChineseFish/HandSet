package com.tongda.debug;

import android.app.Activity;

import com.tongda.base.ActivityLoader;

import java.util.HashMap;
import java.util.Map;

public class DebugMainActivityLoader implements ActivityLoader {
    @Override
    public Map<String, Class<? extends Activity>> injectActivity() {
        Map<String, Class<? extends Activity>> result = new HashMap<>();

        result.put("main", DebugMainActivity.class);

        return result;
    }
}

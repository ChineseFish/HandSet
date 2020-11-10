package com.tongda.debug;

import android.app.Activity;

import com.tongda.base.ActivityLoader;

import java.util.HashMap;
import java.util.Map;

public class MainActivityLoader implements ActivityLoader {
    @Override
    public Map<String, Class<? extends Activity>> injectActivity() {
        Map<String, Class<? extends Activity>> result = new HashMap<>();

        result.put("main", MainActivity.class);

        return result;
    }
}

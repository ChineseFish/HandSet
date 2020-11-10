package com.tongda.ccb_direct_bank;

import android.app.Activity;

import com.tongda.base.ActivityLoader;

import java.util.HashMap;
import java.util.Map;

public class CCBMainActivityLoader implements ActivityLoader {
    @Override
    public Map<String, Class<? extends Activity>> injectActivity() {
        Map<String, Class<? extends Activity>> result = new HashMap<>();

        result.put("main", CCBMainActivity.class);

        return result;
    }
}

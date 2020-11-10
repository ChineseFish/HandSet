package com.tongda.ccb_direct_bank.utils;

import android.text.TextUtils;
import android.util.Log;
import com.tongda.ccb_direct_bank.constant.Global;
import com.lidroid.xutils.util.LogUtils;
import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wutw on 2018/1/5 0005.
 */
public class EntityUtils {

    /**
     * obj 要生成MAP的对象
     * isAnnotation obj对象是不是使用了Annotation
     * */
    public static Map ObjToMap(Object obj){
        Map<String, String> requestParams = new HashMap<String, String>();
        // 获取所有定义的字段
        Field[] mFields = obj.getClass().getFields();
        for (Field mField : mFields) {
            String parameterKey = mField.getName();
            String parameterValue = "";

            if(null!=obj){
                try {
                    Object value = mField.get(obj);
                    if (null != value) {

                        if (value instanceof String) {
                            parameterValue = String.valueOf(value);
                        } else {
                            parameterValue = new Gson().toJson(value);
                        }
                    }
                } catch (Exception e) {
                    Log.e("",e.toString());
                }
            }
            // 加入请求参数
            requestParams.put(parameterKey, parameterValue);
        }
        return requestParams;
    }

    /**
     * obj 要生成MAP的对象
     * isAnnotation obj对象是不是使用了Annotation
     * */
    public static String ObjToParamsString(Object obj,String encode){
        Map <String, String>map = ObjToMap(obj);

        if (map == null || map.size() == 0)
            return null;

        StringBuffer buffer = new StringBuffer(128);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                try {
                    String value = entry.getValue() == null ? "" : entry.getValue();
                    value =  URLEncoder.encode(value, TextUtils.isEmpty(encode) ?Global.DEFAULT_ENCORD : encode);
                    buffer.append(key).append(Global.ONE_EQUAL).append(value).append(Global.YU);
                } catch (Exception e) {
                    Log.e("",e.toString());
                }
            }
        }
        // 删除最后的一个&
        if (buffer.toString().endsWith("&")) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        LogUtils.i("组装后的ccbParams");
        LogUtils.i("Polling "+buffer.toString());

        return buffer.toString();
    }
}

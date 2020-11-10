package com.tongda.ccb_direct_bank.utils;

import android.widget.ImageView;

//import com.tendyron.facelib.impl.FacelibInterface;

import com.tendyron.liveness.impl.LivenessInterface;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by wutw on 2018/1/3 0003.
 */
public class FaceScanUtils {

     // 生成随机活体检测动作序列。库里面会有这个函数，如果initLiveness的第一个参数orderCount设置为0或者小于0时需要自己定义动作序列。
     public static String generatingActionOrder() {
        Random random = new Random();
        ArrayList<Integer> actionList = new ArrayList<Integer>();
        actionList.add(LivenessInterface.LIVENESS_BLINK);
        actionList.add(LivenessInterface.LIVENESS_MOUTH);
        actionList.add(LivenessInterface.LIVENESS_HEADNOD);
        actionList.add(LivenessInterface.LIVENESS_HEADYAW);
        int tmp = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; ++i) {
            tmp = random.nextInt(actionList.size());
            sb.append(actionList.get(tmp));
            sb.append(" ");
            actionList.remove(tmp);
        }
        String re = new String(sb);
        return re;
    }

    public static String picToString(ImageView mImage){
        return "";
    }
}

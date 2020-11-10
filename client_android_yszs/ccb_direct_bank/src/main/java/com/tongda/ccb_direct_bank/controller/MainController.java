package com.tongda.ccb_direct_bank.controller;

import android.content.Context;
import com.tongda.ccb_direct_bank.constant.HostAddress;
import com.tongda.ccb_direct_bank.entity.BaseReq;
import com.tongda.ccb_direct_bank.entity.FileUploadEntity;
import com.tongda.ccb_direct_bank.entity.SecurityReqBody;
import com.tongda.ccb_direct_bank.utils.HttpXutils;
import com.tongda.ccb_direct_bank.utils.LoadingDialogUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;


import org.apache.http.client.CookieStore;

import java.io.File;

/**
 * Created by wutw on 2018/1/4 0004.
 */
public class MainController {
   private static MainController controller;

    public static MainController getInstance(){
        if(controller == null){
            controller = new MainController();
        }
        return controller;
    }
    public void get(Context context,BaseReq params,RequestCallBack callBack) {
        LoadingDialogUtils.getInstance().showLoading(context);
        HttpXutils.getInstance().get(context, HostAddress.host,params, callBack);
    }

    public void postSecurity(Context context,BaseReq params, SecurityReqBody securityReqBody, RequestCallBack callBack){
        LoadingDialogUtils.getInstance().showLoading(context);
        HttpXutils.getInstance().postEncode(context, HostAddress.host, params,securityReqBody,callBack);
    }

    public void post(Context context,BaseReq params,RequestCallBack callBack) {
        LoadingDialogUtils.getInstance().showLoading(context);
        HttpXutils.getInstance().post(context, HostAddress.host, params,callBack);
    }

    public void uploadFiles(CookieStore cookieStore, File imageFile, BaseReq params, RequestCallBack requestCallBack){
        String url = HostAddress.pichost+((FileUploadEntity)params).ACTION;
        HttpXutils.getInstance().uploadFiles(cookieStore,url,imageFile, params,requestCallBack);
    }
}

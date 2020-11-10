package com.tongda.ccb_direct_bank.utils;

import android.content.Context;
import android.util.Log;
import com.tongda.ccb_direct_bank.constant.Global;
import com.tongda.ccb_direct_bank.entity.BaseReq;
import com.tongda.ccb_direct_bank.entity.SecurityReqBody;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by wutw on 2018/1/3 0003.
 */
public class HttpXutils {

    private static HttpXutils mHttpXutils;
    HttpUtils httpUtils;

    Context context;
    String path;
    BaseReq params;
    RequestCallBack requestCallBack;

     public synchronized static HttpXutils getInstance(){
         if(mHttpXutils == null){
             mHttpXutils = new HttpXutils();
         }
         return mHttpXutils;
     }

    private HttpXutils(){
        Log.i("Polling", "xutils init.");
        httpUtils = new HttpUtils(60000);
        httpUtils.configSSLSocketFactory(SSLSocketFactory.getSocketFactory());
    }

    public void get(Context con,String paths, BaseReq param, RequestCallBack callBack){
        this.context = con;
        this.params = param;
        this.path = paths;
        this.requestCallBack = callBack;
        new Runnable(){
            @Override
            public void run() {
                Log.i("Polling", "xutils get."+path);
                String url = assembleUrl(context,path,params,null);
                Log.i("Polling", "xutils get."+path);
                httpUtils.send(HttpRequest.HttpMethod.GET, url, null, requestCallBack);
            }
        }.run();

    }

    public void post(Context con,String paths, BaseReq param,RequestCallBack callBack){
        this.context = con;
        this.params = param;
        this.path = paths;
        this.requestCallBack = callBack;
        new Runnable(){
            @Override
            public void run() {
                Log.i("Polling", "xutils post."+path);
                RequestParams requestParams = assembleRequestParams(context,params,null);
                Log.i("Polling", "xutils get."+path);
                httpUtils.send(HttpRequest.HttpMethod.POST, path, requestParams, requestCallBack);
            }
        }.run();
    }

    public void postEncode(Context con, String paths, BaseReq param, final SecurityReqBody securityReqBody, RequestCallBack callBack) {
        this.context = con;
        this.params = param;
        this.path = paths;
        this.requestCallBack = callBack;
        new Runnable(){
            @Override
            public void run() {
                Log.i("Polling", "xutils post."+path);
                RequestParams requestParams = assembleRequestParams(context,params,securityReqBody);
                Log.i("Polling", "xutils POST."+requestParams.toString());
                httpUtils.send(HttpRequest.HttpMethod.POST, path, requestParams, requestCallBack);
            }
        }.run();
    }

    public void uploadFiles(CookieStore cookieStore, final String url, final File imageFile, BaseReq param, RequestCallBack callBack){
        if(null == imageFile)
            return ;
        httpUtils.configCookieStore(cookieStore);
        this.params = param;
        this.path = url;
        this.requestCallBack = callBack;
        new Runnable(){
            @Override
            public void run() {
                RequestParams requestParams = assembleRequestParams(null,params,null);
                requestParams.addBodyParameter("file",imageFile,"image/JPEG");
                Log.i("Polling", "xutils post."+path);
                Log.i("Polling", "xutils POST."+requestParams.toString());
                httpUtils.send(HttpRequest.HttpMethod.POST,path,requestParams,requestCallBack);
            }
        }.run();

    }

    private String assembleUrl(Context context,String host, BaseReq params, SecurityReqBody securityReqBody){
        String url = host;

        if(null!=securityReqBody){
            String body = EntityUtils.ObjToParamsString(securityReqBody,null);
            params.ccbParam = EsafeUtils.makeESafeData(context,body);
        }

        url= url+ Global.WEN+EntityUtils.ObjToParamsString(params,null);
        return url;
    }

    private RequestParams assembleRequestParams(Context context,BaseReq params, SecurityReqBody securityReqBody){
        RequestParams requestParams = new RequestParams();
        if(null!=securityReqBody&&null!=context){
            String body = JsonUtils.toJson(securityReqBody);
            String d = body.contains("base64_Ecrp_Txn_Inf")?body.substring(0,body.indexOf("base64_Ecrp_Txn_Inf")):body;
            Log.i("Polling", "securityReqBody xutils post."+d);
            params.ccbParam = EsafeUtils.makeESafeData(context,body);
        }

        Map<String, String>  map = EntityUtils.ObjToMap(params);
        for(String dataKey : map.keySet())   {
            Log.i("Polling", "params xutils post."+dataKey+":"+map.get(dataKey));
            requestParams.addBodyParameter(dataKey, map.get(dataKey));
        }
        return requestParams;
    }

}

package com.tongda.ums;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.tongda.base.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UmsPay {
    //
    private static WebView mainWebview;
    private static HashMap<String, UmsAliPayCb> fetchUmsAliPayCbList = new HashMap();


    public static void init(WebView webView)
    {
        //
        mainWebview = webView;
    }

    public static void sendPayRequest(final Context context, final String type, final String orderNo, String price)
    {
        Constants.init(context);

        //
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("正在加载。。。");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //
        Thread thread = new Thread() {
            @Override
            public void run() {

                //
                URL url;
                HttpURLConnection connection = null;

                //
                try {
                    String requestUrl =  String.format("%s?description=androidAliPay&orderNo=%s&redirectSuccess=&redirectFail=", Constants.getCofig(context, type, "ums_alipay_order_info_url"), orderNo);

                    //
                    url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);

                    //
                    InputStream in = connection.getInputStream();

                    // read response data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // translate to JSON format
                    JSONObject json = new JSONObject(response.toString());

                    // check result
                    int resultDicCode = json.getInt("code");
                    final JSONObject resultData = json.getJSONObject("data");
                    String resultDicError = json.getString("error");

                    if(resultDicCode != 0)
                    {
                        Utils.alert(context, true, "银联支付宝支付,获取订单号失败", resultDicError);

                        //
                        return;
                    }

                    //
                    String orderInfo = String.format("{\"qrCode\":\"%s\"}", resultData.getString("qrCode"));

                    /*
                     * 获取综合支付SDK对象
                     * 参数：Context
                     * */
                    UnifyPayPlugin payPlugin = UnifyPayPlugin.getInstance(context);

                    /*
                     * 新建统一支付请求类
                     *  */
                    UnifyPayRequest payRequest = new UnifyPayRequest();

                    /*
                     * 初始化支付渠道(如：微信支付)
                     * */
                    payRequest.payChannel = UnifyPayRequest.CHANNEL_ALIPAY;
                    /*
                     * 设置下单接口中返回的数据(appRequestData)
                     * */
                    payRequest.payData = orderInfo;


                    //
                    final String payRequestInfoUrlEncoded = URLEncoder.encode(resultData.toString(), "utf-8");

                    // 开始支付
                    payPlugin.sendPayRequest(payRequest);

                    //
                    fetchUmsAliPayCbList.put(orderInfo, new UmsAliPayCb() {

                        @Override
                        public void fetchUmsAliPayResult() {
                            // 获取支付结果
                            UmsPay.fetchUmsAliPayResult(context, type, orderNo, payRequestInfoUrlEncoded);
                        }
                    });
                } catch (MalformedURLException e) {
                    Utils.alert(context, true, "银联支付宝支付失败", e.toString());
                } catch (IOException | JSONException e) {
                    Utils.alert(context, true, "银联支付宝支付失败", e.toString());
                } catch (Exception e) {
                    Utils.alert(context, true, "银联支付宝支付失败", e.toString());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                    //
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                }
            }
        };

        thread.start();
    }

    private static void fetchUmsAliPayResult(final Context context, final String type, final String orderNo, final String payRequestInfo) {

        int counter = 0;
        final boolean[] keepRunning = {true};

        //
        while(keepRunning[0] && counter < 3)
        {
            //
            final CountDownLatch cdl = new CountDownLatch(1);

            //
            counter++;

            //
            final Thread thread = new Thread() {
                @Override
                public void run() {

                    //
                    URL url;
                    HttpURLConnection connection = null;

                    //
                    try {
                        String requestUrl =  String.format("%s?orderNo=%s&payRequestInfo=%s", Constants.getCofig(context, type, "ums_alipay_order_state_url"), orderNo, payRequestInfo);

                        //
                        url = new URL(requestUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(2000);
                        connection.setReadTimeout(2000);

                        //
                        InputStream in = connection.getInputStream();

                        // read response data
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        //
                        Log.d("银联支付宝支付, 结果", response.toString());

                        // translate to JSON format
                        JSONObject json = new JSONObject(response.toString());

                        // check result
                        final String resultCode = json.getString("resultCode");
                        final JSONObject resultInfo = json.getJSONObject("resultInfo");

                        // check if receice valid resultCode(with this we can infer payment's state)
                        if(!resultCode.equals("0000"))
                        {
                            //
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     // delete blank and new line char
                                     Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                                     Matcher m = p.matcher(resultInfo.toString());
                                     String sanitizedResultInfo = m.replaceAll("");

                                     //
                                     p = Pattern.compile("\"");
                                     m = p.matcher(resultInfo.toString());
                                     sanitizedResultInfo = m.replaceAll("\\\\\"");

                                     //
                                     String jsInterface = String.format("javascript:androidGtzn.umsAliPayCb(\"%s\", \"%s\")", resultCode, sanitizedResultInfo);

                                     //
                                     mainWebview.loadUrl(jsInterface);
                                 }
                             });

                            //
                            keepRunning[0] = false;
                        }
                    } catch (MalformedURLException e) {
                        Looper.prepare();
                        Toast.makeText(context, "银联支付宝支付失败: " + e.toString(), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (IOException | JSONException e) {
                        Looper.prepare();
                        Toast.makeText(context, "银联支付宝支付失败: " + e.toString(), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } catch (Exception e) {
                        Looper.prepare();
                        Toast.makeText(context, "银联支付宝支付失败: " + e.toString(), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }

                        //
                        cdl.countDown();
                    }
                }
            };

            thread.start();



            //
            try {
                //
                cdl.await();

                //
                Thread.sleep(1000);
            } catch (Exception e)
            {
                Log.d("fetchUmsAliPayResult" , e.toString());
            }
        }
    }

    public static void checkPayResult(final Context context)
    {
        if(fetchUmsAliPayCbList.size() <= 0)
        {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //
                for (Map.Entry<String, UmsAliPayCb> entry : fetchUmsAliPayCbList.entrySet()) {
                    String orderInfo = entry.getKey();
                    UmsAliPayCb payCb = entry.getValue();

                    //
                    Log.d("checkPayResult", "begin to check pay result: " + orderInfo);
                    payCb.fetchUmsAliPayResult();
                }

                //
                fetchUmsAliPayCbList.clear();
            }
        }).start();
    }
}

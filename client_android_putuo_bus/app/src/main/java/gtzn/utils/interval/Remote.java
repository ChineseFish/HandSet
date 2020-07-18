package gtzn.utils.interval;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.tongda.putuoshanlvyoubashi.MainActivity;
import com.tongda.putuoshanlvyoubashi.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gtzn.utils.aop.PrintTimeElapseAnnotation;
import gtzn.utils.log.LogUtils;

public class Remote extends BroadcastReceiver {
    private Tts tts;
    private String remoteUrl;

    //
    public Remote() {
        //
        LogUtils.setCacheQueueSize(0);

        //
        tts = new Tts();

        //
        remoteUrl = "https://mg.zhoulvkeche.com";
    }

    @Override
    @PrintTimeElapseAnnotation("Remote onReceive")
    public void onReceive(final Context context, Intent intent) {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) MyApplication.getContext().getSystemService(Context.POWER_SERVICE);

        // PARTIAL_WAKE_LOCK:保持CPU运转，屏幕和键盘灯有可能是关闭的。
        PowerManager.WakeLock wl = pm
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:PostLocationService");

        // 点亮屏幕
        wl.acquire();

        //
        final boolean[] ifKeepRunning = {true};

        while(ifKeepRunning[0])
        {
            Thread thread = new Thread() {
                @Override
                @PrintTimeElapseAnnotation("Remote fetchPayInfo")
                public void run() {
                    //
                    URL url;
                    HttpURLConnection connection = null;

                    //
                    String index = Db.getBusIdentifierIndex(Interval.identifier);

                    //
                    try {

                        String requestUrl = remoteUrl + "/ashx/GetPlayVoice.ashx?busIdentifier=" + Interval.identifier + "&index=" + index;

                        // test
                        LogUtils.d("fetchPayInfo requestUrl", requestUrl);

                        //
                        url = new URL(requestUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(2000);
                        connection.setReadTimeout(2000);

                        //
                        LogUtils.d("fetchPayInfo", "begin to fetch data");

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
                        LogUtils.d("fetchPayInfo response", response.toString());

                        // translate to JSON format
                        JSONArray speechTextList = new JSONArray(response.toString());
                        if (speechTextList.length() <= 0) {
                            return;
                        }

                        /**
                         * alarm begin
                         */
                        // fetch speech text
                        String speechText = "";
                        for (int i = 0; i < speechTextList.length(); i++) {
                            speechText += speechTextList.getJSONObject(i).getString("text");
                        }

                        // fetch new index
                        int maxIndex = speechTextList.length() - 1;
                        String newIndex = speechTextList.getJSONObject(maxIndex).getString("index");

                        /**
                         * speech
                         */

                        tts.textToSpeech(speechText);

                        // update index
                        Db.writeBusIdentifierIndex(Interval.identifier, newIndex);
                    } catch (MalformedURLException e) {
                        LogUtils.e("fetchPayInfo throw exception", e.toString());
                    } catch (IOException | JSONException e) {
                        LogUtils.e("fetchPayInfo throw exception", e.toString());
                    } catch (Exception e) {
                        LogUtils.e("fetchPayInfo throw exception", e.toString());
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }

                        // jump to scan service
                        Intent i = new Intent(context, ScanService.class);
                        context.startService(i);

                        //
                        ifKeepRunning[0] = false;
                    }
                }
            };

            thread.start();

            //
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                LogUtils.d("onReceive Thread.sleep throw exception" , e.toString());
            }
        }

        //
        wl.release();

        //
        LogUtils.d("onReceive", "onReceive end");
    }
}

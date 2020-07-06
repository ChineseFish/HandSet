package gtzn.utils.interval;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gtzn.utils.log.LogUtils;

public class Remote {
    private Tts tts;
    private String remoteUrl;
    private Boolean ifStop;

    private Handler handler;

    //
    public Remote(Handler handler) {
        this.handler = handler;

        //
        tts = new Tts();

        //
        remoteUrl = "https://mg.zhoulvkeche.com";

        //
        ifStop = false;
    }

    //
    public void fetchPayInfo(final String busIdentifier) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();

                //
                URL url;
                HttpURLConnection connection = null;

                //
                String index = Db.getBusIdentifierIndex(busIdentifier);

                //
                try {

                    String requestUrl = remoteUrl + "/ashx/GetPlayVoice.ashx?busIdentifier=" + busIdentifier + "&index=" + index;

                    // test
                    LogUtils.d("fetchPayInfo requestUrl", requestUrl);

                    //
                    url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    InputStream in = connection.getInputStream();

                    // read response data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // test
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
                    if (ifStop) {
                        return;
                    }

                    tts.textToSpeech(speechText);

                    // update index
                    Db.writeBusIdentifierIndex(busIdentifier, newIndex);
                } catch (MalformedURLException e) {
                    LogUtils.e("fetchPayInfo throw exception", e.toString());
                } catch (IOException | JSONException e) {
                    LogUtils.e("fetchPayInfo throw exception", e.toString());
                } catch (Exception e) {
                    LogUtils.e("fetchPayInfo throw exception", e.toString());
                } finally {
                    // 输出程序运行时间
                    long endTime = System.currentTimeMillis();
                    LogUtils.d("Remote" ,"fetchPayInfo run time：" + (endTime - startTime) + "ms");

                    //
                    Message msg = new Message();
                    handler.sendMessage(msg);

                    //
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        };

        thread.start();
    }

    public void stopFetchPayInfo() {
        ifStop = true;
    }
}

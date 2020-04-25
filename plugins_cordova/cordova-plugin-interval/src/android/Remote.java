package gtzn.cordova.interval;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import __PACKAGE_NAME__.MainActivity;

public class Remote {
    static private Tts tts = new Tts();
    static private String remoteUrl = "http://localhost:8080/payInfo?";

    static public void fetchPayInfo(String busIdentifier)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection connection = null;

                //
                String index = getBusIdentifierIndex(busIdentifier);

                //
                try {

                    String requestUrl = remoteUrl + "busIdentifier=" + busIdentifier + "&index=" + index;

                    // test
                    Log.d("requestUrl", requestUrl);

                    //
                    url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    InputStream in = connection.getInputStream();

                    // read response data
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // test
                    Log.d("response", response.toString());

                    // translate to JSON format
                    JSONObject jsonObject = new JSONObject(response.toString());

                    // update index
                    writeBusIdentifierIndex(busIdentifier, jsonObject.getString("index"));

                    // alarm begin
                    tts.textToSpeech(jsonObject.getString("text"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        };

        thread.start();
    }

    // 存数据
    static public void writeBusIdentifierIndex(String identifier, String index) {
        //
        SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
        editor.putString(identifier, index);
        editor.commit();

        //
        Log.d("writeBusIdentifierIndex", "key: " + identifier + ", value: " + index);
    }

    // 读数据
    static public String getBusIdentifierIndex(String identifier) {
        //
        String info = MainActivity.getSharedPreferences().getString(identifier, "0");

        Log.d("getBusIdentifierIndex", info);

        return info;
    }
}

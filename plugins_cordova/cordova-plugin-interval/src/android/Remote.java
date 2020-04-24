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
    static private String remoteUrl = "http://testaj.ziubao.com/AoJiang";

    static public void fetchPayInfo(String busIdentifier)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection connection = null;


                //
                getBusIdentifierIndex(busIdentifier);

                //
                writeBusIdentifierIndex(busIdentifier, "123");

                //
                String index = getBusIdentifierIndex(busIdentifier);

                //
                try {
                    url = new URL(remoteUrl + "/ShipList.php?startPort=AJ&&busIdentifier=" + busIdentifier + "&index=" + index);
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
                    System.out.println("response=" + response.toString());

                    // translate to JSON format
                    JSONObject jsonObject = new JSONObject(response.toString());

                    //
                    tts.textToSpeech("你好");
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
        String info = MainActivity.getSharedPreferences().getString(identifier, "");

        Log.d("getBusIdentifierIndex", info);

        return info;
    }
}

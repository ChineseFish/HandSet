package gtzn.cordova.interval;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import __PACKAGE_NAME__.MainActivity;

public class Remote {
    static private Tts tts = new Tts();
//    static private String remoteUrl = "http://mg.zhoulvkeche.com";
    static private String remoteUrl = "http://192.168.11.175:3000";

    static private boolean ifInited = false;

    public static void reset()
    {
        ifInited = false;
    }

    private static boolean fetchInitIndex(String busIdentifier)
    {
        if(ifInited)
        {
            return true;
        }

        URL url;
        HttpURLConnection connection = null;

        try {

            String requestUrl = remoteUrl + "/fetchInitIndex?busIdentifier=" + busIdentifier;

            // test
            Log.d("fetchInitIndex requestUrl", requestUrl);

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
            Log.d("fetchInitIndex response", response.toString());

            // translate to JSON format
            JSONObject jsonObject = new JSONObject(response.toString());

            // update index
            String initIndex = jsonObject.getString("index");
            writeBusIdentifierIndex(busIdentifier, initIndex);

            //
            ifInited = true;

            //
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


         return false;
    }

    private static void reportSpeechResult(String result)
    {
        URL url;
        HttpURLConnection connection = null;

        try {

            String requestUrl = remoteUrl + "/speechResult";

            // test
            Log.d("reportSpeechResult requestUrl", requestUrl);

            //
            url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setDoOutput(true);
            
            //
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 写入输出流
            out.print(result);
            // 立即刷新
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    //
    static public void fetchPayInfo(String busIdentifier)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                //
                if(!fetchInitIndex(busIdentifier)) {
                    Log.d("fetchInitIndex", "failed");

                    return;
                }

                //
                URL url;
                HttpURLConnection connection = null;

                //
                String index = getBusIdentifierIndex(busIdentifier);

                //
                try {

                    String requestUrl = remoteUrl + "/ashx/GetPlayVoice.ashx?busIdentifier=" + busIdentifier + "&index=" + index;

                    // test
                    Log.d("fetchPayInfo requestUrl", requestUrl);

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
                    Log.d("fetchPayInfo response", response.toString());

                    // translate to JSON format
                    JSONArray speechTextList = new JSONArray(response.toString());
                    if(speechTextList.length() <= 0)
                    {
                        return;
                    }

                    // alarm begin
                    JSONObject speechResult = new JSONObject();
                    JSONArray successList = new JSONArray();
                    JSONArray failList = new JSONArray();

                    // fetch speech text
                    String speechText = "";
                    for(int i = 0; i < speechTextList.length(); i++)
                    {
                        speechText += speechTextList.getJSONObject(i).getString("text");
                    }

                    // fetch new index
                    int maxIndex = speechTextList.length() - 1;
                    String newIndex = speechTextList.getJSONObject(maxIndex).getString("index");

                    // check index
                    if(Integer.parseInt(newIndex) <= Integer.parseInt(index))
                    {
                        speechResult.accumulate("code", 1);
                        speechResult.accumulate("msg", "index too little");

                        //
                        failList = speechTextList;
                    }
                    else
                    {
                        //
                        try
                        {
                            tts.textToSpeech(speechText);

                            // update index
                            writeBusIdentifierIndex(busIdentifier, newIndex);

                            // record success speech
                            successList = speechTextList;

                            //
                            speechResult.accumulate("code", 0);
                        }
                        catch(Exception e)
                        {
                            //
                            e.printStackTrace();

                            // record failed speech
                            failList = speechTextList;

                            //
                            speechResult.accumulate("code", 2);
                            speechResult.accumulate("msg", "text to speech failed");
                        }
                    }


                    //
                    speechResult.accumulate("sucessList", successList);
                    speechResult.accumulate("failList", failList);
                    reportSpeechResult(speechResult.toString());
                    
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

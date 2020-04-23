/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package local.bridge;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class CDVTts extends CordovaPlugin {
    private class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            // TODO Auto-generated method stub
            if (status == TextToSpeech.SUCCESS) {
                Log.i("MainActivity", "onInit: TTS引擎初始化成功");

                int supported = mSpeech.setLanguage(Locale.CHINA);

                if (supported == TextToSpeech.LANG_MISSING_DATA || supported == TextToSpeech.LANG_NOT_SUPPORTED)
                {
                    Log.i("MainActivity", "onInit: TTS引擎不支持中文");
                }
                else
                {
                    Log.i("MainActivity", "onInit: TTS引擎支持中文");
                }
            }
            else{
                Log.i("MainActivity", "onInit: TTS引擎初始化失败");
            }
        }
    }

    private TextToSpeech mSpeech = null;

    private static final String TAG = "CDVTts";

    // Used when instantiated via reflection by PluginManager
    public CDVTts() {
        mSpeech = new TextToSpeech(MainActivity.this, new TTSListener());
    }
    
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));

        if (action.equals("test")) {
            Log.d(TAG, "plugin initialized.");

            return true;
        }

        return false;
    }

    @Override
    protected void pluginInitialize() {

        super.pluginInitialize();

        Log.d(TAG, "plugin initialized.");
    }

    textToSpeach()
    {
        mSpeech.speak(content.toString(), TextToSpeech.QUEUE_FLUSH, null);
    }
}

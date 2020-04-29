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

package gtzn.cordova.tts;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CDVTts extends CordovaPlugin {
    private static final String TAG = "CDVTts";

    public static final String ERROR_INVALID_PARAMETERS = "参数格式错误";

    private Tts tts;

    // Used when instantiated via reflection by PluginManager
    public CDVTts() {

    }
    
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));

        if (action.equals("textToSpeech")) {
            String text = args.getString(0);

            if(text.length() == 0)
            {
                callbackContext.error(ERROR_INVALID_PARAMETERS);
                
                return false;
            }

            return textToSpeech(text, callbackContext);
        }

        callbackContext.error(ERROR_INVALID_PARAMETERS);

        return false;
    }

    @Override
    protected void pluginInitialize() {

        super.pluginInitialize();

        Log.d(TAG, "plugin initialized.");

        //
        tts = new Tts();
    }

    protected boolean textToSpeech(String text, CallbackContext callbackContext)
    {
        Log.d(TAG, "textToSpeech begin");
      
        // 
        tts.textToSpeech(text);

        //
        callbackContext.success();

        //
        return true;
    }
}

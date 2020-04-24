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

package gtzn.cordova.interval;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class CDVInterval extends CordovaPlugin {

    private static final String TAG = "CDVInterval";

    public static final String ERROR_INVALID_PARAMETERS = "参数格式错误";
    public static final String ERROR_INIT_THREAD = "初始化线程失败";

    private String identifier = "";
    private ScanThread scanThread;

    @SuppressLint("HandlerLeak")
    private Handler scanHandler = new Handler()
    {
        public void handleMessage(Message msg) {
            Log.d("scanHandler", "aaaaaaaaaa");
        }

    };
    
    // Used when instantiated via reflection by PluginManager
    public CDVInterval() {
        
    }
    
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));

        if (action.equals("setIndentifier")) {
            String text = args.getString(0);

            if(text.length() == 0)
            {
                callbackContext.error(ERROR_INVALID_PARAMETERS);
                
                return false;
            }

            return setIndentifier(text, callbackContext);
        }

        callbackContext.error(ERROR_INVALID_PARAMETERS);

        return false;
    }

    @Override
    protected void pluginInitialize() {

        super.pluginInitialize();

        Log.d(TAG, "plugin initialized.");
    }

    protected boolean setIndentifier(String text, CallbackContext callbackContext)
    {
        Log.d(TAG, "setIndentifier begin");
        
        //
        identifier = text;

        //
        try
        {
            scanThread = new ScanThread(scanHandler);
        }
        catch (Exception e)
        {
            callbackContext.error(ERROR_INIT_THREAD);

            return false;
            
        } 
        scanThread.start();

        //
        callbackContext.success();

        //
        return true;
    }
}

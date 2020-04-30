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

import android.util.Log;

public class CDVInterval extends CordovaPlugin {

    private static final String TAG = "CDVInterval";

    public static final String ERROR_INVALID_PARAMETERS = "参数格式错误";
    public static final String ERROR_INIT_THREAD = "初始化线程失败";

    private Interval interval;

    // Used when instantiated via reflection by PluginManager
    public CDVInterval() {
        interval = new Interval();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("%s is called. Callback ID: %s.", action, callbackContext.getCallbackId()));

        if (action.equals("setIndentifier")) {
            String identifier = args.getString(0);
            String index = args.getString(1);

            //
            if (identifier.length() == 0) {
                callbackContext.error(ERROR_INVALID_PARAMETERS);

                return false;
            }

            //
            if (index.length() == 0) {
                callbackContext.error(ERROR_INVALID_PARAMETERS);

                return false;
            }

            return setIndentifier(identifier, index, callbackContext);
        }

        callbackContext.error(ERROR_INVALID_PARAMETERS);

        return false;
    }

    @Override
    protected void pluginInitialize() {

        super.pluginInitialize();

        Log.d(TAG, "plugin initialized.");
    }

    protected boolean setIndentifier(String identifier, String index, CallbackContext callbackContext) {
        Log.d(TAG, "setIndentifier begin");

        //
        if (interval.start(identifier, index)) {
            callbackContext.success();

            return true;
        }

        //
        callbackContext.error(ERROR_INIT_THREAD);

        return false;
    }
}

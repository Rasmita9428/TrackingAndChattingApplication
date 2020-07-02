/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rasmitap.tailwebs_assigment2;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.Config;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rasmitap.tailwebs_assigment2.utils.Utility;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    Config config;
    private SharedPreferences sharedPreferences;
    private String refreshedToken;


    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sharedPreferences = getSharedPreferences("FIREBASE", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TOKEN", refreshedToken);
        editor.commit();
        storeToken(refreshedToken);

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private void storeToken(String token) {
        //saving the token on shared preferences
        Utility.getInstance(getApplicationContext()).saveDeviceToken(token);
//        ParsingHelper.GetAPI(config.reg_deviceId,config.refreshedToken,config.reg_deviceName,config.reg_eDevicetype,"1");
    }
}

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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rasmitap.tailwebs_assigment2.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static int notify_id = 0;
    String msg1;
    String jobtype;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            generateNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    // [END receive_message]
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");
            String message = data.getString("message");

            String CurrentString = message;
            //creating MyNotificationManager object
            sendNotification(message);
            // generateNotification(message);
            // Check if message contains a notification payload.
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */


    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */


    public void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.customer)
                .setContentTitle("Pickcab Driver")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    public void generateNotification(String CommonMsg) {
        notify_id++;
        String tripmessage = "", message = "";
//        Notification notification;
        message = CommonMsg;
        SharedPreferences sharedPreferences;
        try {
            if (jobtype != null && !jobtype.isEmpty() && jobtype.length() != 0 && jobtype.equalsIgnoreCase("newtrips")) {

                tripmessage = message;
                sharedPreferences = getSharedPreferences("PUSH_NOTIFICATION", 0);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("PUSH_TYPE", "newtrips");
                edit.commit();

            } else if (jobtype != null && !jobtype.isEmpty() && jobtype.length() != 0 && jobtype.equalsIgnoreCase("past")) {

                tripmessage = message;
                sharedPreferences = getSharedPreferences("PUSH_NOTIFICATION", 0);
                SharedPreferences.Editor edit1 = sharedPreferences.edit();
                edit1.putString("PUSH_TYPE", "past");
                edit1.commit();

            } else if (jobtype != null && !jobtype.isEmpty() && jobtype.length() != 0 && jobtype.equalsIgnoreCase("upcoming")) {

                tripmessage = message;
                sharedPreferences = getSharedPreferences("PUSH_NOTIFICATION", 0);
                SharedPreferences.Editor edit1 = sharedPreferences.edit();
                edit1.putString("PUSH_TYPE", "upcoming");
                edit1.commit();

            } else if (jobtype != null && !jobtype.isEmpty() && jobtype.length() != 0 && jobtype.equalsIgnoreCase("inprogress")) {

                tripmessage = message;
                sharedPreferences = getSharedPreferences("PUSH_NOTIFICATION", 0);
                SharedPreferences.Editor edit1 = sharedPreferences.edit();
                edit1.putString("PUSH_TYPE", "inprogress");
                edit1.commit();

            } else if (jobtype != null && !jobtype.isEmpty() && jobtype.length() != 0 && jobtype.equalsIgnoreCase("cancel")) {

                tripmessage = message;
                sharedPreferences = getSharedPreferences("PUSH_NOTIFICATION", 0);
                SharedPreferences.Editor edit1 = sharedPreferences.edit();
                edit1.putString("PUSH_TYPE", "cancel");
                edit1.commit();

            } else {

                tripmessage = "";
                sharedPreferences = getSharedPreferences("PUSH_NOTIFICATION", 0);
                SharedPreferences.Editor edit1 = sharedPreferences.edit();
                edit1.putString("PUSH_TYPE", "");
                edit1.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //sendNotification(tripmessage);


    }
}

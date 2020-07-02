package com.rasmitap.tailwebs_assigment2.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.SystemClock;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.rasmitap.tailwebs_assigment2.QuickBlox.QBUserHolder;
import com.rasmitap.tailwebs_assigment2.R;
import com.rasmitap.tailwebs_assigment2.UserListActivity;
import com.rasmitap.tailwebs_assigment2.utils.ConstantStore;
import com.rasmitap.tailwebs_assigment2.utils.GPSTracker;
import com.rasmitap.tailwebs_assigment2.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textlogout,btn_trackhistroy;
    LinearLayout ll_track;
    private long mLastClickTime = 0;
GPSTracker gps;
    static final String APP_ID = "84532";
    static final String AUTH_KEY = "7Z7WuzSsR9-Vueq";
    static final String AUTH_SECRET = "k7fhRQBM4TjVg4v";
    static final String ACCOUNT_KEY = "c2q8NoefDwRWLZ71Qfzm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textlogout=findViewById(R.id.textlogout);
        btn_trackhistroy=findViewById(R.id.btn_trackhistroy);
        ll_track=findViewById(R.id.ll);
        textlogout.setOnClickListener(this);
        ll_track.setOnClickListener(this);
        btn_trackhistroy.setOnClickListener(this);
        gps=new GPSTracker(MainActivity.this);
        QBSettings.getInstance().isEnablePushNotification();
        QBChatService.setDebugEnabled(true); // enable chat logging

        QBChatService.setDefaultPacketReplyTimeout(10000);
        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatServiceConfigurationBuilder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);

        try {
            createSession();
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
        initSession();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textlogout:
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                openLogoutConfirmDialog();

                break;
            case R.id.btn_trackhistroy:
//                Intent intent = new Intent(MainActivity.this, UserListActivity.class);
//                startActivity(intent);
//                finish();

                break;
            case R.id.ll:
                Intent intent1=new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent1);
                finish();


                break;
        }
    }

    public void openLogoutConfirmDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_confirm_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        TextView tv_no = (TextView) dialog.findViewById(R.id.tv_no);
        TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes);
        TextView txt_logout_title = (TextView) dialog.findViewById(R.id.txt_logout_title);
        TextView txt_logout_tagline = (TextView) dialog.findViewById(R.id.txt_logout_tagline);

        txt_logout_title.setText("Logout");
        txt_logout_tagline.setText("Are you sure you want to logout?");
        tv_yes.setText("Yes");
        tv_no.setText("No");

        tv_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }

        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Utility.clearPreference(MainActivity.this);

                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.windowAnimations = R.style.DialogAnimation;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
    }

    private void createSessionforChat() {


        String user = Utility.getStringSharedPreferences(getApplicationContext(), ConstantStore.UserName);
        String password = Utility.getStringSharedPreferences(getApplicationContext(), ConstantStore.Password);

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUserHolder.getInstance().putUsers(qbUsers);

            }

            @Override
            public void onError(QBResponseException e) {

                // Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                try {
                    qbUser.setId(qbSession.getUserId());
                    qbUser.setPassword(BaseService.getBaseService().getToken());

                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onError(QBResponseException e) {
                 Toast.makeText(getApplicationContext(),"chatservice"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void createSession() throws BaseServiceException {

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        QBAuth.getBaseService().setToken("bede1c97d992755ba5ceae43b76ded687c71a1c2");

    }

    private void initSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                createSessionforChat();

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

}
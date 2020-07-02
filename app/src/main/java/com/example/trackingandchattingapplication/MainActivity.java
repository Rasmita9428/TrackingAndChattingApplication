package com.example.trackingandchattingapplication;

import android.app.Dialog;
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

import androidx.appcompat.app.AppCompatActivity;

import com.rasmitap.tailwebs_assigment2.R;
import com.rasmitap.tailwebs_assigment2.utils.ConstantStore;
import com.rasmitap.tailwebs_assigment2.utils.GPSTracker;
import com.rasmitap.tailwebs_assigment2.utils.Utility;
import com.rasmitap.tailwebs_assigment2.view.LoginActivity;
import com.rasmitap.tailwebs_assigment2.view.MapActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textlogout,btn_trackhistroy;
    LinearLayout ll_track;
    private long mLastClickTime = 0;
    GPSTracker gps;
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

                break;
            case R.id.ll:
                if (gps != null) {
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        ConstantStore.LATITUDE = gps.getLatitude();
                        ConstantStore.LONGITUDE = gps.getLongitude();
                        String lata = String.valueOf(latitude);
                        String longt = String.valueOf(longitude);
                        if (!lata.equalsIgnoreCase("0.0") && !longt.equalsIgnoreCase("0.0")) {
                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                            intent.putExtra("latitude", lata);
                            intent.putExtra("Longitide", longt);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

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
}
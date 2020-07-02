package com.rasmitap.tailwebs_assigment2.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rasmitap.tailwebs_assigment2.R;
import com.rasmitap.tailwebs_assigment2.db.DatabaseHelper;
import com.rasmitap.tailwebs_assigment2.model.LoginData;
import com.rasmitap.tailwebs_assigment2.utils.ConstantStore;
import com.rasmitap.tailwebs_assigment2.utils.GlobalMethods;
import com.rasmitap.tailwebs_assigment2.utils.Utility;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView img_back_login, img_no_avtar;

    TextView txt_login_title, txt_login_desc, btn_login, txt_signup_login;

    EditText edt_user_login, edt_password_login;

    private long lastClickTime = 0;
    private DatabaseHelper databaseHelper;
    LoginData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        img_no_avtar = findViewById(R.id.img_no_avtar);
        btn_login = findViewById(R.id.btn_login);
        edt_user_login = findViewById(R.id.edt_user_login);
        edt_password_login = findViewById(R.id.edt_password_login);
        txt_signup_login = findViewById(R.id.txt_signup_login);

        btn_login.setOnClickListener(this);
        txt_signup_login.setOnClickListener(this);
        databaseHelper = new DatabaseHelper(LoginActivity.this);
        user = new LoginData();

    }
    private long mLastClickTime = 0;

    @Override
    public void onClick(View view) {

        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {

        } else {
            switch (view.getId()) {
                case R.id.btn_login:

                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (edt_user_login.getText().toString().equalsIgnoreCase("")) {
                        GlobalMethods.Dialog(LoginActivity.this, "Please enter username");
                    } else if (edt_password_login.getText().toString().equalsIgnoreCase("")) {
                        GlobalMethods.Dialog(LoginActivity.this, "Please enter password");
                    } else {
                            LoginApi(edt_user_login.getText().toString(), edt_password_login.getText().toString());

                    }
                    break;
                case R.id.txt_signup_login:
                    Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                    startActivity(intent);

            }
        }
        lastClickTime = SystemClock.elapsedRealtime();

    }

    public void LoginApi(final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        if (databaseHelper.checkUser(email.trim(),password.trim())) {
            MoveDialog(LoginActivity.this,"User Login Successfully");
            Utility.setStringSharedPreference(getApplicationContext(), ConstantStore.is_Login,"true");
            Utility.setStringSharedPreference(getApplicationContext(), ConstantStore.UserName,email);
            Utility.setStringSharedPreference(getApplicationContext(), ConstantStore.Password,password);

            progressDialog.dismiss();

        }else{
            progressDialog.dismiss();
            GlobalMethods.Dialog(LoginActivity.this, "User Name or Password Not Valid");

        }

    }

    public void MoveDialog(final Context context, String msg) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView txt_dialog_msg = dialog.findViewById(R.id.txt_dialog_msg);
        TextView txt_dialog_ok = dialog.findViewById(R.id.txt_dialog_ok);

        txt_dialog_msg.setText(msg);
        txt_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wlp.windowAnimations = R.style.DialogAnimation;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        dialog.show();


    }

}

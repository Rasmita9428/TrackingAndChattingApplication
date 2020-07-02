package com.rasmitap.tailwebs_assigment2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.rasmitap.tailwebs_assigment2.QuickBlox.QBUserHolder;
import com.rasmitap.tailwebs_assigment2.view.ChatActivity;
import com.rasmitap.tailwebs_assigment2.view.UserListAdapter;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {


    ImageView img_back;
    ListView lv_user_list;
    ProgressDialog dialog;
    ArrayList<QBUser> user_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        img_back = findViewById(R.id.img_back);
        lv_user_list = findViewById(R.id.lv_user_list);

        //createSessionforChat();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lv_user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                QBUser qbUser = (QBUser) lv_user_list.getItemAtPosition(position);

                createPrivateChat(qbUser);

            }
        });

        user_list = new ArrayList<>();
        getAllUser(1);
        dialog = new ProgressDialog(UserListActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }

    private void createPrivateChat(QBUser qbUser) {

        final ProgressDialog dialog = new ProgressDialog(UserListActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        QBChatDialog dialog1 = DialogUtils.buildPrivateDialog(qbUser.getId());

        QBRestChatService.createChatDialog(dialog1).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                dialog.dismiss();
                // Toast.makeText(getApplicationContext(),"Private Chat Dialog Created Successfully",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("DIALOG_EXTRA", qbChatDialog);
                startActivity(intent);

            }

            @Override
            public void onError(QBResponseException e) {

                //  Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getAllUser(int i) {


        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(i);
        pagedRequestBuilder.setPerPage(10);
        QBUsers.getUsers(pagedRequestBuilder, Bundle.EMPTY);

        QBUsers.getUsers(pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUserHolder.getInstance().putUsers(qbUsers);

                int total_pages = bundle.getInt("total_pages");
                int current_page = bundle.getInt("current_page");

                for (QBUser user : qbUsers) {
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())) {
                        String user_id = user.getLogin().toString();
                        String first_chara = Character.toString(user_id.charAt(0));
                        if (first_chara.equalsIgnoreCase("t")) {
                            user_list.add(user);

                        }

                    }
                }

                if (current_page < total_pages) {
                    current_page++;
                    getAllUser(current_page);
                } else {
                    UserListAdapter userListAdapter = new UserListAdapter(getBaseContext(), user_list);
                    lv_user_list.setAdapter(userListAdapter);
                    userListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }


                // Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(QBResponseException e) {

                // Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });

    }

   /* private void createSessionforChat() {

        final ProgressDialog dialog = new ProgressDialog(UserListActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        String user = Utility.getStringSharedPreferences(getApplicationContext(),CommanKey.qb_user_name);
        String password = Utility.getStringSharedPreferences(getApplicationContext(),CommanKey.qb_password);

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUserHolder.getInstance().putUsers(qbUsers);

            }

            @Override
            public void onError(QBResponseException e) {

             //   Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        final QBUser qbUser = new QBUser(user,password);

        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {

                        dialog.dismiss();

                        getAllUser();

                    }

                    @Override
                    public void onError(QBResponseException e) {

                        dialog.dismiss();

                       // Toast.makeText(getApplicationContext(),"login"+e.getMessage(),Toast.LENGTH_SHORT).show();

                       if(e.getMessage().equalsIgnoreCase("You have already logged in chat"))
                       {
                           getAllUser();
                       }



                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {

              //  Toast.makeText(getApplicationContext(),"chatservice"+e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });


    }*/

}

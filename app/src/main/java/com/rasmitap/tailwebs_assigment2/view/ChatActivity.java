package com.rasmitap.tailwebs_assigment2.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.Utils;
import com.quickblox.core.exception.QBResponseException;
import com.rasmitap.tailwebs_assigment2.QuickBlox.QBChatMessagesHolder;
import com.rasmitap.tailwebs_assigment2.QuickBlox.QbEntityCallbackTwoTypeWrapper;
import com.rasmitap.tailwebs_assigment2.R;
import com.rasmitap.tailwebs_assigment2.utils.GlobalMethods;
import com.facebook.CallbackManager;

import org.jivesoftware.smack.SmackException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements QBChatDialogMessageListener {
    ListView list_msg;
    ImageView img_send;
    EditText et_msg;
    QBChatDialog qbChatDialog;
    ImageView img_back;
    TextView tv_title;
    ImageView img_attach;

    int dummy_image_position;

    ChatMessageAdpter chatMessageAdpter;

    private int STORAGE_PERMISSION_CODE = 23;
    private int CAMERA_CODE = 25;

    private static int RESULT_LOAD_IMAGE = 1;

    public static final int PHOTO_TAKE_CAMERA = 5;

    private CallbackManager callbackManager;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initview();

        initChatDialog();

        retriveAllMsg();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();

            }
        });

        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = et_msg.getText().toString();

                if(!msg.isEmpty() || !msg.equalsIgnoreCase("")) {

                    QBChatMessage qbChatMessage = new QBChatMessage();
                    qbChatMessage.setBody(et_msg.getText().toString());
                    qbChatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                    qbChatMessage.setSaveToHistory(true);
                    Long tsLong = System.currentTimeMillis() / 1000;
                    qbChatMessage.setDateSent(tsLong);

                    try {
                        qbChatDialog.sendMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }

                    //put msg to cache

                    QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), qbChatMessage);
                    ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId());

                    chatMessageAdpter = new ChatMessageAdpter(context, messages);
                    list_msg.setAdapter(chatMessageAdpter);
                    list_msg.setSelection(chatMessageAdpter.getCount() - 1);
                    chatMessageAdpter.notifyDataSetChanged();

                    //remove text from editext

                    et_msg.setText("");
                    et_msg.setFocusable(true);

                }
                else {
                    Toast.makeText(getApplicationContext(),"Please Enter Message",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void retriveAllMsg() {

        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);

        if ((qbChatDialog != null))
        {

            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(),qbChatMessages);

                    chatMessageAdpter = new ChatMessageAdpter(context,qbChatMessages);
                    list_msg.setAdapter(chatMessageAdpter);
                    list_msg.setSelection(chatMessageAdpter.getCount() - 1);
                    chatMessageAdpter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });

        }

    }

    private void initChatDialog() {

        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra("DIALOG_EXTRA");
        tv_title.setText(qbChatDialog.getName());

        qbChatDialog.initForChat(QBChatService.getInstance());

        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();

        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });

        qbChatDialog.addMessageListener(this);


    }

    private void initview() {

        context = ChatActivity.this;

        list_msg = findViewById(R.id.list_msg);
        img_send = findViewById(R.id.img_send);
        et_msg = findViewById(R.id.et_msg);
        tv_title = findViewById(R.id.tv_title);
        img_back = findViewById(R.id.img_back);
        img_attach = findViewById(R.id.img_attach);

        callbackManager = CallbackManager.Factory.create();

    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

        QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(),qbChatMessage);

        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatMessage.getDialogId());

        chatMessageAdpter = new ChatMessageAdpter(context,messages);
        list_msg.setAdapter(chatMessageAdpter);
        list_msg.setSelection(chatMessageAdpter.getCount() - 1);
        chatMessageAdpter.notifyDataSetChanged();

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

        //  Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initChatDialog();

        //retriveAllMsg();
    }

    private void showPictureDialog() {

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                //CameraPermission();
                                takePhotoFromCamera();
                                break;

                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {

        if(isCameraAllowed()) {

            startActivityForResult(getPickImageChooserIntent(), PHOTO_TAKE_CAMERA);

            Uri outputFileUri = getCaptureImageOutputUri();

            List<Intent> allIntents = new ArrayList<>();
            PackageManager packageManager = getPackageManager();

            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                }
                allIntents.add(intent);
            }
        }
        else {
            requestCameraPermission();
        }
    }

    private void choosePhotoFromGallary() {

        if(isReadStorageAllowed()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
        }
        {
            requestStoragePermission();
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        callbackManager.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (reqCode == RESULT_LOAD_IMAGE) {

                Uri uri = data.getData();

                String filePath = GlobalMethods.getImagePath(getApplicationContext(), uri);

                File myFile = new File(filePath);

                uploadImage(myFile, new QBEntityCallback<QBAttachment>() {
                    @Override
                    public void onSuccess(QBAttachment qbAttachment, Bundle bundle) {

                        // Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT);

                    }

                    @Override
                    public void onError(QBResponseException e) {

                        // Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT);

                    }

                }, new QBProgressCallback() {
                    @Override
                    public void onProgressUpdate(final int progress) {

                        Log.e("pp",""+progress);
                        if(progress == 0) {

                            QBChatMessage qbChatMessage = new QBChatMessage();
                            qbChatMessage.setSenderId(9999);
                            qbChatMessage.setSaveToHistory(true);

                            QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), qbChatMessage);
                            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId());

                            dummy_image_position = messages.size()-1;

                            chatMessageAdpter = new ChatMessageAdpter(context, messages);
                            list_msg.setAdapter(chatMessageAdpter);
                            list_msg.setSelection(chatMessageAdpter.getCount() - 1);
                            chatMessageAdpter.notifyDataSetChanged();
                        }

                    }

                });

            }
            else if(reqCode == PHOTO_TAKE_CAMERA )
            {
                if (getPickImageResultUri(data) != null) {

                    Uri picUri = getPickImageResultUri(data);

                    String filePath = GlobalMethods.getImagePath(getApplicationContext(), picUri);

                    File myFile = new File(filePath);

                    uploadImage(myFile, new QBEntityCallback<QBAttachment>() {
                        @Override
                        public void onSuccess(QBAttachment qbAttachment, Bundle bundle) {



                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }

                    }, new QBProgressCallback() {
                        @Override
                        public void onProgressUpdate(final int progress) {

                            if(progress == 0) {

                                QBChatMessage qbChatMessage = new QBChatMessage();
                                qbChatMessage.setSenderId(9999);
                                qbChatMessage.setSaveToHistory(true);

                                QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), qbChatMessage);
                                ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId());

                                dummy_image_position = messages.size()-1;

                                chatMessageAdpter = new ChatMessageAdpter(context, messages);
                                list_msg.setAdapter(chatMessageAdpter);
                                list_msg.setSelection(chatMessageAdpter.getCount() - 1);
                                chatMessageAdpter.notifyDataSetChanged();
                            }

                        }

                    });

                }

                else {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap myBitmap = bitmap;
                }
            }

        }
    }

    private void uploadImage(File file, QBEntityCallback<QBAttachment> callback,
                             QBProgressCallback progressCallback)
    {
        QBContent.uploadFileTask(file, true, null, progressCallback).performAsync(
                new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {

                        QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);
                        attachment.setId(qbFile.getId().toString());
                        attachment.setUrl(qbFile.getPublicUrl());


                        ArrayList<QBChatMessage> messagesForRemove = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId());
                        QBChatMessagesHolder.getInstance().removeMessage(qbChatDialog.getDialogId(),dummy_image_position);

                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.addAttachment(attachment);
                        qbChatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        qbChatMessage.setSaveToHistory(true);

                        try {
                            qbChatDialog.sendMessage(qbChatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(),qbChatMessage);
                        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId());

                        chatMessageAdpter = new ChatMessageAdpter(context,messages);
                        list_msg.setAdapter(chatMessageAdpter);
                        list_msg.setSelection(chatMessageAdpter.getCount() - 1);
                        chatMessageAdpter.notifyDataSetChanged();

                        Log.e("set",""+"set");

                    }
                });

    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                //choosePhotoFromGallary();
                //Displaying a toast
                //Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                //  Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == CAMERA_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                //choosePhotoFromGallary();
                //Displaying a toast
                //Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                // Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private boolean isCameraAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

}
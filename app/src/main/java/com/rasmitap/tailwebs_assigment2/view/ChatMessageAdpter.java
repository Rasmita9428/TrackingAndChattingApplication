package com.rasmitap.tailwebs_assigment2.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.rasmitap.tailwebs_assigment2.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by admin on 13/12/17.
 */

public class ChatMessageAdpter extends BaseAdapter {

    private Context context;

    public ChatMessageAdpter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    private ArrayList<QBChatMessage> qbChatMessages;

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        int viewType = getItemViewType(position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final QBChatMessage qbChatMessage = qbChatMessages.get(position);

        String todaydate = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

        long chat_date = qbChatMessage.getDateSent();

        String str_chat_date = getDateCurrentTimeZone(chat_date);

        Log.e("ss",""+str_chat_date);

        Log.e("tt",""+todaydate);

        if(qbChatMessage.getSenderId().equals(QBChatService.getInstance().getUser().getId()))
        {
            view = layoutInflater.inflate(R.layout.send_msg_row, null);

            BubbleTextView txt_send;

            ImageView img_send_image;

            RelativeLayout rl_send_image;

            TextView txt_chat_date;

            txt_send = view.findViewById(R.id.txt_send);
            img_send_image = view.findViewById(R.id.img_send_image);
            txt_chat_date = view.findViewById(R.id.txt_chat_date);
            rl_send_image = view.findViewById(R.id.rl_send_image);

            if(chat_date != 0) {
                if (position == 0) {
                    txt_chat_date.setVisibility(View.VISIBLE);
                    if (todaydate.equalsIgnoreCase(str_chat_date)) {
                        txt_chat_date.setText("Today");
                    } else {
                        txt_chat_date.setText(str_chat_date);
                    }
                } else {

                    long pre_chat_date = qbChatMessages.get(position - 1).getDateSent();

                    String str_pre_chat_date = getDateCurrentTimeZone(pre_chat_date);


                    if (!str_chat_date.equalsIgnoreCase(str_pre_chat_date)) {
                        txt_chat_date.setVisibility(View.VISIBLE);
                        if (todaydate.equalsIgnoreCase(str_chat_date)) {
                            txt_chat_date.setText("Today");
                        } else {
                            txt_chat_date.setText(str_chat_date);
                        }
                    }
                }
            }
            /*else
            {
                if(position != 0)
                {
                    long pre_chat_date = qbChatMessages.get(position - 1).getDateSent();

                    String str_pre_chat_date = getDateCurrentTimeZone(pre_chat_date);

                    if(pre_chat_date != 0) {
                        if (!str_pre_chat_date.equalsIgnoreCase(todaydate)) {
                            txt_chat_date.setVisibility(View.VISIBLE);
                            txt_chat_date.setText("Today");
                        }
                    }
                }
                else
                {
                    txt_chat_date.setVisibility(View.VISIBLE);
                    txt_chat_date.setText("Today");
                }

            }*/

            if(hasAttachments(qbChatMessage))
            {
                Collection<QBAttachment> attachments = qbChatMessages.get(position).getAttachments();
                final QBAttachment qbAttachment = attachments.iterator().next();

                img_send_image.setVisibility(View.VISIBLE);

                rl_send_image.setVisibility(View.VISIBLE);

                Picasso.with(context).load(qbAttachment.getUrl()).placeholder(R.drawable.ic_photo).into(img_send_image);

                txt_send.setVisibility(View.GONE);

                img_send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = qbAttachment.getUrl();

                        Preview_Image(url);

                    }
                });

            }
            else {
                txt_send.setVisibility(View.VISIBLE);
                txt_send.setText(qbChatMessage.getBody());

            }

        }
        else if(qbChatMessage.getSenderId().equals(9999))
        {

            view = layoutInflater.inflate(R.layout.send_msg_row, null);

            BubbleTextView txt_send;

            ImageView img_send_image;

            RelativeLayout rl_send_image;

            TextView txt_chat_date,txt_upload_progress;

            txt_send = view.findViewById(R.id.txt_send);
            txt_upload_progress = view.findViewById(R.id.txt_upload_progress);
            img_send_image = view.findViewById(R.id.img_send_image);
            txt_chat_date = view.findViewById(R.id.txt_chat_date);
            rl_send_image = view.findViewById(R.id.rl_send_image);

            txt_send.setVisibility(View.GONE);
            rl_send_image.setVisibility(View.VISIBLE);
            txt_upload_progress.setVisibility(View.VISIBLE);

        }

        else
        {
            view = layoutInflater.inflate(R.layout.receive_msg_row, null);

            BubbleTextView txt_receive;

            ImageView img_send_image;

            TextView txt_chat_date;

            RelativeLayout rl_send_image;

            txt_receive = view.findViewById(R.id.txt_receive);
            txt_chat_date = view.findViewById(R.id.txt_chat_date);
            img_send_image = view.findViewById(R.id.img_send_image);
            rl_send_image = view.findViewById(R.id.rl_send_image);



            txt_receive.setText(qbChatMessages.get(position).getBody());

            if(chat_date != 0) {
                if (position == 0) {
                    txt_chat_date.setVisibility(View.VISIBLE);
                    if (todaydate.equalsIgnoreCase(str_chat_date)) {
                        txt_chat_date.setText("Today");
                    } else {
                        txt_chat_date.setText(str_chat_date);
                    }
                } else {

                    long pre_chat_date = qbChatMessages.get(position - 1).getDateSent();

                    String str_pre_chat_date = getDateCurrentTimeZone(pre_chat_date);

                    Log.e("pp", "" + str_pre_chat_date);

                    if (!str_chat_date.equalsIgnoreCase(str_pre_chat_date)) {
                        txt_chat_date.setVisibility(View.VISIBLE);
                        if (todaydate.equalsIgnoreCase(str_chat_date)) {
                            txt_chat_date.setText("Today");
                        } else {
                            txt_chat_date.setText(str_chat_date);
                        }
                    }
                }
            }

            if(hasAttachments(qbChatMessage))
            {
                Collection<QBAttachment> attachments = qbChatMessages.get(position).getAttachments();
                final QBAttachment qbAttachment = attachments.iterator().next();

                img_send_image.setVisibility(View.VISIBLE);
                rl_send_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(qbAttachment.getUrl()).placeholder(R.drawable.ic_photo).into(img_send_image);

                txt_receive.setVisibility(View.GONE);

                img_send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = qbAttachment.getUrl();

                        Preview_Image(url);

                    }
                });


            }
            else {
                txt_receive.setVisibility(View.VISIBLE);
                txt_receive.setText(qbChatMessages.get(position).getBody());
            }




        }

        return view;
    }

    private boolean hasAttachments(QBChatMessage chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        return attachments != null && !attachments.isEmpty();
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            //calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    private void Preview_Image(String url) {

        final Dialog open_image = new Dialog(context);
        open_image.requestWindowFeature(Window.FEATURE_NO_TITLE);
        open_image.setContentView(R.layout.dialog_image_preview);
        open_image.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        open_image.setCancelable(true);

        ImageView img_preview = open_image.findViewById(R.id.img_preview);

        Picasso.with(context).load(url).placeholder(R.drawable.ic_photo).into(img_preview);

        Window window = open_image.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        open_image.show();


    }

}
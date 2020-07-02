package com.rasmitap.tailwebs_assigment2.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;
import com.rasmitap.tailwebs_assigment2.R;

import java.util.ArrayList;

/**
 * Created by admin on 12/12/17.
 */

public class UserListAdapter extends BaseAdapter {

    Context context;
    ArrayList<QBUser> arrayList;
    String UserLetter;

    public UserListAdapter(Context context, ArrayList<QBUser> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        TextView txt_user, txt_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.userlist_row, null);

            holder = new ViewHolder();
            holder.txt_user = (TextView) convertView.findViewById(R.id.txt_user);
            holder.txt_image = (TextView) convertView.findViewById(R.id.txt_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_user.setText(arrayList.get(position).getFullName());
        String UserText = arrayList.get(position).getFullName();
        if (UserText != null) {
            UserLetter = UserText.substring(0, 1);
            UserLetter = UserLetter.toUpperCase();
        }

        holder.txt_image.setText(UserLetter);


        return convertView;
    }
}

package com.rasmitap.tailwebs_assigment2.QuickBlox;

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 12/12/17.
 */

public class QBUserHolder {

    public static QBUserHolder instance;
    private SparseArray<QBUser> qbUserSparseArray;

    public static synchronized QBUserHolder getInstance()
    {
        if(instance == null)
        {
            instance = new QBUserHolder();

        }

        return instance;
    }

    private QBUserHolder()
    {
        qbUserSparseArray = new SparseArray<>();
    }

    public void putUsers(List<QBUser> users)
    {
        for(QBUser user:users)
        {
            putUsers(user);
        }
    }

    private void putUsers(QBUser user) {

        qbUserSparseArray.put(user.getId(),user);
    }

    public QBUser getUserById(int id)
    {
        return qbUserSparseArray.get(id);
    }

    public List<QBUser> getUsersByIds(List<Integer> ids)
    {
        List<QBUser> qbUser = new ArrayList<>();

        for(Integer id : ids)
        {
            QBUser user = getUserById(id);
            if(user != null)
            {
                qbUser.add(user);
            }
        }

        return qbUser;
    }

}

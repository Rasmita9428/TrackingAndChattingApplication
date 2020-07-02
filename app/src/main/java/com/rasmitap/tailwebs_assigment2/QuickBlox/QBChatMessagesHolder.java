package com.rasmitap.tailwebs_assigment2.QuickBlox;


import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 13/12/17.
 */

public class QBChatMessagesHolder {

    private static QBChatMessagesHolder instance;

    private HashMap<String, ArrayList<QBChatMessage>> qbChatMessageArray;


    public static synchronized QBChatMessagesHolder getInstance()
    {
        QBChatMessagesHolder qbChatMessagesHolder;

        synchronized (QBChatMessagesHolder.class)
        {
            if(instance == null)
                instance = new QBChatMessagesHolder();
                qbChatMessagesHolder = instance;

        }

        return qbChatMessagesHolder;

    }

    private QBChatMessagesHolder()
    {
        this.qbChatMessageArray = new HashMap<>();
    }

    public void putMessages(String dialogId, ArrayList<QBChatMessage> qbChatMessages)
    {
        this.qbChatMessageArray.put(dialogId,qbChatMessages);
    }

    public void putMessage(String dialogId, QBChatMessage qbChatMessage)
    {
        List<QBChatMessage> lstResult = this.qbChatMessageArray.get(dialogId);
        lstResult.add(qbChatMessage);
        ArrayList<QBChatMessage> lstAdded = new ArrayList<>(lstResult.size());
        lstAdded.addAll(lstResult);
        putMessages(dialogId,lstAdded);
    }

    public ArrayList<QBChatMessage> getChatMessagesByDialogId(String dialogId)
    {
        return (ArrayList<QBChatMessage>) this.qbChatMessageArray.get(dialogId);
    }

    public void removeMessage(String dialogId, int position)
    {
        List<QBChatMessage> lstResult = this.qbChatMessageArray.get(dialogId);
        lstResult.remove(position);
        ArrayList<QBChatMessage> lstAdded = new ArrayList<>(lstResult.size());
        lstAdded.addAll(lstResult);
        putMessages(dialogId,lstAdded);
    }


}

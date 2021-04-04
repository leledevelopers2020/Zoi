package com.zoiapp.zoi.ModalClasses;

import android.view.View;
import android.widget.TextView;

import com.zoiapp.zoi.R;

public class NotificationModelClass {
    private TextView bellCount;
    private String notificationTitle,notificationDate,
            notificationTime,notificationMessage,notificationType;
    private int notificationCount;

    public NotificationModelClass() {
    }

    public NotificationModelClass(View view)
    {
        bellCount = view.findViewById(R.id.bell_count);
    }

    public void updateNotificationCount(int count)
    {
        if(count==0)
        {
            bellCount.setVisibility(View.INVISIBLE);
        }
        else
        {
            bellCount.setVisibility(View.VISIBLE);
            bellCount.setText(String.valueOf(count));
        }
    }
    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}

package com.zoiapp.zoi.Notifications;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zoiapp.zoi.History.History;
import com.zoiapp.zoi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FireMsgService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    public void sendOrderNotification(String typeOfOrder, Context baseContext)
    {
        Intent intent=new Intent(baseContext, History.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(baseContext,0,intent,0);
        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(baseContext,"channel1")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .setContentTitle("Your Order")
                .setContentIntent(pendingIntent)
                .setContentText("Order has been placed through "+typeOfOrder+".\n Click here to view.")
                .setSound(path)
                .setOngoing(true)
                .setAutoCancel(true);
        NotificationManagerCompat manager=NotificationManagerCompat.from(baseContext);
        Object ob = new Object();
        manager.notify(ob.hashCode(),builder.build());

        uploadNotification(typeOfOrder);
    }

    private void uploadNotification(String typeOfOrder) {
        DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("notifications")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("myNotification").document();
        Map<String, Object> items = new HashMap<>();
        items.put("notificationTitle", "You have a order through "+typeOfOrder);
        items.put("notificationMessage", "Click here to view");
        items.put("notificationDate", DateFormat.getDateInstance().format(new Date()));
        items.put("notificationTime", DateFormat.getTimeInstance().format(new Date()));
        documentReference2.set(items).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

}

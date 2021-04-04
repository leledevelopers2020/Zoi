package com.zoiapp.zoi.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zoiapp.zoi.History.History;
import com.zoiapp.zoi.ModalClasses.NotificationModelClass;
import com.zoiapp.zoi.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class NotificationPage extends AppCompatActivity {
    private RecyclerView notificationList;
    private Toolbar toolbar;
    private TextView emptyNotifications;
    String userID;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<NotificationModelClass, NotificationAdapterHolder> notificationAdapter;
    Intent data;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);
        data = getIntent();

        notificationList = findViewById(R.id.notificationRecyclerView);
        emptyNotifications = findViewById(R.id.notificationEmpty);
        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseFirestore = FirebaseFirestore.getInstance();//cloud firestore
        userID = firebaseAuth.getCurrentUser().getUid();

        if(internetConnectivity()) {
            Query query = firebaseFirestore.collection("notifications").document(userID).collection("myNotification");
            FirestoreRecyclerOptions<NotificationModelClass> allnotifications = new FirestoreRecyclerOptions.Builder<NotificationModelClass>()
                    .setQuery(query, NotificationModelClass.class)
                    .build();
            notificationAdapter = new FirestoreRecyclerAdapter<NotificationModelClass, NotificationAdapterHolder>(allnotifications) {
                @NonNull
                @Override
                public NotificationAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view,parent,false);
                    return new NotificationAdapterHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull NotificationAdapterHolder notificationAdapterHolder, int i, @NonNull NotificationModelClass notificationModelClass)
                {
                    checkData();
                    final String docId = notificationAdapter.getSnapshots().getSnapshot(i).getId();
                    notificationAdapterHolder.title.setText(notificationModelClass.getNotificationTitle());
                    notificationAdapterHolder.message.setText(notificationModelClass.getNotificationMessage());
                    notificationAdapterHolder.date.setText(notificationModelClass.getNotificationDate());
                    notificationAdapterHolder.time.setText(notificationModelClass.getNotificationTime());
                    String type = notificationModelClass.getNotificationType();

                    notificationAdapterHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(NotificationPage.this, History.class));
                            deleteNotification(docId,1);
                        }
                    });
                    notificationAdapterHolder.cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteNotification(docId,1);
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    return super.getItemCount();
                }
            };
        }
        notificationList.setLayoutManager(new LinearLayoutManager(this));
        notificationList.setAdapter(notificationAdapter);
    }

    private void deleteNotification(String docId,int type) {
        if(internetConnectivity()) {
            final DocumentReference documentReference2 = firebaseFirestore.collection("Customers Info").document(userID);

            if(type == 1) {
                DocumentReference deleteReference1 = firebaseFirestore.collection("notifications")
                        .document(userID).collection("myNotification").document(docId);
                deleteReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //node deleted
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationPage.this, "Can not deleted", Toast.LENGTH_LONG).show();
                    }
                });

                documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            int count = documentSnapshot.getLong("notificationCount").intValue();
                            documentReference2.update("notificationCount", --count).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    }
                                }
                            });
                        }
                    }
                });
            }
            else if(type==0)
            {
                documentReference2.update("notificationCount", 0).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });

                FirebaseFirestore.getInstance().collection("notifications")
                        .document(userID).collection("myNotification")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshotList) {
                            batch.delete(snapshot.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                    }
                });
            }
        }
        else
        {
            Toast.makeText(NotificationPage.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

    }

    private static class NotificationAdapterHolder extends RecyclerView.ViewHolder
    {
        private TextView title, date, time, message;
        private ImageView cancle;
        View view;
        public NotificationAdapterHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            date = itemView.findViewById(R.id.notificationDate);
            time = itemView.findViewById(R.id.notificationTime);
            cancle = itemView.findViewById(R.id.notificationCancleButton);
            view = itemView;
        }
    }

    private Boolean internetConnectivity()
    {
        boolean connectivityInfo=false;
        ConnectivityManager connection=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connection.getActiveNetworkInfo();
        if(activeNetwork!=null) {
            if (activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE||activeNetwork.getType()==ConnectivityManager.TYPE_WIFI) {
                connectivityInfo = true;
            }
        }
        return connectivityInfo;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(internetConnectivity())
        {
            notificationAdapter.startListening();
        }
        else
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            notificationAdapter.stopListening();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkData() {
        if(notificationAdapter.getItemCount() ==0)
        {
            emptyNotifications.setVisibility(View.VISIBLE);
            notificationList.setVisibility(View.INVISIBLE);

        }
        else
        {
            emptyNotifications.setVisibility(View.INVISIBLE);
            notificationList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menunotification,menu);

        if(data.getIntExtra("notificationCount",0)==0)
        {
            MenuItem item = menu.findItem(R.id.clearAll);
            item.setVisible(false);
            this.invalidateOptionsMenu();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.clearAll)
        {
            deleteNotification("no id",0);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.zoiapp.zoi.History;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.MainHomePage.MainActivity;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class  History extends AppCompatActivity {

    RecyclerView historyList;
    TextView emptyHistory;
    String userID;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<UserProfile, HistoryHolder> historyAdapter;
    FirestoreRecyclerOptions<UserProfile> allhistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        emptyHistory = findViewById(R.id.historyEmpty);
        historyList = (RecyclerView) findViewById(R.id.historyView);

        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseFirestore = FirebaseFirestore.getInstance();//cloud firestore
        userID = firebaseAuth.getCurrentUser().getUid();
        if (internetConnectivity()) {
            Query query = firebaseFirestore.collection("OrdersHistory").document(userID).collection("orders")
                    .orderBy("date",Query.Direction.DESCENDING).orderBy("orderTime",Query.Direction.DESCENDING);
            allhistory = new FirestoreRecyclerOptions.Builder<UserProfile>()
                    .setQuery(query, UserProfile.class)
                    .build();


            historyAdapter = new FirestoreRecyclerAdapter<UserProfile, HistoryHolder>(allhistory) {
                @NonNull
                @Override
                public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_page, parent, false);
                    return new HistoryHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i, @NonNull UserProfile userProfile) {
                    checkData();
                    final String orderId = historyAdapter.getSnapshots().getSnapshot(i).getId();
                    final String ordertype = userProfile.getOrderState();
                    final String orderDate = userProfile.getDate();
                    final String orderTime = userProfile.getOrderTime();
                    final int orderStatus = userProfile.getStatus();
                    final String productDetails = userProfile.getProductDetails();

                    switch (orderStatus)
                    {
                        case 1 :
                        {
                            historyHolder.orderStatus.setText("Pending");
                        }
                        break;
                        case 2 :
                        {
                            historyHolder.orderStatus.setText("Delivered");
                        }
                        break;
                        case 3 :
                        {
                            historyHolder.orderStatus.setText("Returned");
                        }
                        break;
                    }

                    historyHolder.orderDate.setText(orderDate);
                    historyHolder.orderTime.setText(orderTime);
                    historyHolder.orderId.setText("Order Id: " + orderId.substring(0, 4));
                    switch (ordertype) {
                        case "Phonecall": {
                            historyHolder.typeOfImage.setImageResource(R.drawable.ic_local_phone_black_24dp);
                        }
                        break;
                        case "Gallery": {
                            historyHolder.typeOfImage.setImageResource(R.drawable.image);
                        }
                        break;
                        case "Camera": {
                            historyHolder.typeOfImage.setImageResource(R.drawable.photo_camera);
                        }
                        break;
                        case "Note": {
                            historyHolder.typeOfImage.setImageResource(R.drawable.note_icon60);
                        }
                        break;
                        case "Mart": {
                            historyHolder.typeOfImage.setImageResource(R.drawable.shopping_basket);
                        }
                        break;
                        case "Return": {
                            historyHolder.typeOfImage.setImageResource(R.drawable.returnimage);
                            historyHolder.orderStatus.setVisibility(View.INVISIBLE);
                        }
                        break;
                    }
                    historyHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(History.this, HistoryOrderDetails.class);
                            intent.putExtra("orderType",ordertype);
                            intent.putExtra("orderId", orderId.substring(0, 4));
                            intent.putExtra("orderIdFull", orderId);
                            intent.putExtra("orderDate", orderDate);
                            intent.putExtra("orderTime",orderTime);
                            switch (orderStatus)
                            {
                                case 1 :
                                {
                                    intent.putExtra("orderStatus","Pending");
                                }
                                break;
                                case 2 :
                                {
                                    intent.putExtra("orderStatus", "Delivered");
                                }
                                break;
                                case 3 :
                                {
                                    intent.putExtra("orderStatus", "Returned");
                                }
                                break;
                            }

                            intent.putExtra("productDetails", productDetails);
                            startActivity(intent);
                        }
                    });

                }

                @Override
                public int getItemCount() {
                    return super.getItemCount();
                }
            };

            historyList.setLayoutManager(new LinearLayoutManager(this));
            historyList.setAdapter(historyAdapter);
        }

        else

        {
            Toast.makeText(History.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }


    public class HistoryHolder extends RecyclerView.ViewHolder
    {

        TextView orderId,orderStatus,orderItemCount,orderDate,orderTime;
        CardView orderCard;
        ImageView typeOfImage;
        View view;
        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            typeOfImage = itemView.findViewById(R.id.typeOfDeliveryHistory);
            orderId = itemView.findViewById(R.id.orderNumHistory);
            orderStatus = itemView.findViewById(R.id.orderStatusHistroy);
            orderDate = itemView.findViewById(R.id.dateHistroy);
            orderTime = itemView.findViewById(R.id.timeHistory);
            orderCard = itemView.findViewById(R.id.historyCard);
            view = itemView;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(internetConnectivity())
            historyAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(internetConnectivity())
            historyAdapter.stopListening();
    }

    private void checkData() {
        if(historyAdapter.getItemCount() ==0)
        {
            emptyHistory.setVisibility(View.VISIBLE);
            historyList.setVisibility(View.INVISIBLE);

        }
        else
        {
            emptyHistory.setVisibility(View.INVISIBLE);
            historyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(History.this, MainActivity.class));
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
}
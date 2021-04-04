package com.zoiapp.zoi.UserRequired;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zoiapp.zoi.ModalClasses.ProductDetails;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Summary extends AppCompatActivity {
    TextView titleSerialNumber,titleItemName,titleItemQuantity,titleItemPrize;
    TextView listSerialNumber,listItemNames,listItemQuantity,listItemPrize,emptyData;
    TextView total,totalPrize;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    LinearLayout summaryPage;
    static String productSerialNumber,productNames,productQuantity,productPrice;
    static double totalPrice=0;
    String[] products;
    List<String> itemsList;
    ArrayList<ProductDetails> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Summary");
        loadViews();
        productList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (internetConnectivity()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    assert userProfile != null;
                    String data = userProfile.getProductDetails();
                    if (!data.equals("no data")) {
                        try {
                            setData(data);
                            summaryPage.setVisibility(View.VISIBLE);
                            emptyData.setVisibility(View.GONE);

                        } catch (Exception r) {
                            summaryPage.setVisibility(View.GONE);
                            emptyData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        summaryPage.setVisibility(View.GONE);
                        emptyData.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(Summary.this, "No internet Conneciton", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Summary.this,"Cannot get the data",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadViews() {
        titleSerialNumber = (TextView) findViewById(R.id.item_sno);
        titleItemName = (TextView) findViewById(R.id.item_names);
        titleItemQuantity = (TextView) findViewById(R.id.item_quantity);
        titleItemPrize = (TextView) findViewById(R.id.item_prize);
        listSerialNumber = (TextView) findViewById(R.id.items_sno_list);//1
        listItemNames = (TextView) findViewById(R.id.items_list);//2
        listItemQuantity = (TextView) findViewById(R.id.quantity_list);//3
        listItemPrize = (TextView) findViewById(R.id.prize_list);//4
        totalPrize = (TextView) findViewById(R.id.total_prize);//5
        summaryPage = (LinearLayout) findViewById(R.id.summarylayout);
        emptyData = (TextView) findViewById(R.id.summaryTextView);
    }

    private void setData(String data) {
        products = data.split("\\?\\?\\?");
        for(int i=0;i<products.length;i++)
        {
            itemsList = Arrays.asList(products[i].split("[@#!]"));
            productList.add(new ProductDetails(itemsList.get(0), itemsList.get(1), itemsList.get(2), Double.parseDouble(itemsList.get(3))));
        }
        updataSummary();
    }

    private void updataSummary() {
        productSerialNumber="";productNames="";productQuantity="";productPrice="";
        for(int i=0;i<productList.size();i++)
        {
            if(i==0)
            {
                productSerialNumber = productList.get(i).getSerialNumber();
                productNames = productList.get(i).getName();
                productQuantity = productList.get(i).getQunt();
                productPrice = ""+productList.get(i).getPrice();
                totalPrice = productList.get(i).getPrice();
            }
            else {
                productSerialNumber = productSerialNumber+"\n"+productList.get(i).getSerialNumber();
                productNames = productNames+"\n"+productList.get(i).getName();
                productQuantity = productQuantity+"\n"+productList.get(i).getQunt();
                productPrice = productPrice+"\n"+productList.get(i).getPrice();
                totalPrice = totalPrice+ productList.get(i).getPrice();
            }
        }
        listSerialNumber.setText(productSerialNumber);
        listItemNames.setText(productNames);
        listItemQuantity.setText(productQuantity);
        listItemPrize.setText(productPrice);
        totalPrize.setText(""+totalPrice);
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
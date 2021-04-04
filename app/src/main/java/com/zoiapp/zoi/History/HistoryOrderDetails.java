package com.zoiapp.zoi.History;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.ProductDetails;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryOrderDetails extends AppCompatActivity {

    private Boolean val = true;
    TextView tvSerialno,tvItems,tvQunt,tvPrice,id,date,amount,status,deliveryCharges,typeOfpayment;
    static String  orderItemSerialNumber,orderItemNames,orderItemQuantity,orderItemPrice,
            orderId,orderDate,orderStatus,orderDeliverCharges,productDetails,orderTime,orderType,orderIdFull;
    static double totalPrice = 0;
FloatingActionButton return_button;
    RelativeLayout relativeLayout,layout;
    Button more;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Summary");
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order_details);
        loadViews();
        data = getIntent();
        orderType=data.getStringExtra("orderType");
        orderId = data.getStringExtra("orderId");
        orderIdFull = data.getStringExtra("orderIdFull");
        orderDate = data.getStringExtra("orderDate");
        orderTime= data.getStringExtra("orderTime");
        orderStatus = data.getStringExtra("orderStatus");
        productDetails = data.getStringExtra("productDetails");
        id.setText(orderId);
        date.setText(orderDate);
        status.setText(orderStatus);
        return_button=(FloatingActionButton)findViewById(R.id.return_button);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile=snapshot.getValue(UserProfile.class);
                int deliverAmount=userProfile.getDeliveryCharges();
                orderDeliverCharges="₹."+deliverAmount+"";
                deliveryCharges.setText(orderDeliverCharges);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switch (orderStatus) {
            case "Delivered":
                {
                    return_button.setVisibility(View.VISIBLE);
                }
                break;
        }

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!productDetails.isEmpty()) {
                    try {
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                        Calendar calendar=Calendar.getInstance();
                        String[] dateSplit=orderDate.trim().split("/");
                        String[] timeSplit=orderTime.trim().split(":");
                        int[] dateArray = new int[dateSplit.length];
                        int[] timeArray=new int[timeSplit.length];
                        for(int i=0;i<dateSplit.length;i++)
                        {
                            dateArray[i]=Integer.parseInt(dateSplit[i]);
                        }
                        for(int i=0;i<timeSplit.length;i++)
                        {
                            timeArray[i]=Integer.parseInt(timeSplit[i]);
                        }
                        calendar.set(dateArray[2],dateArray[1]-1,dateArray[0],timeArray[0],timeArray[1],timeArray[0]);  //set order date and time in calendar instance
                        Date ordered=calendar.getTime();
                        String  todaysDate=dateTimeFormat.format(new Date());
                        String orderedDate=dateTimeFormat.format(ordered);
                        long currentDateTimeStamp=Date.parse(todaysDate);
                        long orderedDateTimeStamp=Date.parse(orderedDate);
                        long noOfSeconds = (currentDateTimeStamp-orderedDateTimeStamp)/30000;
                        long minutes = noOfSeconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;
                        if(noOfSeconds<=86400 && noOfSeconds>=0)
                            {
                                Intent i = new Intent(HistoryOrderDetails.this, ReturnOrder.class);
                                i.putExtra("item", orderItemNames);
                                i.putExtra("price", orderItemQuantity);
                                i.putExtra("orderIdFull",orderIdFull);
                                startActivity(i);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryOrderDetails.this);
                                builder.setTitle("Alert")
                                        .setMessage("You cannot return your order after 24 hours of your product delivered")
                                        .setPositiveButton("Ok", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(HistoryOrderDetails.this, "No data", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(productDetails.isEmpty() || productDetails == null)
        {
            more.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        }
        else {
            divideData();
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (val) {
                        layout.setVisibility(View.VISIBLE);
                        more.setText("LESS");
                        val = false;
                    } else {
                        layout.setVisibility(View.GONE);
                        more.setText("MORE");
                        val = true;
                    }
                }
            });
        }
    }

    private void divideData() {
        orderItemSerialNumber = "";orderItemNames = "";orderItemQuantity = "";orderItemPrice = "";
        List<String> itemsList;
        ArrayList<ProductDetails> productList = new ArrayList<>();
        String[] products = productDetails.split("\\?\\?\\?");
        for(int j=0;j<products.length;j++)
        {
            itemsList = Arrays.asList(products[j].split("[@#!]"));
            productList.add(new ProductDetails(itemsList.get(0), itemsList.get(1), itemsList.get(2), Double.parseDouble(itemsList.get(3))));
        }
        System.out.println();
        for(int j=0;j<productList.size();j++)
        {
            if(j==0)
            {
                orderItemSerialNumber = productList.get(j).getSerialNumber();
                orderItemNames = ItemModelClass.originalProductName(productList.get(j).getName());
                orderItemQuantity = productList.get(j).getQunt();
                orderItemPrice = ""+productList.get(j).getPrice();
                totalPrice = productList.get(j).getPrice();
            }
            else {
                orderItemSerialNumber = orderItemSerialNumber+"\n"+productList.get(j).getSerialNumber();
                orderItemNames = orderItemNames+"\n"+ ItemModelClass.originalProductName(productList.get(j).getName());
                orderItemQuantity = orderItemQuantity+"\n"+productList.get(j).getQunt();
                orderItemPrice = orderItemPrice+"\n"+productList.get(j).getPrice();
                totalPrice = totalPrice+ productList.get(j).getPrice();
            }
        }
        amount.setText("₹. "+String.format("%.2f",totalPrice));
        tvSerialno.setText(orderItemSerialNumber);
        tvItems.setText(orderItemNames);
        tvQunt.setText(orderItemQuantity);
        tvPrice.setText(orderItemPrice);
    }

    private void loadViews() {
        id = findViewById(R.id.orderid);
        date = findViewById(R.id.orderdate);
        status = findViewById(R.id.orderstatus);
        deliveryCharges=findViewById(R.id.orderdeliverycharges);
        amount = findViewById(R.id.ordertotalprice);
        typeOfpayment = findViewById(R.id.orderpaymentmode);
        tvSerialno = findViewById(R.id.serialnumber);
        tvItems = findViewById(R.id.txt3);
        tvQunt = findViewById(R.id.txt4);
        tvPrice = findViewById(R.id.txt5);
        more = findViewById(R.id.morebtn);
        relativeLayout = findViewById(R.id.orderInfoSummary);
        layout = findViewById(R.id.lay6);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  android.R.id.home:
            {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
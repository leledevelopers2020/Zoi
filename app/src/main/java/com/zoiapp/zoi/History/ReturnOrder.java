package com.zoiapp.zoi.History;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.UserRequired.UserAddress;
import java.util.ArrayList;

public class ReturnOrder extends AppCompatActivity {
    ArrayList<UserProfile> dataList;
    RecyclerView returnRecyclerView;
    ReturnAdapter returnAdapter;
    Button submit;
    CheckBox selectAll;
    Intent data;
    private String items,quantity,price,orderIdFull;
    private String[] str1,str2,str3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order);
        selectAll = (CheckBox) findViewById(R.id.checkboxAll);
        submit = (Button) findViewById(R.id.return_submitbtn);
        data = getIntent();
        buildArray();
        dataList = loadDatatoList(false);
        bulidRecyclerView();
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectAll.isChecked())
                {
                    dataList = loadDatatoList(true);
                    bulidRecyclerView();
                }
                else
                {
                    dataList = loadDatatoList(false);
                    bulidRecyclerView();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity()) {
                    String productNames = "";
                    String qunatity = "";
                    String price = "";
                    for (int i = 0; i < ReturnAdapter.mList.size(); i++) {
                        if (ReturnAdapter.mList.get(i).isSelected()){
                            productNames = productNames + ReturnAdapter.mList.get(i).getItemNames()+" \n ";
                            qunatity = qunatity + ReturnAdapter.mList.get(i).getItemQuantity()+" \n ";
                            price = price + ReturnAdapter.mList.get(i).getItemPrice()+" \n ";
                        }
                    }
                    Intent intent = new Intent(ReturnOrder.this, UserAddress.class);
                    intent.putExtra("activity","Return");
                    intent.putExtra("orderName",productNames);
                    intent.putExtra("orderQuantity",qunatity);
                    intent.putExtra("orderPrice",price);
                    intent.putExtra("orderIdFull",orderIdFull);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ReturnOrder.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void buildArray() {

        items = data.getStringExtra("item");
        quantity = data.getStringExtra("quantity");
        price = data.getStringExtra("price");
        orderIdFull=data.getStringExtra("orderIdFull");
        str1 = items.split("\n");
        str2 = quantity.split("\n");
        str3 = price.split("\n");

    }
    private ArrayList<UserProfile> loadDatatoList(boolean isSelected) {
        ArrayList<UserProfile> list = new ArrayList<>();

        for(int i=0;i<str1.length;i++) {
            UserProfile userProfile = new UserProfile();
            userProfile.setSelected(isSelected);
            userProfile.setItemNames(str1[i]);
            userProfile.setItemQuantity(str2[i]);
            userProfile.setItemPrice(str3[i]);
            list.add(userProfile);
        }
        return list;
    }
    public void bulidRecyclerView()
    {
        returnRecyclerView = findViewById(R.id.return_recyclerview);
        returnRecyclerView.setHasFixedSize(true);
        returnRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        returnAdapter = new ReturnAdapter(dataList);
        returnRecyclerView.setAdapter(returnAdapter);
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

//https://android-pratap.blogspot.com/2015/01/recyclerview-with-checkbox-example.html
//below was main link
//https://demonuts.com/recyclerview-checkbox/
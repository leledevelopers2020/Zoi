package com.zoiapp.zoi.Cam_Gal_Phn;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.R;
import com.zoiapp.zoi.UserRequired.UserAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class PhoneCalls extends AppCompatActivity {
    TextView ShowName,ShowAddress,ShowPhoneNumber;
    EditText getName,getAddress,getPhn;
    ImageButton nameEdit,addressEdit,phoneEdit,saveName,saveAddress,savePhn;
    Button submit;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    String userID,name,address,areaname,phnNum,sName,sAddress,sPhnNum;
    private static Boolean state=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_calls);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity()) {
                    Intent intent = new Intent(PhoneCalls.this, UserAddress.class);
                    intent.putExtra("activity", "Phonecall");
                    startActivity(intent);
                }
                else {
                    Toast.makeText(PhoneCalls.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });


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
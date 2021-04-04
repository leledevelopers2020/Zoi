package com.zoiapp.zoi.UserRequired;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.UserGuide.Guide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDetails extends AppCompatActivity {
    EditText userName, userEmail, userAge, userstreet, userstate;
    Spinner usertown;
    Button saveButton;
    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    String userID,address ;
    static String name,email,age,street,town,state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userAge = findViewById(R.id.userAge);
        userstreet = findViewById(R.id.userstreet);
        usertown=findViewById(R.id.usertown);
        userstate=findViewById(R.id.userstate);
        saveButton = findViewById(R.id.saveButton);

        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseDatabase = FirebaseDatabase.getInstance();//realtime database
        firebaseFirestore = FirebaseFirestore.getInstance();//cloud firestore
        userID = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference1 = firebaseFirestore.collection("users").document(userID);
        final DocumentReference documentReference2 = firebaseFirestore.collection("Customers Info").document(userID);
        getMartList();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = userName.getText().toString();
                // validateUserName(name);
                email = userEmail.getText().toString();
                //validateMail(email);
                age = userAge.getText().toString();
                //validateAge(age);
                street=userstreet.getText().toString();

                town=usertown.getSelectedItem().toString();

                state=userstate.getText().toString();

                //validateAddress(address);
                if (validateUserName(name) && validateMail(email) && validateAge(age) && validateStreet(street)&&validateTown(town)&&validateState(state)) {

                    Map<String, Object> user1 = new HashMap<>();
                    Map<String, Object> user2 = new HashMap<>();
                    user1.put("UserName", name);
                    user1.put("Email", email);
                    user1.put("UserAge", age);
                    user1.put("UserStreet", street);
                    user1.put("UserTown", town);
                    user1.put("UserState", state);
                    user1.put("UserAddress", address);
                    user2.put("CustomerName", name);
                    user2.put("CustomerStreet", street);
                    user2.put("CustomerTown", town);
                    user2.put("CustomerState", state);
                    user2.put("CustomerPhoneNumber", firebaseAuth.getCurrentUser().getPhoneNumber());
                    user2.put("notificationCount",0);
                    if(internetConnectivity())
                    {
                    documentReference1.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddDetails.this, "Data is inserted", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddDetails.this, "Data is not inserted", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    documentReference2.set(user2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AddDetails.this, Guide.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(AddDetails.this, "Data is not inserted", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    DatabaseReference myRef2 = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Position");
                    UserProfile stage = new UserProfile(1);
                    myRef2.setValue(stage);
                    DatabaseReference databaseReference1 = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Location");
                    UserProfile userProfile = new UserProfile();
                    userProfile.setMartLocation(town);
                    userProfile.setStage(1);
                    databaseReference1.setValue(userProfile);
                    DatabaseReference databaseReference2 = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Summary");
                    UserProfile summaryData = new UserProfile();
                    summaryData.setProductDetails("no data");
                    summaryData.setDeliveryCharges(0);
                    databaseReference2.setValue(summaryData);

                    DatabaseReference databaseReference3 = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Cart");
                    ItemModelClass itemModelClass = new ItemModelClass();
                    itemModelClass.setProductCode("");
                    itemModelClass.setProductQuantity("");
                    //itemModelClass.setProductCount("");
                    databaseReference3.setValue(itemModelClass);
                }
                else
                    {
                        Toast.makeText(AddDetails.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddDetails.this, "All fields are required", Toast.LENGTH_LONG).show();
                    return;
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


    private void getMartList() {
        DatabaseReference loc = firebaseDatabase.getReference("Products").child("UserLocationList");
        loc.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                String locationList = snapshot.child("LocationList").getValue().toString();//itemModelClass.get();
                String[] MartLocation = locationList.split(" \n ");
                setCompleteText(MartLocation);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCompleteText(String[] martLocation) {
        usertown.setVisibility(View.VISIBLE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddDetails.this,android.R.layout.simple_spinner_item,martLocation);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usertown.setAdapter(arrayAdapter);
    }

    private boolean validateUserName(String data) {
        Boolean value1=true;
        if(data.isEmpty() && data.length() <30)
        {
            userName.setError("Enter proper Name");
            userName.requestFocus();
            value1 = false;
        }
        return value1;
    }

    private boolean validateMail(String data)
    {
        Boolean value2 = true;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(data);
        if(matcher.matches()==false || data.isEmpty())
        {
            userEmail.setError("Enter valid mail");
            userEmail.requestFocus();
            value2 = false;
        }
        return value2;
    }
    private boolean validateAge(String data) {
        Boolean value3 = true;
        if(data.isEmpty())
        {
            userAge.setError("Age Should not be empty");
            userAge.requestFocus();
            value3 = false;
        }
        else if(data.length() >2)
        {
            userAge.setError("Age length should not exceed 3");
            userAge.requestFocus();
            value3 = false;
        }
        return value3;
    }

    private boolean validateTown(String data) {
        Boolean value4 = true;
        if(data.isEmpty())
        {
            usertown.requestFocus();
            value4 = false;
        }
        else if(data.length() >100)
        {
            usertown.requestFocus();
            value4 = false;
        }
        return value4;
    }
    private boolean validateState(String data) {
        Boolean value4 = true;
        if(data.isEmpty())
        {
            userstate.setError("Address Should not be empty");
            userstate.requestFocus();
            value4 = false;
        }
        else if(data.length() >100)
        {
            userstate.setError("Address length should be not exceed 50");
            userstate.requestFocus();
            value4 = false;
        }
        return value4;
    }

    private boolean validateStreet(String data) {
        Boolean value4 = true;
        if(data.isEmpty())
        {
            userstreet.setError("Address Should not be empty");
            userstreet.requestFocus();
            value4 = false;
        }
        else if(data.length() >100)
        {
            userstreet.setError("Address length should be not exceed 50");
            userstreet.requestFocus();
            value4 = false;
        }
        return value4;
    }
}
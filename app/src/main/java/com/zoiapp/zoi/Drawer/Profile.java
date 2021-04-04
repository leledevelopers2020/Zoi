package com.zoiapp.zoi.Drawer;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.Mart.ShopHomePage;
import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference locationList,loc;
    TextView pName,pEmail,pStreet,pTown,pState,pPhone;
    Toolbar toolbar;
    ImageView editNameIcon,editEmailIcon,editStreetIcon,editTowmIcon,editStateIcon;
    EditText editFullName,editEmailID,editStreet,editState;
    Spinner editTown;
    Button save;
    static  String data,field,name,email,street,town,state;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadViews();
        save.setVisibility(View.GONE);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        loc = firebaseDatabase.getReference(fAuth.getCurrentUser().getUid()).child("Location");
        locationList = firebaseDatabase.getReference("Products").child("UserLocationList");
        final DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        showData();
        editNameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmailIcon.setVisibility(View.GONE);
                editStreetIcon.setVisibility(View.GONE);
                editTowmIcon.setVisibility(View.GONE);
                editStateIcon.setVisibility(View.GONE);
                pName.setVisibility(View.GONE);
                editFullName.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                editFullName.requestFocus();
               editFocus(editFullName);
                editData(1);

            }

        });
        editEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameIcon.setVisibility(View.GONE);
                editStreetIcon.setVisibility(View.GONE);
                editTowmIcon.setVisibility(View.GONE);
                editStateIcon.setVisibility(View.GONE);
                pEmail.setVisibility(View.GONE);
                editEmailID.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                editEmailID.requestFocus();
                editFocus(editEmailID);
                editData(2);
            }
        });

        editStreetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameIcon.setVisibility(View.GONE);
                editEmailIcon.setVisibility(View.GONE);
                editTowmIcon.setVisibility(View.GONE);
                editStateIcon.setVisibility(View.GONE);
                pStreet.setVisibility(View.GONE);
                editStreet.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                editStreet.requestFocus();
                editFocus(editStreet);
                editData(3);
            }
        });
        editTowmIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder locChangeBuilder=new AlertDialog.Builder(Profile.this);
                locChangeBuilder.setMessage("Location change will effect your items price added in the cart (or) you may loose your items")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                locationList.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                                        String locationList = snapshot.child("LocationList").getValue().toString();
                                        String[] martLocation = locationList.split(" \n ");

                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Profile.this,android.R.layout.simple_spinner_item,martLocation);
                                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        editTown.setAdapter(arrayAdapter);
                                        editData(4);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = locChangeBuilder.create();
                dialog.show();





                editNameIcon.setVisibility(View.GONE);
                editEmailIcon.setVisibility(View.GONE);
                editStreetIcon.setVisibility(View.GONE);
                editStateIcon.setVisibility(View.GONE);
                pTown.setVisibility(View.GONE);
                editTown.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                editTown.requestFocus();

                //editData(4);
            }
        });
        editStateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameIcon.setVisibility(View.GONE);
                editEmailIcon.setVisibility(View.GONE);
                editStreetIcon.setVisibility(View.GONE);
                editTowmIcon.setVisibility(View.GONE);
                pState.setVisibility(View.GONE);
                editState.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                editState.requestFocus();
                editFocus(editState);
                editData(5);
            }
        });
    }

    private void editFocus(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }


    private void loadViews() {
        //TEXTVIEWS
        pName = (TextView) findViewById(R.id.profileFullName);
        pEmail =(TextView)  findViewById(R.id.profileEmail);
        pStreet =(TextView)  findViewById(R.id.profileStreet);
        pTown =(TextView)  findViewById(R.id.profileTown);
        pState =(TextView)  findViewById(R.id.profileState);
        pPhone = (TextView) findViewById(R.id.profilePhone);

        //EDIT TEXT
        editFullName = (EditText) findViewById(R.id.editFullName);
        editEmailID = (EditText) findViewById(R.id.editEmail);
        editStreet = (EditText) findViewById(R.id.editStreet);
        editTown = (Spinner) findViewById(R.id.editTown);
        editState = (EditText) findViewById(R.id.editState);

        editNameIcon =(ImageView) findViewById(R.id.imageView7);
        editEmailIcon =(ImageView) findViewById(R.id.imageView9);
        editStreetIcon = (ImageView) findViewById(R.id.imageView12);
        editTowmIcon = (ImageView) findViewById(R.id.imageView13);
        editStateIcon = (ImageView) findViewById(R.id.state_imageview_edit);
        save = findViewById(R.id.saveButton);
    }

    private void showData() {
        if (internetConnectivity()) {
            final DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        pName.setText(documentSnapshot.getString("UserName"));
                        pEmail.setText(documentSnapshot.getString("Email"));
                        pPhone.setText(fAuth.getCurrentUser().getPhoneNumber());
                        pStreet.setText(documentSnapshot.getString("UserStreet"));
                        pState.setText(documentSnapshot.getString("UserState"));
                    }
                }
            });
            DatabaseReference loc = firebaseDatabase.getReference(fAuth.getCurrentUser().getUid()).child("Location");
            loc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserProfile location = snapshot.getValue(UserProfile.class);
                    pTown.setText(location.getMartLocation());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    pTown.setText("NULL");
                }
            });
        }
        else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void editData(final int type)
    {
        save.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (internetConnectivity()) {

                    if (type == 1) {
                        name = editFullName.getText().toString().trim();
                        if (validate(1)) {
                            final DocumentReference documentReference1 = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            final DocumentReference documentReference2 = fStore.collection("Customers Info").document(fAuth.getCurrentUser().getUid());
                            documentReference1.update("UserName", editFullName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "UserName Updated", Toast.LENGTH_SHORT).show();
                                        save.setVisibility(View.GONE);
                                        editFullName.setVisibility(View.GONE);
                                        pName.setVisibility(View.VISIBLE);
                                        showData();
                                        editEmailIcon.setVisibility(View.VISIBLE);
                                        editStreetIcon.setVisibility(View.VISIBLE);
                                        editTowmIcon.setVisibility(View.VISIBLE);
                                        editStateIcon.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(Profile.this, "saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            documentReference2.update("CustomerName", editFullName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        }
                    } else if (type == 2) {
                        email = editEmailID.getText().toString().trim();
                        if (validate(2)) {
                            final DocumentReference documentReference1 = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            documentReference1.update("Email", editEmailID.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Email Updated", Toast.LENGTH_SHORT).show();
                                        save.setVisibility(View.GONE);
                                        editEmailID.setVisibility(View.GONE);
                                        pEmail.setVisibility(View.VISIBLE);
                                        showData();
                                        editNameIcon.setVisibility(View.VISIBLE);
                                        editStreetIcon.setVisibility(View.VISIBLE);
                                        editTowmIcon.setVisibility(View.VISIBLE);
                                        editStateIcon.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(Profile.this, "saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else if (type == 3) {
                        street = editStreet.getText().toString().trim();
                        if (validate(3)) {
                            final DocumentReference documentReference1 = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            final DocumentReference documentReference2 = fStore.collection("Customers Info").document(fAuth.getCurrentUser().getUid());
                            documentReference1.update("UserStreet", editStreet.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Street Updated", Toast.LENGTH_SHORT).show();
                                        save.setVisibility(View.GONE);
                                        editStreet.setVisibility(View.GONE);
                                        pStreet.setVisibility(View.VISIBLE);
                                        showData();
                                        editNameIcon.setVisibility(View.VISIBLE);
                                        editEmailIcon.setVisibility(View.VISIBLE);
                                        editTowmIcon.setVisibility(View.VISIBLE);
                                        editStateIcon.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(Profile.this, "saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            documentReference2.update("CustomerStreet", editStreet.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        } else {

                        }
                    } else if (type == 4) {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setMartLocation(editTown.getSelectedItem().toString());
                        loc.setValue(userProfile);

                        Toast.makeText(Profile.this, "Town Updated", Toast.LENGTH_SHORT).show();
                        save.setVisibility(View.GONE);
                        editTown.setVisibility(View.GONE);
                        pTown.setVisibility(View.VISIBLE);
                        showData();
                        editNameIcon.setVisibility(View.VISIBLE);
                        editEmailIcon.setVisibility(View.VISIBLE);
                        editStreetIcon.setVisibility(View.VISIBLE);
                        editStateIcon.setVisibility(View.VISIBLE);
                    } else if (type == 5) {
                        state = editState.getText().toString().trim();
                        if (validate(5)) {
                            final DocumentReference documentReference1 = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                            final DocumentReference documentReference2 = fStore.collection("Customers Info").document(fAuth.getCurrentUser().getUid());
                            documentReference1.update("UserState", editState.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "State Updated", Toast.LENGTH_SHORT).show();
                                        save.setVisibility(View.GONE);
                                        editState.setVisibility(View.GONE);
                                        pState.setVisibility(View.VISIBLE);
                                        showData();
                                        editNameIcon.setVisibility(View.VISIBLE);
                                        editEmailIcon.setVisibility(View.VISIBLE);
                                        editStreetIcon.setVisibility(View.VISIBLE);
                                        editTowmIcon.setVisibility(View.VISIBLE);

                                    } else {
                                        Toast.makeText(Profile.this, "saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            documentReference2.update("CustomerState", editState.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        }
                    }
                }
                else {
                    Toast.makeText(Profile.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }


            }




        });
    }

    private Boolean validate(int type)
    {
        Boolean returnValue = true;
        if(type == 1)
        {
            if(name.isEmpty() ) {
                editFullName.setError("Name can not be empty");
                editFullName.requestFocus();
                returnValue = false;
            }
            else if(name.length()>20)
            {
                editFullName.setError("Name length should be less than 20 characters");
                editFullName.requestFocus();
                returnValue = false;
            }
        }
        else if(type == 2)
        {
            final String EMAIL_PATTERN =
                    "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)(\\.[A-Za-z]{2,})$";
            final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            final Matcher matcher = pattern.matcher(email);
            if(matcher.matches()==false || email.isEmpty())
            {
                editEmailID.setError("Enter valid mail");
                editEmailID.requestFocus();
                returnValue = false;
            }
            return returnValue;

        }
        else if(type == 3) {
            if (street.isEmpty()) {
                editStreet.setError("H/no:,Street can not be Empty");
                editStreet.requestFocus();
                returnValue = false;
            } else if (street.length() > 100) {
                editStreet.setError("H.no,street length should be less than 50 characters");
                editStreet.requestFocus();
                returnValue = false;
            }

            return returnValue;
        }
        else if(type == 5) {
            if (state.isEmpty()) {
                editState.setError("State can not be Empty");
                editState.requestFocus();
                returnValue = false;
            } else if (state.length() > 50) {
                editState.setError("State length should be less than 50 characters");
                editState.requestFocus();
                returnValue = false;
            }
        }
        return returnValue;

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
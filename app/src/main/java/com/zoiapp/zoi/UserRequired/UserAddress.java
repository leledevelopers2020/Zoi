package com.zoiapp.zoi.UserRequired;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.Notifications.FireMsgService;
import com.zoiapp.zoi.History.History;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class UserAddress extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private DocumentReference documentReference;

    Button submit;
    LinearLayout linearlayoutimage;
    LayoutInflater inflater;
    TextView ShowName,ShowStreet,ShowTown,ShowState,ShowPhoneNumber;
    EditText getName,getStreet,getTown,getState,getPhn;
    ImageButton nameEdit,streetEdit,townEdit,stateEdit,phoneEdit,saveName,saveStreet,saveTown,saveState,savePhn;
    static String userID,name,phnNum,sName,sStreet,sTown,sState,sPhnNum,sMailId,martCredentials;
    private int notificationCount = 0;
    private static Boolean state=true;
    String streetValue,townValue,stateValue;
    static String cartOrderNames,cartOrderQuantity,cartOrderPrice;
    static String noteOrderNames,noteOrderQuantity;
    static String productDetails = "";
    static String Location;
    private static final int GALLERY_PERM_CODE = 106;
    public static final int GALLERY_REQUEST_CODE = 105;
    String Email ="zoimainorders@gmail.com";
    String password ="[#ioz#]-!0";
    String recevierEmail=" ";
    private ArrayList<SendMail> mailDetails;
    private List<String> itemslist;


    ////variables for progress bar
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private  long fileSize = 0;
    ProgressDialog progressDialog;
    Thread t1;

    //get extra data
    String activity;
    ArrayList<Uri> images;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        inflater=LayoutInflater.from(this);
        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Position");
        documentReference = firebaseFirestore.collection("Customers Info").document(userID);
        linearlayoutimage=(LinearLayout)findViewById(R.id.llimg);
        //EditText
        getName = (EditText) findViewById(R.id.name);
        getStreet = (EditText) findViewById(R.id.userstreet);
        getState = (EditText) findViewById(R.id.userstate);
        getTown = (EditText) findViewById(R.id.usertown);
        getPhn = (EditText) findViewById(R.id.phnNum);
        //TextView
        ShowName = (TextView) findViewById(R.id.permanentname);
        ShowStreet = (TextView) findViewById(R.id.permanentuserstreet);
        ShowTown = (TextView) findViewById(R.id.permanentusertown);
        ShowState = (TextView) findViewById(R.id.permanentuserstate);
        ShowPhoneNumber = (TextView) findViewById(R.id.permanentphnNum);
        //ImageButton for editing data
        nameEdit = (ImageButton) findViewById(R.id.editname);
        streetEdit = (ImageButton) findViewById(R.id.edituserstreet);
        townEdit = (ImageButton) findViewById(R.id.editusertown);
        stateEdit = (ImageButton) findViewById(R.id.edituserstate);
        phoneEdit = (ImageButton) findViewById(R.id.editphoneno);
        //ImageButton for saving data
        saveName = (ImageButton) findViewById(R.id.save4);
        saveStreet = (ImageButton) findViewById(R.id.save5);
        saveTown = (ImageButton) findViewById(R.id.save6);
        saveState = (ImageButton) findViewById(R.id.save7);
        savePhn = (ImageButton) findViewById(R.id.save8);
        submit = (Button) findViewById(R.id.submit);
        showData();
        data = getIntent();
        mailDetails = new ArrayList<SendMail>();
        activity=data.getExtras().getString("activity");
        switch(activity)
        {
            case "Phonecall" :
            {

            }
            break;
            case "Camera" :
            {
                images = data.getExtras().getParcelableArrayList("images");
            }
            break;
            case "Gallery" :
            {
                images = data.getExtras().getParcelableArrayList("images");
            }
            break;
            case "Note" :
            {
                noteOrderNames = data.getStringExtra("orderName");
                noteOrderQuantity = data.getStringExtra("orderQuantity");
                convertDataIntoString(noteOrderNames,noteOrderQuantity);
            }
            break;
            case "Mart" :
            {
                cartOrderNames = data.getStringExtra("orderName");
                cartOrderQuantity = data.getStringExtra("orderQuantity");
                cartOrderPrice = data.getStringExtra("orderPrice");
                convertDataIntoString(cartOrderNames,cartOrderQuantity,cartOrderPrice);
                townEdit.setVisibility(View.INVISIBLE);
            }
            break;
            case "Return" :
            {
                cartOrderNames = data.getStringExtra("orderName");
                cartOrderQuantity = data.getStringExtra("orderQuantity");
                cartOrderPrice = data.getStringExtra("orderPrice");
                convertDataIntoString(cartOrderNames,cartOrderQuantity,cartOrderPrice);
            }
            break;
        }

        nameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowName.setVisibility(View.INVISIBLE);//textview
                nameEdit.setVisibility(View.INVISIBLE);//editicon
                getName.setVisibility(View.VISIBLE);//editview
                saveName.setVisibility(View.VISIBLE);//saveicon
                nameEdit.requestFocus();
                editFocus(nameEdit);

            }
        });

        streetEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShowStreet.setVisibility(View.INVISIBLE);//textview
                streetEdit.setVisibility(View.INVISIBLE);//editicon
                getStreet.setVisibility(View.VISIBLE);//editview
                saveStreet.setVisibility(View.VISIBLE);//saveicon
                streetEdit.requestFocus();
                editFocus(streetEdit);
            }
        });
        townEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShowTown.setVisibility(View.INVISIBLE);//textview
                townEdit.setVisibility(View.INVISIBLE);//editicon
                getTown.setVisibility(View.VISIBLE);//editview
                saveTown.setVisibility(View.VISIBLE);//saveicon
                townEdit.requestFocus();
                editFocus(townEdit);
            }
        });
        stateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShowState.setVisibility(View.INVISIBLE);//textview
                stateEdit.setVisibility(View.INVISIBLE);//editicon
                getState.setVisibility(View.VISIBLE);//editview
                saveState.setVisibility(View.VISIBLE);//saveicon
                stateEdit.requestFocus();
                editFocus(stateEdit);
            }
        });

        phoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ShowPhoneNumber.setVisibility(View.INVISIBLE);//textview
                phoneEdit.setVisibility(View.INVISIBLE);//editicon
                getPhn.setVisibility(View.VISIBLE);//editview
                savePhn.setVisibility(View.VISIBLE);//saveicon
                phoneEdit.requestFocus();
                editFocus(phoneEdit);
            }
        });
        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity())
                {
                    name = getName.getText().toString().trim();
                    if (validate(1)) {
                        documentReference.update("CustomerName", name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getName.setVisibility(View.GONE);//edittext
                                    saveName.setVisibility(View.INVISIBLE);//saveicon
                                    ShowName.setVisibility(View.VISIBLE);//textview
                                    nameEdit.setVisibility(View.VISIBLE);//editicon
                                    showData();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(UserAddress.this, "Enter the Data", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(UserAddress.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }

        });

        saveStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity()) {

                    streetValue = getStreet.getText().toString().trim();
                    if (validate(2)) {
                        documentReference.update("CustomerStreet", streetValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getStreet.setVisibility(View.GONE);//edittext
                                    saveStreet.setVisibility(View.INVISIBLE);//saveicon
                                    ShowStreet.setVisibility(View.VISIBLE);//textview
                                    streetEdit.setVisibility(View.VISIBLE);//editicon
                                    showData();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserAddress.this, "Enter the Data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserAddress.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity())
                {
                    townValue = getTown.getText().toString().trim();
                    if (validate(3)) {
                        documentReference.update("CustomerTown", townValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getTown.setVisibility(View.GONE);//edittext
                                    saveTown.setVisibility(View.INVISIBLE);//saveicon
                                    ShowTown.setVisibility(View.VISIBLE);//textview
                                    stateEdit.setVisibility(View.VISIBLE);//editicon
                                    showData();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserAddress.this, "Enter the Data", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(UserAddress.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity())
                {
                    stateValue = getState.getText().toString().trim();
                    if (validate(4)) {
                        documentReference.update("CustomerState", stateValue).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getState.setVisibility(View.GONE);//edittext
                                    saveState.setVisibility(View.INVISIBLE);//saveicon
                                    ShowState.setVisibility(View.VISIBLE);//textview
                                    stateEdit.setVisibility(View.VISIBLE);//editicon
                                    showData();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserAddress.this, "Enter the Data", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(UserAddress.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        savePhn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity())
                {
                    phnNum = getPhn.getText().toString().trim();
                    if (validate(5)) {
                        documentReference.update("CustomerPhoneNumber", phnNum).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getPhn.setVisibility(View.GONE);//edittext
                                    savePhn.setVisibility(View.INVISIBLE);//saveicon
                                    ShowPhoneNumber.setVisibility(View.VISIBLE);//textview
                                    phoneEdit.setVisibility(View.VISIBLE);//editicon
                                    showData();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UserAddress.this, "Enter the Data", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(UserAddress.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(internetConnectivity())
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(UserAddress.this);
                    builder.setMessage("Are you sure to place your order")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    upload(activity);
                                    dialog.dismiss();
                                    progressbar();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    Toast.makeText(UserAddress.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void showData() {
        if(internetConnectivity()) {
            final DocumentReference documentReference = firebaseFirestore.collection("Customers Info").document(userID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        sName = documentSnapshot.getString("CustomerName");
                        ShowName.setText(sName);
                        sStreet = documentSnapshot.getString("CustomerStreet");
                        ShowStreet.setText(sStreet);
                        sState = documentSnapshot.getString("CustomerState");
                        ShowState.setText(sState);
                        sPhnNum = documentSnapshot.getString("CustomerPhoneNumber");
                        ShowPhoneNumber.setText(sPhnNum);
                        int i= documentSnapshot.getLong("notificationCount").intValue();
                        notificationCount = i;
                    }
                }
            });

            final DocumentReference documentReference1 = firebaseFirestore.collection("users").document(userID);
            documentReference1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        sMailId = documentSnapshot.getString("Email");
                    }
                }
            });

            DatabaseReference loc = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid()).child("Location");
            loc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserProfile location = snapshot.getValue(UserProfile.class);
                    sTown = location.getMartLocation();
                    ShowTown.setText(sTown);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ShowTown.setText("NULL");
                }
            });

            DatabaseReference mailList = firebaseDatabase.getReference("Products").child("UserLocationList");
            mailList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    martCredentials = snapshot.child("martCredentials").getValue().toString();
                    loadMailCredentials();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ShowTown.setText("NULL");
                }
            });
        }
        else
        {
            Toast.makeText(UserAddress.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void  upload(String activity) {
        final DatabaseReference databaseReference1 = firebaseDatabase.getReference("%Orders").child(sTown);
        final DatabaseReference databaseReference3 = firebaseDatabase.getReference(firebaseAuth.getUid()).child("OrderInfo").child(activity);
        DatabaseReference position = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Position");
        DocumentReference ordersHistory = firebaseFirestore.collection("OrdersHistory").document(userID).collection("orders").document() ;
        Map<String,Object> details = new HashMap<>();

        final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference(firebaseAuth.getUid()).child("OrderImages");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
        String currentDateTimeString = simpleDateFormat.format(new Date());
        UserProfile userProfile1 = new UserProfile();
        UserProfile userProfile2 = new UserProfile();

        userProfile1.setId(userID);
        userProfile1.setApplicantName(sName);
        userProfile1.setDeliveryAddress(sStreet+" "+sTown+" "+sState);
        userProfile1.setPhn(sPhnNum);
        userProfile1.setTime(currentDateTimeString);
        //update thid order in your personal folder
        userProfile2.setApplicantName(sName);
        userProfile2.setDeliveryAddress(sStreet+" "+sTown+" "+sState);
        userProfile2.setPhn(sPhnNum);
        userProfile2.setTime(currentDateTimeString);
        //update data into cloud firestore
        details.put("date", dateFormat.format(new Date()));
        details.put("itemCount",2);
        details.put("orderId","Order#1");
        details.put("orderTime", timeFormat.format(new Date()));
        details.put("status",1);

        switch (activity)
        {
            case "Camera" :
            {
                userProfile1.setOrderState(activity);
                userProfile1.setOrderDetails("");
                userProfile2.setOrderDetails("");
                imageUpload(ImageFolder,databaseReference1);
                details.put("orderState",activity);
                details.put("productDetails","");
                details.put("status",1);
            }
            break;
            case "Gallery" :
            {
                userProfile1.setOrderState(activity);
                userProfile1.setOrderDetails("");
                userProfile2.setOrderDetails("");
                imageUpload(ImageFolder,databaseReference1);
                details.put("orderState",activity);
                details.put("productDetails","");
                details.put("status",1);
            }
            break;
            case "Phonecall" :
            {
                userProfile1.setOrderState(activity);
                userProfile1.setOrderDetails("");
                userProfile2.setOrderDetails("");
                details.put("orderState",activity);
                details.put("productDetails","");
                details.put("status",1);
            }
            break;


            case "Note" :
            {
                userProfile1.setOrderState(activity);
                userProfile1.setOrderDetails(productDetails);
                userProfile2.setOrderDetails(productDetails);
                details.put("orderState",activity);
                details.put("productDetails",productDetails);
                details.put("status",1);
            }
            break;
            case "Mart" :
            {
                userProfile1.setOrderState(activity);
                userProfile1.setOrderDetails(productDetails);
                userProfile2.setOrderDetails(productDetails);
                details.put("orderState",activity);
                details.put("productDetails",productDetails);
                details.put("status",1);
                DatabaseReference clearCart = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Cart");
                ItemModelClass itemModelClass = new ItemModelClass();
                itemModelClass.setProductCode("");
                itemModelClass.setProductQuantity("");
                clearCart.setValue(itemModelClass);
            }
            break;
            case "Return" :
            {
                String orderIdFull=data.getStringExtra("orderIdFull");
                DocumentReference ordersHistorReturn = firebaseFirestore.collection("OrdersHistory").document(userID).collection("orders").document(orderIdFull) ;
                userProfile1.setOrderState(activity);
                userProfile1.setOrderDetails(productDetails);
                userProfile2.setOrderDetails(productDetails);
                details.put("orderState",activity);
                details.put("productDetails",productDetails);
                details.put("status",3);
                ordersHistorReturn.update("status",3);
            }
        }
        databaseReference1.push().setValue(userProfile1);
        databaseReference3.push().setValue(userProfile2);
        ordersHistory.set(details);
    }

    private void editFocus(ImageView editIcon) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editIcon, InputMethodManager.SHOW_IMPLICIT);
    }

    private void convertDataIntoString(String names, String quantity) {
        String[] productNames = names.split("\n");
        String[] productQuantity = quantity.split("\n");
        //String[] productPrice = price.split("\n");
        int serialnumber=0;
        for(int i=0;i<productNames.length;i++)
        {
            serialnumber++;
            if(i==0)
                productDetails = serialnumber+"@"+ItemModelClass.rawProductName(productNames[i])+"#"+productQuantity[i]+"!"+"0";
            else
                productDetails = productDetails+"???"+serialnumber+"@"+ItemModelClass.rawProductName(productNames[i])+"#"+productQuantity[i]+"!"+"0";
        }
    }

    private void convertDataIntoString(String names, String quantity, String price) {
        String[] productNames = names.split(" \n ");
        String[] productQuantity = quantity.split(" \n ");
        String[] productPrice = price.split(" \n ");
        int serialnumber=0;
        for(int i=0;i<productNames.length;i++)
        {
            serialnumber++;
            if(i==0)
                productDetails = serialnumber+"@"+ItemModelClass.rawProductName(productNames[i])+"#"+productQuantity[i]+"!"+productPrice[i];
            else
                productDetails = productDetails+"???"+serialnumber+"@"+ItemModelClass.rawProductName(productNames[i])+"#"+productQuantity[i]+"!"+productPrice[i];
        }

    }

    private Boolean validate(int type)
    {
        Boolean returnValue = true;
        if(type == 1)
        {
            if(name.isEmpty() || name.length()>=20) {
                getName.setError("Enter the Proper Name");
                getName.requestFocus();
                returnValue = false;
            }
        }
        else if(type == 2)
        {
            if(streetValue.isEmpty()  ||  streetValue.length() >100) {
                getStreet.setError("Enter Correct Street");
                getStreet.requestFocus();
                returnValue = false;
            }
        }
        else if(type == 3)
        {
            if(townValue.isEmpty()  ||  townValue.length() >100) {
                getTown.setError("Enter Correct Town");
                getTown.requestFocus();
                returnValue = false;
            }
        }
        else if(type == 4)
        {
            if(stateValue.isEmpty()  ||  stateValue.length() >100) {
                getState.setError("Enter Correct State");
                getState.requestFocus();
                returnValue = false;
            }
        }
        else  if(type == 5)
        {
            if(phnNum.isEmpty()  ||  phnNum.length()!=10) {
                getPhn.setError("Phone number should 10 digits");
                getPhn.requestFocus();
                returnValue = false;
            }
        }
        return  returnValue;
    }


    private void progressbar()
    {

        progressDialog=new ProgressDialog(UserAddress.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setTitle("Do not exit the page");
        progressDialog.show();
        progressBarStatus = 0;
        fileSize = 0;


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBarStatus < 100) {
                    // performing operation
                    progressBarStatus = doOperation();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Updating the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressDialog.setProgress(progressBarStatus);
                        }
                    });
                }
                if (progressBarStatus >= 100) {
                    // sleeping for 1 second after operation completed
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressDialog.dismiss();
                }
            }
        }).start();


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<mailDetails.size();i++)
                {
                    if(mailDetails.get(i).getLocation().equals(sTown))
                    {
                        recevierEmail = (recevierEmail+mailDetails.get(i).getEmailId()).trim();

                        Log.d("tag","Mailid  "+recevierEmail);
                        sendMail();
                        break;
                    }
                }


            }
        },3000);

    }

    private void sendMail() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Email,password);
            }
        });

        try {
            //Initialize email content
            Message message = new MimeMessage(session);
            //Sender email
            message.setFrom(new InternetAddress(Email));
            //Recipient email
           /*setRecipients() is used to send mail to single mail id
               addRecipients() is used to send mail to multiple mail ids
            */
            //message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("karukola33@gmail.com"));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recevierEmail));
            //Email Subject
            message.setSubject("Order through "+activity);
            //Email Text
            message.setText("This Order is from: "+firebaseAuth.getCurrentUser().getPhoneNumber()
                    +"\n"+"Id: "+userID+"\n\n\n"+"ZOI Team");

            //send mail
            new SendMail().execute(message);

        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }

    }

    private void okayDialog()
    {
        AlertDialog.Builder okBuilder = new AlertDialog.Builder(UserAddress.this);
        okBuilder.setMessage("Your Order has been successfully palced")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FireMsgService notification = new FireMsgService();
                        notification.sendOrderNotification(activity,getApplicationContext());
                        Intent intent=new Intent(UserAddress.this, History.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity( intent);

                    }
                }).setCancelable(false);
        AlertDialog alertDialog=okBuilder.create();
        alertDialog.show();
    }

    private void updateNotificationCount() {
        documentReference.update("notificationCount", ++notificationCount).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }


    private int doOperation() {

        //The range of ProgressDialog starts from 0 to 10000
        while (fileSize <= 10000) {
            fileSize++;
            if (fileSize == 1000) {
                return 10;
            } else if (fileSize == 2000) {
                return 20;
            } else if (fileSize == 3000) {
                return 30;
            } else if (fileSize == 4000) {
                return 40; // you can add more else if
            }
        }
        return 100;

    }

    private void loadMailCredentials() {
        mailDetails.clear();
        String mailData[] = martCredentials.split("\\?\\?\\?");
        for(int i=0; i<mailData.length; i++)
        {
            itemslist = Arrays.asList(mailData[i].split("[~]"));
            mailDetails.add(new SendMail(itemslist.get(0),itemslist.get(1)));
        }

    }

    //For Camera and Gallery Uploading
    private  void imageUpload(StorageReference ImageFolder, final DatabaseReference databaseReference1)
    {
        for(int i =0;i <images.size();i++)
        {
            Uri IndividualImage = images.get(i);
            final StorageReference imagename = ImageFolder.child(IndividualImage.getLastPathSegment());

            imagename.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            SendLink(url,databaseReference1);
                        }});
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    },500);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }



    private void SendLink(String url,DatabaseReference databaseReference1) {
        databaseReference1 = firebaseDatabase.getReference("%Orders").child("images");
        DatabaseReference databaseReference3 = firebaseDatabase.getReference(firebaseAuth.getUid()).child("OrderInfo").child("Through Images").child("Images");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        databaseReference1.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        databaseReference3.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // progressDialog.dismiss();
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

    private class SendMail extends AsyncTask<Message,String,String> {
        private String martCredentials;
        private String emailId,location;
        private ProgressDialog progressDialog;

        public SendMail(String location, String emailId) {
            this.emailId = emailId;
            this.location = location;
        }

        public SendMail() {

        }

        public String getMartCredentials() {
            return martCredentials;
        }

        public void setMartCredentials(String martCredentials) {
            this.martCredentials = martCredentials;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }



        @Override
        protected String doInBackground(Message... messages) {
            try {
                //When Success
                Transport.send(messages[0]);
                return  "Success";
            }
            catch (MessagingException e)
            {
                //When error
                e.printStackTrace();
                return  "Error";
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UserAddress.this,
                    Html.fromHtml("<font color='#509324'>Please Wait...</font>"),
                    "Your order is getting placed\nDo not exit the page",true,false);
            updateNotificationCount();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("Success"))
            {
                okayDialog();
            }
        }
    }
}
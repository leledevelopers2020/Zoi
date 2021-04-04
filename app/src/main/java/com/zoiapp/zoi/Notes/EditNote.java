package com.zoiapp.zoi.Notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {
    ArrayList<UserProfile> dataList;
    RecyclerView recyclerViewEdit;
    Adapter madapter;
    FloatingActionButton save;
    Intent data;
    String[] item,quantity;
    static String userID,istring,cstring,noteID;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        data = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        save = (FloatingActionButton) findViewById(R.id.saveItems1);
        bulidArray(0);
        loadDatatoList();
        bulidRecyclerView();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnectivity())
                {
                    for (int i = 0; i < Adapter.mList.size(); i++) {
                        item[i] = Adapter.mList.get(i).getItemNames();
                        quantity[i] = Adapter.mList.get(i).getItemQuantity();
                        System.out.println(item[i] + " " + quantity[i]);
                    }
                uploadDatatoDatabase(item, quantity);
                bulidArray(1);
                loadDatatoList();
                bulidRecyclerView();
            }
                else
                {
                    Toast.makeText(EditNote.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
        }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void bulidArray(int updateValue) {
        if(updateValue == 0) {
            istring = data.getStringExtra("content");
            cstring = data.getStringExtra("quantity");
            noteID = data.getStringExtra("noteID");
        }
        item= istring.split("\n");
        for(int i=0;i<item.length;i++)
        {
            System.out.println(item[i]);
        }
        quantity = cstring.split("\n");
        for(int j=0;j<quantity.length;j++)
        {
            System.out.println(quantity[j]);
        }
    }


    public void bulidRecyclerView()
    {
        recyclerViewEdit = findViewById(R.id.recyclerviewEdit);
        recyclerViewEdit.setHasFixedSize(true);
        recyclerViewEdit.setLayoutManager(new LinearLayoutManager(this));
        madapter = new Adapter(dataList);
        recyclerViewEdit.setAdapter(madapter);
    }

    private void loadDatatoList() {
        dataList = new ArrayList<>();
        for(int i=0;i<item.length;i++) {
            dataList.add(new UserProfile(item[i],quantity [i]));
        }
    }

    private void uploadDatatoDatabase(String[] item, String[] cost) {
        istring = "";
        cstring = "";
        for(int j=0;j<item.length;j++)
        {
            if(!item[j].isEmpty() && !cost[j].isEmpty()) {
                istring = istring + item[j] + "\n";
                cstring = cstring + cost[j] + "\n";
            }
        }
        sendNote(istring,cstring);
    }
    private void sendNote(String str1, String str2) {
        if(internetConnectivity())
        {
        final DocumentReference documentReference = firebaseFirestore.collection("notes").document(userID).collection("myNotes").document(noteID);
        if (str1.isEmpty() || str2.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditNote.this);
            builder.setMessage("Alert!\nThere is no data.\nThis action may lead to deletion of note.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditNote.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                 }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            bulidArray(0);
                            loadDatatoList();
                            bulidRecyclerView();
                        }
                    }).setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            Map<String, Object> items = new HashMap<>();
            items.put("ItemNames", str1);
            items.put("ItemQuantity", str2);
            documentReference.update(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditNote.this, "Note updated succesfully", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditNote.this, "update failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
        else
        {
            Toast.makeText(EditNote.this,"No Internet Connection",Toast.LENGTH_LONG).show();
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

}
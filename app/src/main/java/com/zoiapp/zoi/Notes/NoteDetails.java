package com.zoiapp.zoi.Notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zoiapp.zoi.R;
import com.zoiapp.zoi.UserRequired.UserAddress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoteDetails extends AppCompatActivity implements NoteDialog.NoteDialogListener {

    private TextView content,quantity;
    Intent data;
    int j,i=-1;
    static String itemNames,itemQuantity,noteID,userID;
    static boolean state = true;
    FirebaseFirestore firebaseFirestore;
    private static ArrayList<String> itemN=new ArrayList<>();
    private static ArrayList<String> itemQ=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        content = findViewById(R.id.noteDetailsContent);
        quantity = findViewById(R.id.noteDetailsQuantity);
        FloatingActionButton fab = findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();

        data = getIntent();
        userID = data.getStringExtra("userID");
        noteID = data.getStringExtra("noteID");
        loadNote();
        content.setMovementMethod(new ScrollingMovementMethod());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditPage();
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
    private void loadNote() {
        if(internetConnectivity()) {
                final DocumentReference documentReference = firebaseFirestore.collection("notes")
                        .document(userID).collection("myNotes").document(noteID);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            itemNames = documentSnapshot.getString("ItemNames");
                            itemQuantity = documentSnapshot.getString("ItemQuantity");
                            String[] Name = itemNames.split("\n");
                            String[] Qunatity = itemQuantity.split("\n");
                            if(state) {
                                showNotes(Name, Qunatity);
                                state = false;
                            }
                        }
                        else
                        {
                            finish();
                            state = true;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                        state = true;
                    }
                });

        }
        else
        {
            Toast.makeText(NoteDetails.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotes(String[] name, String[] qunatity) {
        i = name.length-2;
        itemN.clear();
        itemQ.clear();
        for(int i =0;i<name.length;i++)
        {
            itemN.add(name[i]);
            itemQ.add(qunatity[i]);
        }
        content.setText(itemNames);
        quantity.setText(itemQuantity);
    }

    private void getEditPage() {
        Intent i = new Intent(NoteDetails.this, EditNote.class);
        i.putExtra("content",itemNames);
        i.putExtra("quantity",itemQuantity);
        i.putExtra("noteID",noteID);
        startActivity(i);
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_note,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case  android.R.id.home:
            {
                onBackPressed();
                break;
            }
            case R.id.delete_note:
            {
                final AlertDialog.Builder delBuilder=new AlertDialog.Builder(NoteDetails.this);
                delBuilder.setMessage("Are you sure you want to delete the note?\n This action may lead to deletion of complete note.\n"+
                        Html.fromHtml("<font color='#509324'>(If you want to delete a particular item,then click the below edit icon.)</font>"))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteNote();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(false);
                AlertDialog alertDialog=delBuilder.create();
                alertDialog.show();

                break;

            }
            case R.id.add_note:
            {
                openDialog();
                break;
            }
            case R.id.send_note:
            {
                if (internetConnectivity()) {
                    // Toast.makeText(NoteDetails.this,itemNames+itemQuantity,Toast.LENGTH_LONG).show();
                    if(!itemNames.isEmpty() && !itemQuantity.isEmpty()) {
                        Intent intent = new Intent(NoteDetails.this, UserAddress.class);
                        intent.putExtra("activity", "Note");
                        intent.putExtra("orderName", itemNames);
                        intent.putExtra("orderQuantity", itemQuantity);
                        //Log.d("tag","notes"+itemNames+itemQuantity);
                        startActivity(intent);
                    }
                    break;
                }
                else
                {
                    Toast.makeText(NoteDetails.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void deleteNote() {
        if(internetConnectivity()) {
            DocumentReference deleteReference = firebaseFirestore.collection("notes").document(userID).collection("myNotes").document(noteID);
            deleteReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(NoteDetails.this, "deleted", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NoteDetails.this, "Can not be deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(NoteDetails.this,"Cannot be deleted please check your internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    public void openDialog() {
        NoteDialog noteDialog = new NoteDialog();
        if(noteDialog.isNextValue()) {
            noteDialog.show(getSupportFragmentManager(), "Note Dialog");
            i++;
        }
    }
    @Override
    public void applyText(String itemname, String itemquantity, Boolean state) {
        String str3="";
        String str4="";
        if(state)
        {
            itemN.add(itemname);
            itemQ.add(itemquantity);
            openDialog();

        }
        else if (!state)
        {
            itemN.add(itemname);
            itemQ.add(itemquantity);

            for(j=0;j<itemN.size();j++)
            {
                if(itemN.get(j)!=null||itemQ.get(j)!=null) {
                    if(j == itemN.size()-1)
                    {
                        str3 = str3 + itemN.get(j) ;
                        str4 = str4 + itemQ.get(j) ;
                    }
                    else
                    {
                        str3 = str3 + itemN.get(j) + "\n";
                        str4 = str4 + itemQ.get(j) + "\n";
                    }
                }
            }
            sendNote(str3,str4);
            content.setText(str3);
            quantity.setText(str4);
            i=-1;
        }
    }
    private void sendNote(String str1, String str2) {
        if (internetConnectivity()) {
            final DocumentReference documentReference = firebaseFirestore.collection("notes").document(userID).collection("myNotes").document(data.getStringExtra("noteID"));
            Map<String, Object> items = new HashMap<>();
            items.put("ItemNames", str1);
            items.put("ItemQuantity", str2);
            documentReference.update(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(NoteDetails.this, "Note updated succesfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NoteDetails.this, "update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(NoteDetails.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        state = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadNote();
        state = true;
    }
}
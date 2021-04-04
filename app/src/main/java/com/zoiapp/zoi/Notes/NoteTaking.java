package com.zoiapp.zoi.Notes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NoteTaking extends AppCompatActivity implements NoteDialog.NoteDialogListener {

    RecyclerView noteList;
    Adapter adapter;
    TextView emptyData;
    private  ArrayList<String> itemN=new ArrayList<>();
    private  ArrayList<String> itemQ=new ArrayList<>();
    private static String[] itemNC=new String[20];
    private static String[] itemQC=new String[20];
    int j,i=-1;
    private String userID;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<UserProfile, NoteViewHolder> noteAdapter;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notetaking);
        noteList = (RecyclerView) findViewById(R.id.noteView);
        emptyData = (TextView) findViewById(R.id.noteEmpty);

        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseFirestore = FirebaseFirestore.getInstance();//cloud firestore
        userID = firebaseAuth.getCurrentUser().getUid();
        if(internetConnectivity()) {
            Query query = firebaseFirestore.collection("notes").document(userID).collection("myNotes");
            FirestoreRecyclerOptions<UserProfile> allnotes = new FirestoreRecyclerOptions.Builder<UserProfile>()
                    .setQuery(query, UserProfile.class)
                    .build();

            System.out.println("??????????/ "+allnotes);
            noteAdapter = new FirestoreRecyclerAdapter<UserProfile, NoteViewHolder>(allnotes) {

                @NonNull
                @Override
                public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
                    return new NoteViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final UserProfile userProfile) {
                    //count = count+i;
                    checkData();
                    noteViewHolder.noteContent.setText(userProfile.getItemNames());
                    noteViewHolder.noteQuantity.setText(userProfile.getItemQuantity());
                    noteViewHolder.noteTitle.setText(userProfile.getDate()+" "+userProfile.getOrderTime());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        noteViewHolder.cardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(getRandomColor(),null));
                    }
                    final String noteId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                    noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long now = System.currentTimeMillis();
                            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                                return;
                            }
                            Intent i = new Intent(v.getContext(), NoteDetails.class);
                            i.putExtra("noteID",noteId);
                            i.putExtra("userID",userID);
                            v.getContext().startActivity(i);
                            overridePendingTransition(0, 0);
                        }
                    });
                    ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                    menuIcon.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(final View v) {
                            final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                            PopupMenu menu = new PopupMenu(v.getContext(),v);
                            menu.setGravity(Gravity.END);
                            menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Intent i = new Intent(v.getContext(), EditNote.class);
                                    i.putExtra("content",userProfile.getItemNames());
                                    i.putExtra("quantity",userProfile.getItemQuantity());
                                    i.putExtra("noteID",docId);
                                    startActivity(i);
                                    overridePendingTransition(0, 0);
                                    return false;
                                }
                            });
                            menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if(internetConnectivity()) {
                                        DocumentReference deleteReference = firebaseFirestore.collection("notes").document(userID).collection("myNotes").document(docId);
                                        deleteReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //node deleted
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), "Can not be deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(NoteTaking.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                                    }

                                    return false;
                                }

                            });
                            menu.show();
                        }
                    });
                }
                @Override
                public int getItemCount() {
                    return super.getItemCount();
                }
            };


            final List<String> content = new ArrayList<>();
            List<String> quantity = new ArrayList<>();
            FloatingActionButton fab = findViewById(R.id.addNoteIcon);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(internetConnectivity()) {
                        openDialog();
                    }
                    else {
                        Toast.makeText(NoteTaking.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //noteList.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            noteList.setLayoutManager( new LinearLayoutManager(this));
            noteList.setAdapter(noteAdapter);
        }
        else {
            Toast.makeText(NoteTaking.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }




    }
    private void checkData() {
        if(noteAdapter.getItemCount() ==0)
        {
            emptyData.setVisibility(View.VISIBLE);
            noteList.setVisibility(View.INVISIBLE);
        }
        else
        {
            emptyData.setVisibility(View.INVISIBLE);
            noteList.setVisibility(View.VISIBLE);
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

    public void openDialog() {
        NoteDialog noteDialog = new NoteDialog();
        ///noteDialog
        if(noteDialog.isNextValue()) {
            noteDialog.show(getSupportFragmentManager(), "Note Dialog");
            i++;
        }
    }

    @Override
    public void applyText(String itemname, String itemquantity, Boolean state) {
        String str1="";
        String str2="";
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
                        str1 = str1 + itemN.get(j) ;
                        str2 = str2 + itemQ.get(j) ;
                    }
                    else
                    {
                        str1 = str1 + itemN.get(j) + "\n";
                        str2 = str2 + itemQ.get(j) + "\n";
                    }
                }
            }
            sendNote(str1,str2);
        }
    }

    private void sendNote(final String str1, final String str2) {
        if (internetConnectivity()) {
            final DocumentReference documentReference = firebaseFirestore.collection("notes").document(userID).collection("myNotes").document();
            Map<String, Object> items = new HashMap<>();
            items.put("ItemNames", str1);
            items.put("ItemQuantity", str2);
            items.put("date", DateFormat.getDateInstance().format(new Date()));
            items.put("orderTime", DateFormat.getTimeInstance().format(new Date()));
            documentReference.set(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    itemN.clear();
                    itemQ.clear();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NoteTaking.this, "Note add failed", Toast.LENGTH_LONG).show();
                }
            });

        }
        else {
            Toast.makeText(NoteTaking.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }
    private class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView noteQuantity,noteContent,noteTitle;
        View view;
        CardView cardView;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteContent = itemView.findViewById(R.id.itemName);
            noteQuantity = itemView.findViewById(R.id.itemQuantity);
            noteTitle = itemView.findViewById(R.id.titles);
            cardView = itemView.findViewById(R.id.noteCard);
            view =itemView;
        }
    }
    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(internetConnectivity())
        {
            noteAdapter.startListening();
        }
        else
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            noteAdapter.stopListening();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent i = new Intent(Notetaking.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(0, 0);

         */
        finish();
    }
}
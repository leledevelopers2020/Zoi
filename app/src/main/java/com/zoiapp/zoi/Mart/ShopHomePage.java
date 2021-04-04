package com.zoiapp.zoi.Mart;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.ProductDetails;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopHomePage extends AppCompatActivity implements CategoryAdapter.CategoryAdapterEvent {
    private RecyclerView recyclerViewHorizonatal, recyclerViewVertical;
    private ShimmerFrameLayout shimmerFrameLayoutHorizontal,shimmerFrameLayoutVertical;
    FloatingActionButton cartButton;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseRef;
    private String mLocation;
    private String categoryNames, categoryCode, categoryType;
    private String[] categoryN, categoryC;
    private String  productPrice, productCode;
    private String cartItemsCode,cartItemName,cartItemQuantity;
    private String[] itemCode, itemQuantity, items, Category;
    private ArrayList<ItemModelClass> categoryHlist;
    private ArrayList<ItemModelClass> categoryVlist;
    static ArrayList<ItemModelClass> productsList;
    static ArrayList<String> productsNames;
    private ArrayList<String> categoryImages;
    ArrayList<ProductDetails> productList, categoryList;   //mummy evar leru intlocall me on 8888940634 ok
    CategoryAdapter categoryAdapter;
    ShopAdapter shopAdapter;
    private List<String> itemslist,categorylist;
    static String lang = "English";
    static boolean loadstate;
    static boolean cartState;
    String category;
    Intent data;
    //int iterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_home_page);
        loadView();
        loadstate = true;
        cartState = true;
        category="G";
        data = getIntent();
        String categoryFromSlider = data.getStringExtra("Category");
        if(categoryFromSlider != null)
            category = categoryFromSlider;
        categoryImages = new ArrayList<>();
        categoryHlist = new ArrayList<>();
        categoryVlist = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryHlist,this);
        shopAdapter = new ShopAdapter(categoryVlist);
        productList = new ArrayList<ProductDetails>();
        categoryList = new ArrayList<ProductDetails>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid()).child("Cart");
        getMartLocation();
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(ShopHomePage.this,Cart.class);
                cart.putExtra("retriveState",true);
                startActivity(cart);
            }
        });

    }

    private void loadHorzontalShrimmer() {
        recyclerViewHorizonatal.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewHorizonatal.setLayoutManager(layoutManager);
        recyclerViewHorizonatal.setAdapter(categoryAdapter);
    }

    private void loadVerticalShrimmer()
    {
        recyclerViewVertical.setHasFixedSize(true);
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVertical.setAdapter(shopAdapter);
    }

    //retriving location
    private void getMartLocation() {
        loadHorzontalShrimmer();
        loadVerticalShrimmer();
        if(internetConnectivity()) {
            DatabaseReference loc = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Location");
            loc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    mLocation = userProfile.getMartLocation();
                    if (mLocation.isEmpty()) {
                        retriveMartLocationList();
                    } else if (!mLocation.isEmpty() && loadstate) {
                        loadstate = false;
                        loadAllData(mLocation, category);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            Toast.makeText(this,"No Internet Coneection",Toast.LENGTH_SHORT).show();
        }
    }
    //retrive list of martloaction
    private void retriveMartLocationList() {
        if(internetConnectivity()) {
            DatabaseReference loc = firebaseDatabase.getReference("Products").child("UserLocationList");
            loc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                    String location = snapshot.child("LocationList").getValue().toString();//itemModelClass.getMartLocation();
                    String[] MartLocation = location.split(" \n ");
                    setMartLocationDataBase(MartLocation);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }
    //opening location alert box
    private void setMartLocationDataBase(String[] martLocation) {
        final String previousLoc = mLocation;
        View view = getLayoutInflater().inflate(R.layout.martlocationdialog,null);
        final Spinner spinner = (Spinner) view.findViewById(R.id.martSpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShopHomePage.this,android.R.layout.simple_spinner_item,martLocation);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);




        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShopHomePage.this);
        mBuilder.setView(view)
                .setTitle("Select your mart location")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (internetConnectivity()) {
                            mLocation = spinner.getSelectedItem().toString();
                            if (previousLoc!=mLocation) {

                                //Subscribe and unsubscribe to topics
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(previousLoc);
                                FirebaseMessaging.getInstance().subscribeToTopic(mLocation);

                                //save new location
                                DatabaseReference loc = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Location");
                                UserProfile userProfile = new UserProfile();
                                userProfile.setMartLocation(mLocation);
                                loc.setValue(userProfile);
                                loadAllData(mLocation, category);
                            }
                            dialog.dismiss();
                        }
                        else {
                            dialog.dismiss();
                            Toast.makeText(ShopHomePage.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return;
    }

    private void saveitems(String pName, String pQunatity, String pCode) {

        cartItemName = pName;
        cartItemQuantity = pQunatity;
        cartItemsCode = pCode;

    }

    //retriving category details
    public void loadAllData(final String location, final String category) {
        System.out.println("!!!!!!!!!!!Start loadAllData method!!!!!!!!!!!!");

        DatabaseReference categoryData = firebaseDatabase.getReference("Products").child(location).child("Category").child(lang);
        DatabaseReference productData = firebaseDatabase.getReference("Products").child(location).child("ProductDetails").child(lang);
        if (internetConnectivity())
        {
            //loading category data
            categoryData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                    String category = itemModelClass.getCategoryDetails();
                    categoryList.clear();
                    Category = category.split("\\?\\?\\?");
                    for(int i=0;i<Category.length;i++)
                    {
                        categorylist = Arrays.asList(Category[i].split("[#]"));
                        categoryList.add(new ProductDetails(categorylist.get(0), categorylist.get(1),0 ));
                    }
                    loadCategories(location);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //loading product details
            productData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                    String item = itemModelClass.getProductDetails();
                    productList.clear();
                    items = item.split("\\?\\?\\?");
                    for(int i=0;i<items.length;i++)
                    {
                        itemslist = Arrays.asList(items[i].split("[@#!%]"));
                        productList.add(new ProductDetails(itemslist.get(0), itemslist.get(1), itemslist.get(2), itemslist.get(3), Double.parseDouble(itemslist.get(4)) ));
                    }
                    getCategoryTypeData(category,mLocation,cartState);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    //retriving category images
    private void loadCategories(final String location) {
        categoryHlist.clear();
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Products").child(location).child("images");
        //iterator = 0;
        for(int i = 0;i<categoryList.size();i++)
        {
            Query query = databaseReference1.child(categoryList.get(i).getCatCode());
            final int finalI = i;

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String cimges = "";

                    try {

                        cimges = snapshot.child("imageUrl").getValue().toString();
                    }
                    catch (Exception e)
                    {

                        return;
                    }
                    categoryHlist.add(new ItemModelClass(categoryList.get(finalI).getCatName(),
                            categoryList.get(finalI).getCatCode(),cimges,location));

                    buildHorizontalRecycler();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
 }

    //loading data into horizontal recyclerview
    private void buildHorizontalRecycler() {
        recyclerViewHorizonatal.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizonatal.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(categoryHlist, this);
        categoryAdapter.isShimmerHorzontalPage = false;
        recyclerViewHorizonatal.setAdapter(categoryAdapter);
    }

    //loading views
    private void loadView() {
        recyclerViewHorizonatal = findViewById(R.id.recyclerviewHorizontal);
        recyclerViewVertical = findViewById(R.id.recyclerviewVertical);
        cartButton = findViewById(R.id.cartButton);
        shimmerFrameLayoutVertical = findViewById(R.id.shimmerVerticalPage);
        shimmerFrameLayoutHorizontal = findViewById(R.id.shimmerHorizontalPage);
    }

    @Override
    public void onClickProduct(ItemModelClass itemModelClass) {
        categoryType = itemModelClass.getCategoryCode();
        mLocation = itemModelClass.getMartLocation();
        categoryVlist.clear();
        shopAdapter.notifyItemRangeRemoved(0, shopAdapter.getItemCount());
        getCategoryTypeData(categoryType,mLocation,cartState);
    }
    //retriving product details
    public void getCategoryTypeData(final String cType, String location, final boolean state) {
        ArrayList<ProductDetails> selectedData = new ArrayList<>();
        for(int i= 0;i<productList.size();i++) {
            if(productList.get(i).getCatCode().equals(cType))
            {
                selectedData.add(productList.get(i));
            }
            if(i==productList.size()-1)
            {
                getData(selectedData,cType,state);
            }
        }
    }


    private void getData(final ArrayList<ProductDetails> item, String cType, boolean state) {
        categoryVlist.clear();


        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Products").child(mLocation).child("images");
        for(int i= 0;i<item.size();i++)
        {
            productCode = item.get(i).getCode();
            final int finalI = i;
            Query query = databaseReference1.child(productCode);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String returnimage;
                    try {

                        returnimage  = snapshot.child("imageUrl").getValue().toString();
                        productPrice = snapshot.child("price").getValue().toString();
                    }
                    catch (Exception e)
                    {

                        return;
                    }
                    double price = Double.parseDouble(productPrice);
                    if(item.get(finalI).getQunt().toLowerCase().equals("kg") || item.get(finalI).getQunt().toLowerCase().equals("g")
                            || item.get(finalI).getQunt().toLowerCase().equals("ml") || item.get(finalI).getQunt().toLowerCase().equals("l")
                            || item.get(finalI).getQunt().toLowerCase().equals("units"))
                    {
                        categoryVlist.add(new ItemModelClass(item.get(finalI).getCode(), item.get(finalI).getName(), price, item.get(finalI).getQunt().toLowerCase(), returnimage, item.get(finalI).getOffer()));

                        bulidVerticalRecyclerView(categoryVlist);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    //loading data into vertical recyclerview
    private void bulidVerticalRecyclerView(ArrayList<ItemModelClass> l1) {
        recyclerViewVertical.setHasFixedSize(true);
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(this));
        shopAdapter = new ShopAdapter(l1);
        shopAdapter.isShimmerVerticalPage = false;
        recyclerViewVertical.setAdapter(shopAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.cart,menu);

        MenuItem searchIcon= menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchIcon.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shopAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_loaction:
            {
                AlertDialog.Builder locChangeBuilder=new AlertDialog.Builder(ShopHomePage.this);
                locChangeBuilder.setMessage("Location change will effect your items price added in the cart (or) you may loose your items")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(internetConnectivity()) {
                                    cartState = false;
                                    retriveMartLocationList();
                                    categoryHlist.clear();
                                }
                                else {
                                    Toast.makeText(ShopHomePage.this,"No internet Connection",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = locChangeBuilder.create();
                dialog.show();


                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loadstate = true;
        finish();
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
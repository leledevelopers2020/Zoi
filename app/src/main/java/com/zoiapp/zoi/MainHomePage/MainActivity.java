package com.zoiapp.zoi.MainHomePage;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.zoiapp.zoi.Cam_Gal_Phn.Camera;
import com.zoiapp.zoi.Cam_Gal_Phn.Gallery;
import com.zoiapp.zoi.Cam_Gal_Phn.PhoneCalls;
import com.zoiapp.zoi.Drawer.NavigationDrawer;
import com.zoiapp.zoi.Drawer.Profile;
import com.zoiapp.zoi.History.History;
import com.zoiapp.zoi.Mart.Cart;
import com.zoiapp.zoi.Mart.ShopHomePage;
import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.NotificationModelClass;
import com.zoiapp.zoi.ModalClasses.ProductDetails;
import com.zoiapp.zoi.Notes.NoteTaking;
import com.zoiapp.zoi.Notifications.NotificationPage;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.UserGuide.Guide;
import com.zoiapp.zoi.UserRequired.AddDetails;
import com.zoiapp.zoi.UserRequired.Register;
import com.zoiapp.zoi.UserRequired.Summary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import hotchemi.android.rate.AppRate;

public class  MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE =1 ;
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference notif;
    private ImageButton phone,camera,note,mart,gallery;
    public TextView orderinfo;
    private Button viewButton,historyButton;
    public int stage;
    Toolbar toolbar;
    DrawerLayout drawerlayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    TextView wish,welcome  ;
    TextView bell_count,cart_item_count;
    MenuItem menubell,menucart;
    int pendingNotifications;
    int cart_items=0;
    //For Category ViewPager/
    private static ViewPager viewpager_category;
    private static LinearLayout mDotLayout;
    private TextView[] mDots;
    private SliderAdapter sliderAdapter;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;
    final long PERIOD_MS = 3000;
    //accessing time/
    Calendar calender=Calendar.getInstance();
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HHmm");
    int Datetime;
    ArrayList<String> names1;
    ArrayList<String> code1;
    ArrayList<String> images;
    static int len;
    static RecyclerView offersView;
    static ArrayList<ProductDetails> product;
    static ArrayList<ProductDetails> allProducts;
    static ArrayList<ItemModelClass> productsList;
    static ArrayList<String> productsName;
    private static ArrayList<ItemModelClass> offersList;
    private String[] items;
    List<String> itemslist;
    List<String> sliderPageList;
    OffersAdapter offersAdapter;
    private  String location;
    private static int   lastLocation = 0;
    static String lang = "English";
    static boolean appPauseState;
    static String previousLoc;
    static String previousItems;
    static String previousSlides ;
    static int prevoiusLocCount;
    NotificationModelClass notificationModelClass;
    View notificationIcon;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannels();
        Datetime = Integer.parseInt(simpleDateFormat.format(calender.getTime()));
        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseDatabase = FirebaseDatabase.getInstance();//realtime database
        firebaseFirestore = FirebaseFirestore.getInstance();
        notif = firebaseFirestore.collection("Customers Info").document(firebaseAuth.getCurrentUser().getUid());
        askPermission();
        loadViews();
        names1 = new ArrayList<>();
        code1 = new ArrayList<>();
        images = new ArrayList<>();
        product = new ArrayList<>();
        allProducts = new ArrayList<>();
        productsList = new ArrayList<ItemModelClass>();
        offersList = new ArrayList<ItemModelClass>();
        offersAdapter = new OffersAdapter(offersList);
        productsName = new ArrayList<>();
        appPauseState = true;
        previousLoc = "";
        previousItems = "";
        previousSlides = "";
        if (checkConnectivity())
        {
            getMartLocation();
            loadAddDetailsPage();
        }
        else
        {
            Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ZOI");
        toggle = new ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.navigationdrawer_open,R.string.navigationdrawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        drawerlayout.addDrawerListener(toggle);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch(id) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_profile:
                        Intent intent = new Intent(MainActivity.this, Profile.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        Logout();
                        break;
                    case R.id.nav_about_us: {
                        Intent aboutUs = new Intent(MainActivity.this, NavigationDrawer.class);
                        aboutUs.putExtra("SelectedItem", "AboutUs");
                        startActivity(aboutUs);
                    }
                    break;


                    case R.id.nav_privacy_policy:
                    {
                        Intent privacyPolicy = new Intent(MainActivity.this, NavigationDrawer.class);
                        privacyPolicy.putExtra("SelectedItem", "PrivacyPolicy");
                        startActivity(privacyPolicy);

                    }
                    break;


                    case R.id.nav_tandc:
                        Intent termsConditions=new Intent(MainActivity.this, NavigationDrawer.class);
                        termsConditions.putExtra("SelectedItem","TermsAndConditions");
                        startActivity(termsConditions);
                        break;
                    case R.id.nav_feedback:
                        Intent feedBack=new Intent(MainActivity.this, NavigationDrawer.class);
                        feedBack.putExtra("SelectedItem","FeedBack");
                        startActivity(feedBack);
                        break;
                    case R.id.nav_rate_us:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse( "market://details?id="+ "com.zoiapp.zoi")));
                        break;
                }
                drawerlayout.closeDrawers();
                return true;
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhoneCalls.class));
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Camera.class));
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Gallery.class));
            }
        });
        mart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShopHomePage.class);
                startActivity(intent);
            }
        });
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteTaking.class));
            }
        });
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Summary.class));
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, History.class));
            }
        });
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotificationPage.class);
                intent.putExtra("notificationCount",pendingNotifications);
                startActivity(intent);
            }
        });
        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(10)
                .setRemindInterval(5)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

    }


    private void loadViews() {
        offersView = (RecyclerView) findViewById(R.id.card_view);
        drawerlayout=findViewById(R.id.drawerlayout);
        nav_view=findViewById(R.id.nav_view);
        phone = (ImageButton)findViewById(R.id.phonecall);
        camera=(ImageButton)findViewById(R.id.camera);
        gallery=(ImageButton)findViewById(R.id.gallery) ;
        mart = (ImageButton)findViewById(R.id.mart);
        note=(ImageButton)findViewById(R.id.note);
        orderinfo=(TextView)findViewById(R.id.orderinfo);
        viewButton=(Button) findViewById(R.id.viewdataButton);
        historyButton=(Button) findViewById(R.id.history);
        toolbar= findViewById(R.id.toolbar);
        welcome=findViewById(R.id.welcome);
        View nav_header=findViewById(R.id.nav_header);

                viewpager_category=findViewById(R.id.viewpager_category);
        mDotLayout=findViewById(R.id.mDotLayout);
        wish=findViewById(R.id.wish);
        notificationIcon = (View) findViewById(R.id.notificationIcon);
    }

    private void loadAddDetailsPage() {
        final DocumentReference documentReference1 =
                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference1.get().addOnSuccessListener(  new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String address= documentSnapshot.getString("UserAddress");
                if(address!=null)
                {
                    Intent intent=new Intent(MainActivity.this, AddDetails.class);
                    startActivity(intent);
                }

            }
        });
    }
    private void loadShimmer() {
        offersView.setHasFixedSize(true);
        offersView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL, false));
        offersView.setAdapter(offersAdapter);

        sliderAdapter = new SliderAdapter(this, names1, code1, images);
        viewpager_category.setAdapter(sliderAdapter);
    }

    private void getMartLocation() {
        loadShimmer();
        final DatabaseReference loc = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Location");
        loc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location = snapshot.child("martLocation").getValue().toString();//userProfile.getUserLocation();
                if(!location.isEmpty() && previousLoc != location)
                {
                    lastLocation++;
                    loadSliderView(location);
                    loadRecyclerView(location);
                    previousLoc = location;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadRecyclerView(String location) {
        System.out.println("!!!!!!!!!!!start loadRecyclerView method!!!!!!!!!!!!");
        DatabaseReference card = firebaseDatabase.getReference("Products").child(location).child("DailyOffers").child("English");
        final DatabaseReference databaseReference1 = firebaseDatabase.getReference("Products").child(this.location).child("images");
        card.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                String item = itemModelClass.getProductDetails();
                if (previousItems != item) {
                    previousItems = item;
                    product.clear();
                    offersList.clear();
                    items = item.split("\\?\\?\\?");
                    for (int i = 0; i < items.length; i++) {
                        itemslist = Arrays.asList(items[i].split("[@#!%]"));
                        product.add(new ProductDetails(itemslist.get(0), itemslist.get(1), itemslist.get(2), itemslist.get(3), Double.parseDouble(itemslist.get(4))));

                    }

                    for (int i = 0; i < product.size(); i++) {
                        Query im = databaseReference1.child(product.get(i).getCode());
                        final int finalI = i;
                        im.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String image;
                                double price;
                                try {
                                    image = snapshot.child("imageUrl").getValue().toString();
                                    price = Double.parseDouble(snapshot.child("price").getValue().toString());
                                }
                                catch (Exception e)
                                {
                                    return;
                                }
                                if(product.get(finalI).getQunt().toLowerCase().equals("kg") || product.get(finalI).getQunt().toLowerCase().equals("g")
                                        || product.get(finalI).getQunt().toLowerCase().equals("ml") || product.get(finalI).getQunt().toLowerCase().equals("l")
                                        || product.get(finalI).getQunt().toLowerCase().equals("units"))
                                {
                                    offersList.add(new ItemModelClass(product.get(finalI).getCode(), product.get(finalI).getName(), price, product.get(finalI).getQunt(), image, product.get(finalI).getOffer()));
                                     buildOfferList();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void buildOfferList() {
        if(lastLocation >1 && !appPauseState)
        {
            offersAdapter.notifyItemRangeRemoved(0,offersAdapter.getItemCount());
            offersAdapter = new OffersAdapter(offersList);
            offersAdapter.isShimmerOffers = false;
            offersView.setAdapter(offersAdapter);
            offersAdapter.notifyItemRangeInserted(0,offersList.size());
        }
        else
        {
            offersView.setHasFixedSize(true);
            offersView.setLayoutManager(new GridLayoutManager(this,2,GridLayoutManager.VERTICAL, false));
            offersAdapter = new OffersAdapter(offersList);
            offersAdapter.isShimmerOffers = false;
            offersView.setAdapter(offersAdapter);
            appPauseState = false;
        }
    }

    private void  loadSliderView(String location) {
        if(checkConnectivity())
        {
            final ArrayList<ItemModelClass> slidesList = new ArrayList<>();
            final DatabaseReference slider = firebaseDatabase.getReference("Products").child(location).child("SliderPage").child("English");
            final DatabaseReference sliderimages = firebaseDatabase.getReference("Products").child(this.location).child("SliderPage").child("images");
            slider.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ItemModelClass sliderDetails = snapshot.getValue(ItemModelClass.class);
                    String item = sliderDetails.getSliderPagesData();
                    if (previousSlides != item) {
                        previousSlides = item;
                        names1.clear();
                        code1.clear();
                        images.clear();
                        String[] data = item.split("\\?\\?\\?");
                        for (int i = 0; i < data.length; i++) {
                            sliderPageList = Arrays.asList(data[i].split("#"));
                            names1.add(sliderPageList.get(0));
                            code1.add(sliderPageList.get(1));
                        }
                        len = names1.size();
                        for (int i = 0; i < len; i++) {
                            Query im = sliderimages.child(names1.get(i));
                            final int finalI = i;
                            im.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String image = snapshot.child("imageUrl").getValue().toString();
                                    saveImage(image, finalI);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    public void saveImage(String img,int i)
    {
        images.add(img);
        if (i == len - 1) {
            prevoiusLocCount = lastLocation;
            mDotsIndicator(0);
            if(lastLocation>1 && !appPauseState)
            {
                sliderAdapter.addPages();
                sliderAdapter = new SliderAdapter(this, names1, code1, images);
                sliderAdapter.isShimmerSlider = false;
                viewpager_category.setAdapter(sliderAdapter);
            }
            else {
                sliderAdapter = new SliderAdapter(this, names1, code1, images);
                sliderAdapter.isShimmerSlider = false;
                viewpager_category.setAdapter(sliderAdapter);
            }
            viewpager_category.setCurrentItem(0, true);
            viewpager_category.addOnPageChangeListener(viewListener);
            autoScrollSliderThread();
        }
    }

    private void autoScrollSliderThread() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if(lastLocation == prevoiusLocCount && !appPauseState) {
                    if (currentPage >= mDots.length) {
                        currentPage = 0;
                    }
                    viewpager_category.setCurrentItem(currentPage++, true);
                }
                else
                    return;
            }
        };

        if(lastLocation == prevoiusLocCount) {
            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        }
        else {
            return;
        }
    }

    //For Category ViewPager/
    public void mDotsIndicator( int position ){
        mDots=new TextView[len];
        mDotLayout.removeAllViews();
        for(int i=0;i<mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.design_default_color_primary_dark));
            mDotLayout.addView(mDots[i]);
        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.red));
        }


    }

    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED ) {
            } else {
                Toast.makeText(MainActivity.this, "Permission are Requried to use app Services", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkConnectivity(){
        ConnectivityManager cm=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null){
            NetworkInfo info= cm.getActiveNetworkInfo();
            if(info !=null){
                if(info.getState() ==NetworkInfo.State.CONNECTED)
                    return true;
            }
        }
        return false;
    }

    //menu related code
    public void Logout()
    {
        firebaseAuth.signOut();
        appPauseState = true;
        previousLoc = "";
        previousItems = "";
        previousSlides = "";
        finish();
        startActivity(new Intent(this, Register.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        menucart = (MenuItem) menu.findItem(R.id.cart);
        cart_item_count=findViewById(R.id.cart_item_count);
        if(cart_items==0){
            menucart.setActionView(null);
        }
        else
        {
            menucart.setActionView(R.layout.cart_badge);
            View view=menucart.getActionView();
            cart_item_count=view.findViewById(R.id.cart_item_count);
            cart_item_count.setText(String.valueOf(cart_items));
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.cart)
        {
            startActivity(new Intent(this,Cart.class));
        }
        else if(id == R.id.user_guide)
        {
            startActivity(new Intent(this,Guide.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("channel1", "Your Orders", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("These are your concerned order notifications ");
            channel1.enableLights(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            NotificationChannel channel2 = new NotificationChannel("channel2", "Miscellaneous", NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("These are offer notifications");
            manager.createNotificationChannel(channel2);
        }
    }

    public void welcome(int Datetime){
        if(checkConnectivity()) {
            final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("UserName");
                        welcome.setText("Welcome, " + name + "!");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String name = "User";
                    welcome.setText("Welcome, " + name + "!");

                }
            });
            if (Datetime > 0 && Datetime < 1200) {
                wish.setText("Good Morning!");
            } else if (Datetime >= 1200 && Datetime <= 1600) {
                wish.setText("Good AfterNoon!");
            } else if (Datetime >= 1600 && Datetime < 2359) {
                wish.setText("Good Evening!");
            } else {
                wish.setVisibility(View.GONE);
            }
            pendingNotifications = loadNotificationCount();
        }
        else
        {
            Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    private int loadNotificationCount() {

        DocumentReference notif = firebaseFirestore.collection("Customers Info").document(firebaseAuth.getCurrentUser().getUid());
        notif.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //NotificationModelClass notificationModelClass = new NotificationModelClass();
                    pendingNotifications = Objects.requireNonNull(documentSnapshot.getLong("notificationCount")).intValue();
                }
            }
        });
        return pendingNotifications;
    }

    private void showNotificationPage()
    {
        notif.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    pendingNotifications = Objects.requireNonNull(documentSnapshot.getLong("notificationCount")).intValue();
                    notificationModelClass = new NotificationModelClass(findViewById(R.id.notificationIcon));
                    notificationModelClass.updateNotificationCount(pendingNotifications);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        welcome(Datetime);
        showNotificationPage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        appPauseState = true;
        previousLoc = "";
        previousItems = "";
        previousSlides = "";
        finish();
    }
}
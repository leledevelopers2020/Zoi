package com.zoiapp.zoi.Mart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShoppingItemDetails extends AppCompatActivity{

    Intent data;
    private String imageUrl,productName,productQuantity,productCode,productinfo;
    private double productPrice,offerPercentage;
    private ImageView productImage;
    private TextView name,price,productInfo,offerPrice,offer;
    private Spinner spinnerunits,spinnervalues;
    private TextView addTocart,buyNow;
    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseRef;
    String userID;
    private static String Name,Price,Qunatity,Count,Code;
    static int datastate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_item_details);
        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseDatabase = FirebaseDatabase.getInstance();//realtime database
        userID = firebaseAuth.getCurrentUser().getUid();
        loadViews();
        data = getIntent();
        productCode = data.getStringExtra("productCode");
        imageUrl = data.getStringExtra("image");
        productName = data.getStringExtra("productName");
        productQuantity = data.getStringExtra("productQunatity");
        productPrice = data.getDoubleExtra("productPrice",0.0);
        productinfo = data.getStringExtra("productInfo");
        offerPercentage = data.getDoubleExtra("offerPercetage",0.0);
        loadSpinners();
        databaseRef = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Cart");
        addTocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity()) {
                    addcart();
                }
                else
                {
                    Toast.makeText(ShoppingItemDetails.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }

        });

        Picasso.get().load(imageUrl).into(productImage);
        name.setText(productName);
        productInfo.setText("(₹"+String.format("%.2f",productPrice)+"/ "+productinfo+")");
        price.setText("₹"+productPrice);
    }

    private void loadSpinners() {
        ArrayList<String> units=new ArrayList<>();
        ArrayList<Double>kilo=new ArrayList<Double>();
        final ArrayList<Integer>gram=new ArrayList<Integer>();
        ArrayList<Double>litre=new ArrayList<Double>();
        ArrayList<Integer>millilitre=new ArrayList<Integer>();
        final ArrayList<Integer>unit=new ArrayList<Integer>();
        //type of quantity
        units.add("kilograms(kg)");
        units.add("grams(g)");
        units.add("litres(l)");
        units.add("millilitres(ml)");
        units.add("no(units)");
        //kilogram
        kilo.add(1.0);kilo.add(1.5);kilo.add(2.0);kilo.add(2.5);kilo.add(3.0);kilo.add(5.0);kilo.add(7.5);kilo.add(10.0);
        kilo.add(15.0);kilo.add(20.0);kilo.add(25.0);
        //grams
        gram.add(250);gram.add(50);gram.add(100);gram.add(200);gram.add(500);gram.add(750);
        //Liters
        litre.add(1.0);litre.add(1.5);litre.add(2.0);litre.add(5.0);litre.add(10.0);litre.add(15.0);
        //mililiters
        millilitre.add(250);millilitre.add(500);millilitre.add(750);
        //untis
        unit.add(1);unit.add(2);unit.add(3);unit.add(4);unit.add(5);unit.add(6);unit.add(7);unit.add(8);unit.add(9);unit.add(10);;unit.add(11);unit.add(12);
        final ArrayAdapter<Double> kiloadapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,kilo);
        final ArrayAdapter<Integer> gramadapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,gram);
        final ArrayAdapter<Double> litreadapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,litre);
        final ArrayAdapter<Integer> millilitreadapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,millilitre);
        final ArrayAdapter<Integer> unitadapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,unit);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,units){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v=super.getDropDownView(position, convertView, parent);
                TextView tv=(TextView)v;
                if(productQuantity.equals("kg")||productQuantity.equals("g")){
                    if(position==2 || position==3 || position==4) {
                        tv.setTextColor(Color.parseColor("#888888"));
                    }
                }
                if(productQuantity.equals("l")||productQuantity.equals("ml")){
                    if(position==0 || position==1 || position==4) {
                        tv.setTextColor(Color.parseColor("#888888"));
                    }
                }
                if(productQuantity.equals("units")){
                    if(position==0 || position==1 || position==2 || position==3) {
                        tv.setTextColor(Color.parseColor("#888888"));
                    }
                }
                return v;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerunits.setAdapter(adapter);
        spinnervalues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                computeprice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerunits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    if((productQuantity.equals("kg") || productQuantity.equals("g")))
                        spinnervalues.setAdapter(kiloadapter);
                    else {
                        if (productQuantity.equals("ml") || productQuantity.equals("l"))
                            spinnerunits.setSelection(2);

                        else
                            spinnerunits.setSelection(4);
                    }
                 }
                if(position==1){
                    if((productQuantity.equals("kg") || productQuantity.equals("g")))
                        spinnervalues.setAdapter(gramadapter);
                    else {
                        if (productQuantity.equals("ml") || productQuantity.equals("l"))
                            spinnerunits.setSelection(3);
                        else
                            spinnerunits.setSelection(4);
                    }
                 }
                if(position==2){
                    if((productQuantity.equals("ml") || productQuantity.equals("l")))
                        spinnervalues.setAdapter(litreadapter);
                    else {
                        if (productQuantity.equals("kg") || productQuantity.equals("g"))
                            spinnerunits.setSelection(0);
                        else
                            spinnerunits.setSelection(4);
                    }
                 }
                if(position==3){
                    if((productQuantity.equals("ml") || productQuantity.equals("l")))
                        spinnervalues.setAdapter(millilitreadapter);
                    else {
                        if (productQuantity.equals("kg") || productQuantity.equals("g"))
                            spinnerunits.setSelection(0);
                        else
                            spinnerunits.setSelection(4);
                    }
                 }
                if(position==4){
                    if(productQuantity.equals("units"))
                        spinnervalues.setAdapter(unitadapter);
                    else {
                        if (productQuantity.equals("kg") || productQuantity.equals("g"))
                            spinnerunits.setSelection(0);
                        else
                            spinnerunits.setSelection(2);
                    }
                 }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void computeprice(){
        double sum=0.0;
        DecimalFormat df = new DecimalFormat("####.");
        if(spinnerunits.getSelectedItemPosition()==0){
            if(productQuantity.equals("kg")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice;
            }
            else if(productQuantity.equals("g")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice*10;
            }
            else{
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if(spinnerunits.getSelectedItemPosition()==1){
            if(productQuantity.equals("g")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice/100;
            }
            else if(productQuantity.equals("kg")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice/1000;
            }
            else{
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if(spinnerunits.getSelectedItemPosition()==2){
            if(productQuantity.equals("l")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice;
            }
            else if(productQuantity.equals("ml")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice*10;
            }
            else{
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else if(spinnerunits.getSelectedItemPosition()==3){
            if(productQuantity.equals("ml")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice/100;
            }
            else if(productQuantity.equals("l")){
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice/1000;
            }
            else{
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            if(productQuantity.equals("units"))
                sum=Double.parseDouble(spinnervalues.getSelectedItem().toString())*productPrice;
            else
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_SHORT).show();
        }
        price.setText("₹"+String.format("%.2f",sum));
        if(offerPercentage != 0.0)
        {
            offerPrice.setVisibility(View.VISIBLE);
            offer.setVisibility(View.VISIBLE);
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            offer.setText(offerPercentage+"% off");
             sum = sum-(sum*(offerPercentage/100));
            offerPrice.setText("₹"+String.format("%.2f",sum));
         }

     }



    private void display(String pCode,String pQunatity,int state) {
        Code= pCode;
         Qunatity = pQunatity;
        datastate = state;
     }

    private void addcart() {
        String v = spinnervalues.getSelectedItem().toString();
        String u = spinnerunits.getSelectedItem().toString();
        u = u.substring(u.indexOf("(")+1,u.lastIndexOf(")"));
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Cart");
        ItemModelClass itemModelClass = new ItemModelClass();
         if(datastate == 0)//empty cart
        {
            itemModelClass.setProductQuantity(v+" "+u);
            itemModelClass.setProductCode(productCode);
        }
        else if(datastate == 1)//non empty cart
        {
            Qunatity = Qunatity+" \n "+v+" "+u;
            Code = Code+" \n "+productCode;
            itemModelClass.setProductQuantity(Qunatity);
            itemModelClass.setProductCode(Code);
        }
        databaseReference.setValue(itemModelClass);
        finish();
    }

    private void loadViews() {
        productImage = (ImageView) findViewById(R.id.detailspageprductImage);
        name = (TextView) findViewById(R.id.detailspageprductName);
        price = (TextView) findViewById(R.id.detailspageprductPrice);
        offerPrice = (TextView) findViewById(R.id.detailspageprductOfferPrice);
        offer = (TextView) findViewById(R.id.detailspageprductOffer);
        productInfo  = (TextView) findViewById(R.id.detailspageprductInfo1);
        spinnervalues = (Spinner) findViewById(R.id.quantitySpinner);
        spinnerunits = (Spinner) findViewById(R.id.unitsSpinner);
        addTocart  = (TextView) findViewById(R.id.cart);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                String pQunatity = itemModelClass.getProductQuantity();
                String pCode = itemModelClass.getProductCode();
                if(pQunatity.isEmpty() && pCode.isEmpty())//empty case ...data in cart
                {
                    display(pCode,pQunatity,0);
                }
                else//not empty case ....no data in cart
                {
                    display(pCode,pQunatity,1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
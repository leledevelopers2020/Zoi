package com.zoiapp.zoi.Mart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.ProductDetails;
import com.zoiapp.zoi.R;
import com.zoiapp.zoi.ModalClasses.UserProfile;
import com.zoiapp.zoi.UserRequired.UserAddress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cart extends AppCompatActivity implements CartAdapter.CartAdapterEvents {

    Intent data;
    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseRef;
    Button order;
    String userID;
    private TextView total,totalPrice;
    private RecyclerView recyclerCartView;
    LinearLayout cartpage;
    FloatingActionButton cartToMart;
    TextView cartToMartText,emptyCart;
    CartAdapter cartAdapter;
    static String[] itemN, itemQ,itemC, itemO,items;
    private List<String> itemCodes;
    private ArrayList<ItemModelClass> arrayListCart;
    private ArrayList<ProductDetails> arrayListCartDetails;
    static String image;
    static  double priceTotal,editItemPrice,editItemOffer;
    static String Name,Quantity,Code;
    static String editItemCode,editItemName,editItemQuantity,editItemImage;
    static int editItemCount;
    boolean retriveState;
    private static String martLocation,productDetails;
    private static String Lang = "English";
    List<String> itemslist;
    ArrayList<ProductDetails> product;
    private  String orderItemName,orderItemQuantity,orderItemPrice,orderItemCount;
    private  int numberOfItemsInCart=0;
    static int cnt;
    int check;
    private ProgressDialog progressDialog;
    private LinearLayout priceDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        retriveState = true;
        loadViews();
        data = getIntent();

        retriveState = data.getBooleanExtra("retriveState",true);

        arrayListCart = new ArrayList<>();
        arrayListCartDetails = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();//authentication
        firebaseDatabase = FirebaseDatabase.getInstance();//realtime database
        userID = firebaseAuth.getCurrentUser().getUid();
        product = new ArrayList<>();

        if(internetConnectivity()) {
            progressDialog = ProgressDialog.show(Cart.this,
                    Html.fromHtml("<font color='#509324'>Plase Wait!!</font>"),
                    "Loading....", true, false);
            emptyCart.setVisibility(View.GONE);
        }
        else
        {
            emptyCart.setVisibility(View.VISIBLE);
        }
        databaseRef = firebaseDatabase.getReference(userID).child("Cart");
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity())
                {

                    //Toast.makeText(Cart.this,"Your order has been placed!!",Toast.LENGTH_LONG).show();
                    List<String> name = Arrays.asList(orderItemName.split("@"));
                    List<String> quantity = Arrays.asList(orderItemQuantity.split("@"));
                    List<String> price = Arrays.asList(orderItemPrice.split("@"));
                    List<String> count = Arrays.asList(orderItemCount.split("@"));
                    String itemNames = "", itemPrice = "", itemQunatityCount = "";
                    for (int i = 0; i < name.size(); i++) {
                        if (i == 0) {
                            itemNames = itemNames + name.get(i);
                            itemPrice = itemPrice + price.get(i);
                            itemQunatityCount = itemQunatityCount + quantity.get(i) + "(" + count.get(i) + ")";
                        } else {
                            itemNames = itemNames + " \n " + name.get(i);
                            itemPrice = itemPrice + " \n " + price.get(i);
                            itemQunatityCount = itemQunatityCount + " \n " + quantity.get(i) + "(" + count.get(i) + ")";

                        }
                        //Log.d("order",itemNames+","+itemPrice+"."+itemQunatityCount);
                        if (i == name.size() - 1) {
                            Intent intent = new Intent(Cart.this, UserAddress.class);
                            intent.putExtra("activity", "Mart");
                            intent.putExtra("orderName", itemNames);
                            intent.putExtra("orderQuantity", itemQunatityCount);
                            intent.putExtra("orderPrice", itemPrice);
                            startActivity(intent);
                        }
                    }
                }
                else
                {
                    // Toast.makeText(Cart.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }

            }


        });
        // getDataFromCart();
        cartToMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internetConnectivity()) {

                    Intent intent = new Intent(Cart.this, ShopHomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();
                    startActivity(intent);

                }
                else {
                    // Toast.makeText(Cart.this,"No Internet Connection",Toast.LENGTH_LONG).show();
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

    //get the mart location
    private void getMartLocation() {
        if (internetConnectivity()) {
            DatabaseReference loc = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Location");
            loc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    martLocation = userProfile.getMartLocation();
                    saveitems(martLocation, 1);
                    getProductDetailsFromLocation();
                    //getDataFromCart();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            //  Toast.makeText(Cart.this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }
    //get product details from mart location
    public void getProductDetailsFromLocation()
    {

        if (internetConnectivity()) {
            if (!martLocation.isEmpty()) {
                DatabaseReference databaseReference1 = firebaseDatabase.getReference("Products").child(martLocation).child("ProductDetails").child(Lang);
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                        String item = itemModelClass.getProductDetails();
                        saveitems(item, 2);
                        items = item.split("\\?\\?\\?");
                        for (int i = 0; i < items.length; i++) {
                            itemslist = Arrays.asList(items[i].split("[@#!%]"));
                            product.add(new ProductDetails(itemslist.get(0), itemslist.get(1), itemslist.get(2), itemslist.get(3), Double.parseDouble(itemslist.get(4))));
                        }
                        getDataFromCart();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(this, "Please select your location my shop and add some data to cart", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    //save location
    private void  saveitems(String data, int i) {
        switch (i){
            case 1:
                martLocation = data;
                break;
            case 2:
                productDetails = data;
        }
    }

    //get data from cart
    private void getDataFromCart() {
        if (internetConnectivity()) {
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ItemModelClass itemModelClass = snapshot.getValue(ItemModelClass.class);
                    assert itemModelClass != null;
                    String pCode = itemModelClass.getProductCode();
                    String pQunatity = itemModelClass.getProductQuantity();
                    if (!pCode.isEmpty() || !pQunatity.isEmpty()) {
                        progressDialog.dismiss();
                        cartpage.setVisibility(View.VISIBLE);
                        saveitems(pCode, pQunatity);
                    }
                    else
                    {
                        progressDialog.dismiss();
                        cartToMart.setVisibility(View.VISIBLE);
                        cartToMartText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    //save cart details
    private void saveitems(String Code,String Quantity)
    {

        Cart.Quantity = Quantity;
        Cart.Code = Code;
        if(!Code.isEmpty()&& !Quantity.isEmpty()) {

            itemCodes = new ArrayList<String>();
            itemCodes = getProductCode(Code);
            itemC = new String[itemCodes.size()];
            itemC = itemCodes.toArray(itemC);
            itemN = getProductName("productname");
            itemQ = getProductName("quantity");
            itemO = getProductName("offers");


            if (retriveState) {
                loadCartAdapter(itemN, itemC, itemQ);
                retriveState = false;
            }
        }
        else {

        }

    }

    private ArrayList<String> getProductCode(String code) {
        String[] codes = code.split(" \n ");
        ArrayList<String> codeAl = new ArrayList<String>();
        int k;
        for(int i=0;i< codes.length;i++)
        {
            for(int j=0;j<product.size();j++)
            {
                k=j;
                if (codes[i].equals(product.get(k).getCode())) {
                    codeAl.add(codes[i]);

                    break;
                }
            }
        }
        return codeAl;
    }

    //get product name from the total product details
    private String[] getProductName(String typeofData) {

        String[] data = new String[itemC.length];
        int k;
        if(typeofData.equals("quantity"))
        {
            int count =0;
            String[] quantity = Quantity.split(" \n ");
            String[] code = Code.split(" \n ");
            for(int i=0;i<code.length;i++) {
                for (int j = 0; j < product.size(); j++) {
                    k = j;
                    if (code[i].equals(product.get(k).getCode())) {
                        data[count] = quantity[i];
                        count++;
                        break;
                    }

                }
            }
            return data;
        }


        for(int i=0;i< itemC.length;i++)
        {
            for(int j=0;j<product.size();j++)
            {
                k=j;
                if(typeofData.equals("productname")) {
                    if (itemC[i].equals(product.get(k).getCode())) {
                        data[i] = product.get(k).getName();
                        break;
                    }
                }
                else if(typeofData.equals("offers"))
                {
                    if (itemC[i].equals(product.get(k).getCode())) {
                        data[i] = ""+(product.get(k).getOffer());
                        break;
                    }
                }
            }
        }
        return  data;
    }

    private String[] arrayCoverter(String data)
    {
        return data.split(" \n ");
    }

    private void  loadCartAdapter(String[] itemN, String[] itemC, String[] itemQ) {
        if (itemC.length != 0 || itemN.length != 0 || itemQ.length != 0) {
            cartpage.setVisibility(View.VISIBLE);
            cartToMart.setVisibility(View.GONE);
            cartToMartText.setVisibility(View.GONE);
            priceTotal = 0.0;
            final List<String> code1 = new ArrayList<String>();
            final List<String> name1 = new ArrayList<String>();
            final List<String> quant1 = new ArrayList<String>();
            final List<String> offers1 = new ArrayList<String>();
            final List<Integer> itemCount = new ArrayList<Integer>();
            int l = -1;

            for (int i = 0; i < itemC.length; i++) {
                 String str = itemC[i];
                String qunt = itemQ[i];
                boolean state = true;
                for (int j = 0; j < code1.size(); j++) {
                    if (str.equals(code1.get(j)) && qunt.equals(quant1.get(j))) {
                        state = false;
                        break;
                    }
                }
                if (state) {
                    int cnt = 0;
                    l++;
                    for (int j = 0; j < itemC.length; j++) {
                        if (str.equals(itemC[j]) && qunt.equals(itemQ[j])) {
                            cnt++;
                        }
                    }
                    code1.add(l,str);
                    name1.add(l,itemN[i]);
                    quant1.add(l,itemQ[i]);
                    offers1.add(l,itemO[i]);
                    itemCount.add(cnt);

                }
             }
            arrayListCart.clear();
            arrayListCartDetails.clear();

            final ArrayList<String> orginalQuantity = new ArrayList<>();
            for (int i = 0; i < code1.size(); i++) {
                for (int j = 0; j < product.size(); j++) {
                    if (code1.get(i).equals(product.get(j).getCode())) {
                        orginalQuantity.add(product.get(j).getQunt());
                        break;
                    }
                }
            }
            if (internetConnectivity())
            {
                orderItemName="";orderItemQuantity="";orderItemPrice="";orderItemCount="";
                check = 0;
                for(int i=0;i<code1.size();i++)
                {

                    final int count = itemCount.get(i);
                    final int finalI = i;
                    DatabaseReference databaseReference1 = firebaseDatabase.getReference("Products").child(martLocation).child("images");
                    Query query = databaseReference1.child(code1.get(i));
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String itemprice;

                            try {
                                image = snapshot.child("imageUrl").getValue().toString();
                                itemprice = snapshot.child("price").getValue().toString();
                            }
                            catch (Exception e)
                            {
                                return;
                            }
                            if(orginalQuantity.get(finalI).toLowerCase().equals("kg") || orginalQuantity.get(finalI).toLowerCase().equals("g")
                                    || orginalQuantity.get(finalI).toLowerCase().equals("ml") || orginalQuantity.get(finalI).toLowerCase().equals("l")
                                    || orginalQuantity.get(finalI).toLowerCase().equals("units"))
                            {
                                double price = Double.parseDouble(itemprice);
                                double offer = Double.parseDouble(offers1.get(finalI));
                                double p = calculatePrice(price, quant1.get(finalI));
                                arrayListCartDetails.add(new ProductDetails(price,orginalQuantity.get(finalI).toLowerCase() ));
                                double p1 = p;
                                if (offer != 0.0) {
                                    p = p - (p * (offer / 100));
                                }

                                if (check == 0) {
                                    orderItemName = orderItemName + name1.get(finalI);
                                    orderItemQuantity = orderItemQuantity + quant1.get(finalI);
                                    orderItemPrice = orderItemPrice + p * count;
                                    orderItemCount = orderItemCount+count;
                                } else {

                                    orderItemName = orderItemName +"@"+ name1.get(finalI) ;
                                    orderItemQuantity = orderItemQuantity +"@"+ quant1.get(finalI);
                                    orderItemPrice = orderItemPrice  +"@"+ p * count;
                                    orderItemCount = orderItemCount +"@"+ count ;
                                }
                                priceTotal = priceTotal + p * count;
                                totalPrice.setText("₹" + String.format("%.2f", priceTotal));
                                arrayListCart.add(new ItemModelClass(code1.get(finalI), name1.get(finalI), quant1.get(finalI).toLowerCase(), p1, count, image, offer));
                                numberOfItemsInCart++;
                                bulidRecyclerView();
                                check++;

                                if(priceTotal<=0.0)
                                {
                                    cartpage.setVisibility(View.INVISIBLE);
                                    cartToMart.setVisibility(View.VISIBLE);
                                    cartToMartText.setVisibility(View.VISIBLE);
                                    order.setVisibility(View.INVISIBLE);
                                    priceDetails.setVisibility(View.INVISIBLE);
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            cartpage.setVisibility(View.GONE);
            cartToMart.setVisibility(View.VISIBLE);
            cartToMartText.setVisibility(View.VISIBLE);
            order.setVisibility(View.INVISIBLE);
            priceDetails.setVisibility(View.INVISIBLE);
        }

    }

    private double calculatePrice(double price, String s) {
        String[] quant = s.split(" ");//2.5 kg
        double p1 = Double.parseDouble(quant[0]);//2.5
        //price 50
        switch (quant[1])
        {
            case "kg":
                p1 = price*p1;
                break;
            case "g":
                p1 = (price/1000)*p1;
                break;
            case "l":
                p1 = price*p1;
                break;
            case "ml":
                p1 = (price/1000)*p1;
                break;
            case "units":
                p1 = price*p1;
                break;
        }

        return p1;
    }


    private void bulidRecyclerView() {
        recyclerCartView.setHasFixedSize(true);
        recyclerCartView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(arrayListCart,arrayListCartDetails,getApplicationContext(),this);
        recyclerCartView.setAdapter(cartAdapter);
        ((SimpleItemAnimator) recyclerCartView.getItemAnimator()).setSupportsChangeAnimations(false);
    }
    private void loadViews() {
        cartpage = (LinearLayout) findViewById(R.id.cartpage);
        cartToMart = (FloatingActionButton) findViewById(R.id.cartTomart);
        cartToMartText = (TextView) findViewById(R.id.cartTomarttext);
        recyclerCartView = (RecyclerView) findViewById(R.id.recyclerviewCart);
        total = (TextView) findViewById(R.id.total);
        totalPrice = (TextView) findViewById(R.id.cartTotalPrice);
        order = (Button) findViewById(R.id.orderButtonCart);
        emptyCart = (TextView) findViewById(R.id.cartEmpty);
        priceDetails = (LinearLayout) findViewById(R.id.priceDetails);
    }

   
    @Override
    public void onPlusClicked(ItemModelClass itemModelClass, int position) {
        editItemQuantity="";
        editItemCode = itemModelClass.getProductCode();
        editItemName = itemModelClass.getProductName();
        editItemQuantity = itemModelClass.getProductQuantity();
        editItemOffer = itemModelClass.getOffer();
        editItemPrice = itemModelClass.getPrice();
        editItemCount = itemModelClass.getProductCount();
        editItemCount++;
        editItemImage = itemModelClass.getImageUrl();
        //  System.out.println(">>>>>>>>>>price<<<<<<<<<<<,"+editItemPrice);
        if(editItemOffer != 0.0)
        {
            editItemPrice = editItemPrice-(editItemPrice*(editItemOffer/100));
        }
        priceTotal = priceTotal+editItemPrice;
        Quantity = Quantity +" \n "+editItemQuantity;
        Code = Code +" \n "+editItemCode;
        updateOrderList(editItemName,editItemQuantity,editItemPrice,itemModelClass.getProductCount(),0);
        arrayListCart.set(position, new ItemModelClass(editItemCode,editItemName, editItemQuantity, itemModelClass.getPrice(),editItemCount, editItemImage, editItemOffer));
        cartAdapter.notifyItemChanged(position);
        itemModelClass.setProductCode(Code);
        itemModelClass.setProductQuantity(Quantity);
        databaseRef.setValue(itemModelClass);
        totalPrice.setText("₹"+String.format("%.2f",priceTotal));


    }

    @Override
    public void onMinusClicked(ItemModelClass itemModelClass, int position) {
        editItemCode = itemModelClass.getProductCode();
        editItemName = itemModelClass.getProductName();
        editItemQuantity = itemModelClass.getProductQuantity();
        editItemOffer = itemModelClass.getOffer();
        editItemPrice = itemModelClass.getPrice();
        if(editItemOffer != 0.0)
        {
            editItemPrice = editItemPrice-(editItemPrice*(editItemOffer/100));
        }
        editItemCount = itemModelClass.getProductCount();
        editItemCount--;
        editItemImage = itemModelClass.getImageUrl();
        String[] itemQR = arrayCoverter(Quantity);
        String[] itemCR = arrayCoverter(Code);
        updateOrderList(editItemName,editItemQuantity,editItemPrice,itemModelClass.getProductCount(),1);
        List<String> q = new ArrayList<String>();
        List<String> c = new ArrayList<String>();
        for(int i=0;i<itemCR.length;i++)
        {
            c.add(itemCR[i]);
            q.add(itemQR[i]);
        }
        String updatedCode = "";String updatedQuantity = "";
        boolean s = false;
        for(int i = 0;i< c.size();i++)
        {
            if(c.get(i).equals(editItemCode) && q.get(i).equals(editItemQuantity) && s== false)
            {
                s = true;
                c.remove(i);q.remove(i);
                break;
            }
        }

        for(int j =0;j<c.size();j++)
        {
            if(!c.get(j).isEmpty() && !q.get(j).isEmpty())
            {
                if(j == 0)
                {
                    updatedCode = c.get(j);
                    updatedQuantity = q.get(j);
                }
                else
                {
                    updatedCode = updatedCode +" \n "+  c.get(j);
                    updatedQuantity =updatedQuantity +" \n "+ q.get(j);

                }
            }
        }

        if(editItemCount>0) {
            arrayListCart.set(position, new ItemModelClass(editItemCode,editItemName, editItemQuantity, itemModelClass.getPrice(),editItemCount, editItemImage, editItemOffer));
            cartAdapter.notifyItemChanged(position);
        }
        else if(editItemCount == 0)
        {
            numberOfItemsInCart--;
            arrayListCart.remove(position);
            cartAdapter.notifyItemRemoved(position);
        }
        itemModelClass.setProductCode(updatedCode);
        itemModelClass.setProductQuantity(updatedQuantity);
        databaseRef.setValue(itemModelClass);
        priceTotal = priceTotal -editItemPrice;
        totalPrice.setText("₹"+String.format("%.2f",priceTotal));
         if(priceTotal<=0.0)
        {
            cartpage.setVisibility(View.INVISIBLE);
            cartToMart.setVisibility(View.VISIBLE);
            cartToMartText.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public void onDeleteClicked(ItemModelClass itemModelClass,int position) {
        if (internetConnectivity()) {
            editItemCode = itemModelClass.getProductCode();
            editItemName = itemModelClass.getProductName();
            editItemQuantity = itemModelClass.getProductQuantity();
            editItemOffer = itemModelClass.getOffer();
            editItemPrice = itemModelClass.getPrice();
             if (editItemOffer != 0.0) {
                editItemPrice = editItemPrice - (editItemPrice * (editItemOffer / 100));
            }
            editItemCount = itemModelClass.getProductCount();
            editItemImage = itemModelClass.getImageUrl();
            String[] itemCR = arrayCoverter(Code);
            String[] itemQR = arrayCoverter(Quantity);
            updateOrderList(editItemName, editItemQuantity, editItemPrice,itemModelClass.getProductCount(),2);
             List<String> q = new ArrayList<String>();
            List<String> c = new ArrayList<String>();
            String updatedCode = "";
            String updatedQuantity = "";
            int itemCount = 0;
            for (int i = 0; i < itemCR.length; i++) {
                if (itemCR[i].equals(editItemCode) && itemQR[i].equals(editItemQuantity)) {
                    itemCount++;
                    continue;
                }
                q.add(itemQR[i]);
                c.add(itemCR[i]);
            }
             for (int j = 0; j < c.size(); j++) {
                if (j == 0) {
                    updatedCode = c.get(j);
                    updatedQuantity = q.get(j);
                } else {
                    updatedCode = updatedCode + " \n " + c.get(j);
                    updatedQuantity = updatedQuantity + " \n " + q.get(j);
                }
            }
            numberOfItemsInCart--;

            arrayListCart.remove(position);
            cartAdapter.notifyItemRemoved(position);
            itemModelClass.setProductQuantity(updatedQuantity);
            itemModelClass.setProductCode(updatedCode);
            databaseRef.setValue(itemModelClass);
            priceTotal = priceTotal - (editItemPrice * itemCount);
            totalPrice.setText("₹" + String.format("%.2f", priceTotal));


            if (priceTotal <= 0.0) {
                cartpage.setVisibility(View.INVISIBLE);
                cartToMart.setVisibility(View.VISIBLE);
                cartToMartText.setVisibility(View.VISIBLE);
            }
        }
        else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    //updata order data when plus or minus or delete clicked
    private void updateOrderList(String editItemName, String editItemQuantity, double editItemPrice,int itemCount ,int st) {
        String[] orderN = orderItemName.split("@");
        String[] orderQ = orderItemQuantity.split("@");
        String[] orderP = orderItemPrice.split("@");
        String[] orderC = orderItemCount.split("@");//count
        double[] price = new double[orderP.length];
        int[] count = new int[orderC.length];
        double oldPrice;
        int oldCount;
        //converting string type price, count into double and int
        for(int i=0;i<orderP.length;i++)
        {
            oldPrice = Double.parseDouble(orderP[i]);
            price[i] = oldPrice;
            oldCount = Integer.parseInt(orderC[i]);
            count[i] = oldCount;
        }
        List<String> oN = new ArrayList<>();
        List<String> oQ = new ArrayList<>();
        List<Double> orderPrice = new ArrayList<>();
        List<Integer> orderCount = new ArrayList<>();
        orderItemName = "";orderItemQuantity = "";orderItemPrice = "";orderItemCount = "";
        //adding data into 3 different arrays
        for(int i=0;i<orderN.length;i++)
        {
            if(st == 1 || st == 0)
            {
                oN.add(orderN[i]);orderPrice.add(price[i]);oQ.add(orderQ[i]);orderCount.add(count[i]);
            }
            else if (st == 2)
            {
                if(orderN[i].equals(editItemName) && orderQ[i].equals(editItemQuantity))
                {
                    continue;
                }
                oN.add(orderN[i]);orderPrice.add(price[i]);oQ.add(orderQ[i]);orderCount.add(count[i]);
            }
        }

        //performing adding or minus or delets of particluar data (Editing data according to operation)
        boolean state = false;
        for (int i = 0; i < oN.size(); i++) {
            if(st ==1) {
                if (oN.get(i).equals(editItemName) && oQ.get(i).equals(editItemQuantity) &&
                        orderPrice.get(i) - editItemPrice >= 0 && orderCount.get(i).equals(itemCount) && state == false) {

                    state = true;
                    itemCount--;
                    if (orderPrice.get(i) - editItemPrice == 0.0) {
                        oN.remove(i);
                        oQ.remove(i);
                        orderPrice.remove(i);
                        orderCount.remove(i);
                    }
                    else {
                        orderPrice.set(i, orderPrice.get(i) - editItemPrice);
                        orderCount.set(i, itemCount);
                    }
                    break;
                }
            }
            else if(st == 0)
            {
                if (oN.get(i).equals(editItemName) && oQ.get(i).equals(editItemQuantity)  &&
                        orderCount.get(i).equals(itemCount) && state == false) {

                    state = true;
                    itemCount++;
                    orderPrice.set(i, orderPrice.get(i)+editItemPrice);
                    orderCount.set(i, itemCount);
                    break;
                }
            }
        }
        //coverting list data into string with concatnation
        for(int j =0;j<oN.size();j++)
        {
            if(!oN.get(j).isEmpty() && !oQ.get(j).isEmpty() && !orderCount.isEmpty())
            {
                if(j == 0)
                {
                    orderItemName = oN.get(j);
                    orderItemPrice = ""+orderPrice.get(j);
                    orderItemQuantity = oQ.get(j);
                    orderItemCount = ""+orderCount.get(j);
                }
                else
                {
                    orderItemName = orderItemName +"@"+ oN.get(j);
                    orderItemPrice = orderItemPrice +"@"+ orderPrice.get(j);
                    orderItemQuantity = orderItemQuantity +"@"+ oQ.get(j);
                    orderItemCount = orderItemCount + "@"+ orderCount.get(j);
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getMartLocation();
        retriveState = true;
    }
}
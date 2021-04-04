package com.zoiapp.zoi.Mart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.ModalClasses.ProductDetails;
import com.zoiapp.zoi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class  CartAdapter extends RecyclerView.Adapter<CartAdapter.CardHolder> {

    private static ArrayList<ItemModelClass> lsit;
    private ArrayList<ProductDetails> itemDetails;
    private CartAdapterEvents cartAdapterEvents;
    private Context context;
    private long mLastClickTime = System.currentTimeMillis();
    private long mLastClickTimePlus = System.currentTimeMillis();
    private long mLastClickTimeMinus = System.currentTimeMillis();
    private long mLastClickTimeDelete = System.currentTimeMillis();
    private long mLastClickTimeItem = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 500;

    public CartAdapter() {
    }

    public CartAdapter(ArrayList<ItemModelClass> mlist, Context context, CartAdapterEvents cartAdapterEvents) {
        lsit = mlist;
        this.context = context;
        this.cartAdapterEvents = cartAdapterEvents;
    }

    public CartAdapter(ArrayList<ItemModelClass> list, ArrayList<ProductDetails> itemDetails, Context context, CartAdapterEvents cartAdapterEvents) {
        lsit = list;
        this.itemDetails = itemDetails;
        this.cartAdapterEvents = cartAdapterEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartview, parent, false);
        return new CardHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, final int position) {
        final ItemModelClass currentItem = lsit.get(position);
        final ProductDetails currentItemDetails = itemDetails.get(position);
        Picasso.get().load(currentItem.getImageUrl()).into(holder.productImage);
        holder.productName.setText(currentItem.getProductName());
        holder.productQuantity.setText(currentItem.getProductQuantity());
        double offer = currentItem.getOffer();
        double price = currentItem.getPrice();
        holder.productPrice.setText("₹" + String.format("%.2f", price));
        holder.productCount.setText(String.valueOf(currentItem.getProductCount()));
        if (offer != 0.0) {
            holder.productOfferPrice.setVisibility(View.VISIBLE);
            holder.productOffer.setVisibility(View.VISIBLE);
            holder.productOffer.setText(offer + "% off");
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            price = price - (price * (offer / 100));
            holder.productOfferPrice.setText("₹" + String.format("%.2f", price));

        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                Intent data = new Intent(v.getContext(), ShoppingItemDetails.class);
                data.putExtra("productCode", currentItem.getProductCode());
                data.putExtra("image", currentItem.getImageUrl());
                data.putExtra("productName", currentItem.getProductName());
                if (currentItemDetails.getQunt().equals("kg") || currentItemDetails.getQunt().equals("l") || currentItemDetails.getQunt().equals("units"))
                    data.putExtra("productPrice", currentItemDetails.getPrice());
                else if (currentItemDetails.getQunt().equals("g") || currentItemDetails.getQunt().equals("ml"))
                    data.putExtra("productPrice", (currentItemDetails.getPrice() / 1000) * 100);
                data.putExtra("productQunatity", currentItemDetails.getQunt());
                String productInfo;
                switch (currentItemDetails.getQunt()) {
                    case "kg":
                        productInfo = "1" + currentItemDetails.getQunt();
                        break;
                    case "g":
                        productInfo = "100" + currentItemDetails.getQunt();
                        break;
                    case "l":
                        productInfo = "1" + currentItemDetails.getQunt();
                        break;
                    case "ml":
                        productInfo = "100" + currentItemDetails.getQunt();
                        break;
                    case "units":
                        productInfo = "1" + currentItemDetails.getQunt();
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + currentItemDetails.getQunt());
                }
                data.putExtra("productInfo", productInfo);
                data.putExtra("offerPercetage", currentItem.getOffer());
                v.getContext().startActivity(data);
            }
        });
    }


    @Override
    public int getItemCount() {
        return lsit.size();
    }

    public class CardHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName, productQuantity, productPrice, productCount, productOfferPrice, productOffer;
        private Button delete, plus, minus;
        View view;

        public CardHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImageCart);
            productName = itemView.findViewById(R.id.pNameCart);
            productQuantity = itemView.findViewById(R.id.pQuantityCart);
            productPrice = itemView.findViewById(R.id.pPriceCart);
            productOfferPrice = itemView.findViewById(R.id.offerPriceCart);
            productOffer = itemView.findViewById(R.id.offerCart);
            productCount = itemView.findViewById(R.id.pCountCart);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            delete = itemView.findViewById(R.id.deleteitem);
            view = itemView;
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTimeItem < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTimeItem = now;
                    int position = getAdapterPosition();
                    cartAdapterEvents.onPlusClicked(lsit.get(position), position);
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTimeItem < 1000) {
                        return;
                    }
                    mLastClickTimeItem = now;
                    int position = getAdapterPosition();
                    cartAdapterEvents.onMinusClicked(lsit.get(position), position);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTimeItem < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTimeItem = now;
                    int position = getAdapterPosition();
                    cartAdapterEvents.onDeleteClicked(lsit.get(position), position);
                }
            });
        }
    }

    public interface CartAdapterEvents {
        void onPlusClicked(ItemModelClass itemModelClass, int position);

        void onMinusClicked(ItemModelClass itemModelClass, int position);

        void onDeleteClicked(ItemModelClass itemModelClass, int position);
    }
}
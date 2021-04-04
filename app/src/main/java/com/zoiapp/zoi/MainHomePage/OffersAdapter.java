package com.zoiapp.zoi.MainHomePage;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.Mart.ShoppingItemDetails;
import com.zoiapp.zoi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OddersAdapterHolder>{

    public  ArrayList<ItemModelClass> mList = new ArrayList<>();
    public boolean isShimmerOffers = true;
    int shimmerNum = 4;
    private long mLastClickTimeOfferList = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 500;


    public OffersAdapter(ArrayList<ItemModelClass> offersList) {
        mList.clear();
        mList = offersList;
    }

    @NonNull
    @Override
    public OddersAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_view,parent,false);
        return new OddersAdapterHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull OddersAdapterHolder holder, int position) {
        if(isShimmerOffers)
        {
            holder.shimmerFrameLayout.startShimmer();
        }
        else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);
            holder.imageView.setBackground(null);
            holder.offerPrice.setBackground(null);
            holder.productName.setBackground(null);
            final ItemModelClass currentItem = mList.get(position);
            Picasso.get().load(currentItem.getImageUrl()).into(holder.imageView);
            holder.productName.setText(currentItem.getProductName());
            holder.offerPrice.setText(currentItem.getOffer() + "% off");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTimeOfferList < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTimeOfferList = now;
                    Intent data = new Intent(v.getContext(), ShoppingItemDetails.class);
                    data.putExtra("productCode", currentItem.getProductCode());
                    data.putExtra("image", currentItem.getImageUrl());
                    data.putExtra("productName", currentItem.getProductName());
                    if (currentItem.getProductQuantity().equals("kg") || currentItem.getProductQuantity().equals("l") || currentItem.getProductQuantity().equals("units")) {
                        data.putExtra("productPrice", currentItem.getPrice());
                        data.putExtra("productInfo", "1" + currentItem.getProductQuantity());
                    } else if (currentItem.getProductQuantity().equals("g") || currentItem.getProductQuantity().equals("ml")) {
                        data.putExtra("productPrice", (currentItem.getPrice() / 1000) * 100);
                        data.putExtra("productInfo", "100" + currentItem.getProductQuantity());
                    }
                    data.putExtra("productQunatity", currentItem.getProductQuantity());
                    data.putExtra("offerPercetage", currentItem.getOffer());
                    v.getContext().startActivity(data);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return isShimmerOffers?shimmerNum:mList.size();
    }

    public class OddersAdapterHolder extends RecyclerView.ViewHolder
    {
        ShimmerFrameLayout shimmerFrameLayout;
        ImageView imageView;
        TextView productName,offerPrice;
        View view;
        public OddersAdapterHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerOfferPage);
            imageView = itemView.findViewById(R.id.offerViewImage);
            productName = itemView.findViewById(R.id.offerProductName);
            offerPrice = itemView.findViewById(R.id.offerPercentage);
            view = itemView;
        }
    }
}

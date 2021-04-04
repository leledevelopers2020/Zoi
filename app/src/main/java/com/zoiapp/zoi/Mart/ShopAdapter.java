package com.zoiapp.zoi.Mart;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> implements Filterable {

    public static ArrayList<ItemModelClass> mList;
    public static ArrayList<ItemModelClass> mListfull;
    public boolean isShimmerVerticalPage = true;
    int shimmerNum2 = 6;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    public ShopAdapter(ArrayList<ItemModelClass> mlist) {
        this.mList = mlist;
        mListfull=new ArrayList<>(mList);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppinglist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(isShimmerVerticalPage)
        {
            holder.shimmerFrameLayoutVertical.startShimmer();
        }
        else {
            holder.shimmerFrameLayoutVertical.stopShimmer();
            holder.shimmerFrameLayoutVertical.setShimmer(null);
            holder.imageView.setBackground(null);
            /*holder.productName.setBackground(null);
            holder.productQuantity.setBackground(null);
            holder.productPrice.setBackground(null);*/
            holder.fisrtProductInfo.setBackground(null);
            holder.secondProductInfo.setBackground(null);

            final ItemModelClass currentItem = mList.get(position);
            Picasso.get().load(currentItem.getImageUrl()).into(holder.imageView);
            holder.productName.setText(currentItem.getProductName());
            //holder.productName.setPaintFlags(holder.productName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            System.out.println(currentItem.getProductName() + "Offer: " + currentItem.getOffer());
            String productInfo;
            double price;
            switch (currentItem.getProductQuantity()) {
                case "kg":
                    productInfo = "1" + currentItem.getProductQuantity();
                    holder.productQuantity.setText(productInfo);
                    if (currentItem.getOffer() == 0.0) {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice())));
                    } else {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice())));
                        setPrice(holder, currentItem, currentItem.getPrice(), currentItem.getOffer());
                        //holder.productPrice.setText("₹" +price);
                    }

                    break;
                case "g":
                    productInfo = "100" + currentItem.getProductQuantity();
                    holder.productQuantity.setText(productInfo);
                    if (currentItem.getOffer() == 0.0) {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice() / 1000) * 100));
                    } else {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice() / 1000) * 100));
                        setPrice(holder, currentItem, (currentItem.getPrice() / 1000) * 100, currentItem.getOffer());
                        // holder.productPrice.setText("₹" +price);
                    }
                    //setPrice(holder,currentItem,currentItem.getPrice(),currentItem.getOffer());
                    //holder.productPrice.setText("₹"+(price/1000)*100);
                    break;
                case "l":
                    productInfo = "1" + currentItem.getProductQuantity();
                    holder.productQuantity.setText(productInfo);
                    if (currentItem.getOffer() == 0.0) {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice())));
                    } else {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice())));
                        setPrice(holder, currentItem, currentItem.getPrice(), currentItem.getOffer());
                        //holder.productPrice.setText("₹" +price);
                    }
                    //price = setPrice(holder,currentItem,currentItem.getPrice(),currentItem.getOffer());
                    //holder.productPrice.setText("₹"+price);
                    break;
                case "ml":
                    productInfo = "100" + currentItem.getProductQuantity();
                    holder.productQuantity.setText(productInfo);
                    if (currentItem.getOffer() == 0.0) {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice() / 1000) * 100));
                    } else {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice() / 1000) * 100));
                        setPrice(holder, currentItem, (currentItem.getPrice() / 1000) * 100, currentItem.getOffer());
                        // holder.productPrice.setText("₹" +price);
                    }
                    //price = setPrice(holder,currentItem,currentItem.getPrice(),currentItem.getOffer());
                    // holder.productPrice.setText("₹"+(price/1000)*100);
                    break;
                case "units":
                    productInfo = "1" + currentItem.getProductQuantity();
                    holder.productQuantity.setText(productInfo);
                    if (currentItem.getOffer() == 0.0) {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice())));
                    } else {
                        holder.productPrice.setText(String.format("%.2f", (currentItem.getPrice())));
                        setPrice(holder, currentItem, currentItem.getPrice(), currentItem.getOffer());
                        //holder.productPrice.setText("₹" +price);
                    }
                    //  price = setPrice(holder,currentItem,currentItem.getPrice(),currentItem.getOffer());
                    // holder.productPrice.setText("₹"+price);
                    break;

                default:
                    //try {
                        throw new IllegalStateException("Unexpected value: " + currentItem.getProductQuantity());
                    /*} catch (Exception e) {
                        e.printStackTrace();
                    }*/
            } //holder.productQuantity.setText("1"+currentItem.getProductQuantity());

            final String finalProductInfo = productInfo;
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
                    if (currentItem.getProductQuantity().equals("kg") || currentItem.getProductQuantity().equals("l") || currentItem.getProductQuantity().equals("units"))
                        data.putExtra("productPrice", currentItem.getPrice());
                    else if (currentItem.getProductQuantity().equals("g") || currentItem.getProductQuantity().equals("ml"))
                        data.putExtra("productPrice", (currentItem.getPrice() / 1000) * 100);
                    data.putExtra("productQunatity", currentItem.getProductQuantity());
                    data.putExtra("productInfo", finalProductInfo);
                    data.putExtra("offerPercetage", currentItem.getOffer());
                    v.getContext().startActivity(data);
                }
            });
        }
    }
    private void  setPrice(ViewHolder holder, ItemModelClass currentItem, double price, double offer) {
        holder.productOfferPrice.setVisibility(View.VISIBLE);
        holder.productOffer.setVisibility(View.VISIBLE);
        holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        price = price-(price*(currentItem.getOffer()/100));
        holder.productOfferPrice.setText("₹"+String.format("%.2f",price));
        holder.productOffer.setText(currentItem.getOffer()+"%");
    }

    @Override
    public int getItemCount() {
        return isShimmerVerticalPage?shimmerNum2:mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ShimmerFrameLayout shimmerFrameLayoutVertical;
        private ImageView imageView;
        private TextView productName,productQuantity,productPrice,productOfferPrice,productOffer;
        private LinearLayout fisrtProductInfo, secondProductInfo;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayoutVertical = itemView.findViewById(R.id.shimmerVerticalPage);
            imageView = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQunatity);
            productOfferPrice = itemView.findViewById(R.id.productOfferPrice);
            productOffer = itemView.findViewById(R.id.productOffer);
            fisrtProductInfo = itemView.findViewById(R.id.fisrtProductInfo);
            secondProductInfo = itemView.findViewById(R.id.secondProductInfo);
            view = itemView;
        }
    }
    @Override
    public Filter getFilter() {
        return mListFilter;
    }
    private Filter mListFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemModelClass> filteredList=new ArrayList<>();
            if(constraint==null || constraint.length()==0)
            {
                filteredList.addAll(mListfull);
            }
            else
            {
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(ItemModelClass item:mListfull)
                {
                    if(item.getProductName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mList.clear();
            mList.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };




}
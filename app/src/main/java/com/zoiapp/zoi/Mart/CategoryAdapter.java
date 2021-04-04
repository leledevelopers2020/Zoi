package com.zoiapp.zoi.Mart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoiapp.zoi.ModalClasses.ItemModelClass;
import com.zoiapp.zoi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{

    private ArrayList<ItemModelClass> mArrayList;
    private CategoryAdapterEvent categoryAdapterEvent;
    public boolean isShimmerHorzontalPage = true;
    int shimmerNum1 = 6;

    public CategoryAdapter(ArrayList<ItemModelClass> mArrayList) {
        this.mArrayList = mArrayList;
    }

    public CategoryAdapter(ArrayList<ItemModelClass> mArrayList, CategoryAdapterEvent categoryAdapterEvent) {
        this.mArrayList = mArrayList;
        this.categoryAdapterEvent = categoryAdapterEvent;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_horizontal,parent,false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        if(isShimmerHorzontalPage)
        {
            holder.shimmerFrameLayoutHorzontal.startShimmer();
        }
        else {
            holder.shimmerFrameLayoutHorzontal.stopShimmer();
            holder.shimmerFrameLayoutHorzontal.setShimmer(null);
            holder.imageView.setBackground(null);
            final ItemModelClass currentItem = mArrayList.get(position);
            Picasso.get().load(currentItem.getImageUrl()).into(holder.imageView);
            holder.categoryName.setText(currentItem.getCategoryName());
        }
    }

    @Override
    public int getItemCount() {
        return isShimmerHorzontalPage ?shimmerNum1:mArrayList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder{
        private ShimmerFrameLayout shimmerFrameLayoutHorzontal;
        CircleImageView imageView;
        TextView categoryName;
        View v;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayoutHorzontal = itemView.findViewById(R.id.shimmerHorizontalPage);
            imageView = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.category_name);
            v = itemView;
            if(!isShimmerHorzontalPage) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoryAdapterEvent.onClickProduct(mArrayList.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
    public interface CategoryAdapterEvent
    {
        void onClickProduct(ItemModelClass itemModelClass);
    }
}

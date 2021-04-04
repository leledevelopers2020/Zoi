package com.zoiapp.zoi.MainHomePage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.zoiapp.zoi.Mart.ShopHomePage;
import com.zoiapp.zoi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> categorycode;
    ArrayList<String> categories;
    ArrayList<String> images;
    public boolean isShimmerSlider = true;
    int shimmerNum = 1;
    private long mLastClickTimeSliderImage = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 500;



    public SliderAdapter(Context context,ArrayList<String> categories,ArrayList<String> categorycode,ArrayList<String> images){
        this.context=context;
        this.categories=categories;
        this.categorycode=categorycode;
        this.images=images;
    }

    public void addPages() {
          notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return isShimmerSlider?shimmerNum:categories.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==(RelativeLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_category,container,false);
        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.shimmerSliderPage);
        ImageView category_image=view.findViewById(R.id.category_image);
        final TextView category_text=view.findViewById(R.id.category_text);
        if(isShimmerSlider)
        {
            shimmerFrameLayout.startShimmer();
        }
        else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setShimmer(null);
            category_image.setBackground(null);
            category_text.setBackground(null);
            Picasso.get().load(images.get(position)).into(category_image);
            category_text.setText(categories.get(position));
            container.addView(view);
            category_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTimeSliderImage < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTimeSliderImage = now;
                    Intent intent = new Intent(v.getContext(), ShopHomePage.class);
                    intent.putExtra("Category", categorycode.get(position));
                    context.startActivity(intent);
                }
            });
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }


}
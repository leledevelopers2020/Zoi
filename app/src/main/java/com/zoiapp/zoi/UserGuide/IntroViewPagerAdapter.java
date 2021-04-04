package com.zoiapp.zoi.UserGuide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.zoiapp.zoi.R;
import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {
    Context mcontext;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mcontext, List<ScreenItem> mListScreen) {
        this.mcontext = mcontext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen= layoutInflater.inflate(R.layout.layout_screen,null);

        ImageView img=layoutScreen.findViewById(R.id.img);
        TextView title=layoutScreen.findViewById(R.id.title);
        TextView description=layoutScreen.findViewById(R.id.description);

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        img.setImageResource(mListScreen.get(position).getImg());

        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
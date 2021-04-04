package com.zoiapp.zoi.UserGuide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.zoiapp.zoi.MainHomePage.MainActivity;
import com.zoiapp.zoi.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Guide extends AppCompatActivity {

    private ViewPager screenpager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tab_indicator;
    Button btnnext,btnprev,getstarted,sound;
    int position, media_Length;
    Animation btnAnim;
    MediaPlayer player;
    boolean soundon=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        tab_indicator=findViewById(R.id.tab_indicator);
        btnnext=findViewById(R.id.btnnext);
        btnprev=findViewById(R.id.btnprev);
        sound=findViewById(R.id.sound);
        getstarted=findViewById(R.id.getstarted);
        btnAnim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_get_started_animation);
        final List<ScreenItem> mList=new ArrayList<>();
        mList.add(new ScreenItem(""," Easy ways to shop with us"
                ,R.mipmap.appicon1_round));
        mList.add(new ScreenItem("Camera","Capture images with your mobile camera and submit your order (Note: Image should contain product name and quantity)"
                ,R.drawable.camera_icon60));
        mList.add(new ScreenItem("Phonecall","Submit a request for a phone call and wait for a response from our Customer representative"
                ,R.drawable.phone_icon60));
        mList.add(new ScreenItem("Mart","Add products to your cart based on your current location"
                ,R.drawable.basket_icon64));
        mList.add(new ScreenItem("Gallery","Select images from your phone memory and submit a order (Note: Image should contain product name and quantity)"
                ,R.drawable.gallery_icon60));
        mList.add(new ScreenItem("Notes","Make a note of all your products and their quantities and you can place the order according to your willingness "
                ,R.drawable.note_icon60));
        mList.add(new ScreenItem("Cart","Contains all your products added to cart by using mart,use cart to place order of items selected from mart"
                ,R.drawable.cart_icon10));
        mList.add(new ScreenItem("History","Contains all your history of orders placed with their details and even a return facility after the delivery of your order"
                ,R.drawable.history_symbol));
        mList.add(new ScreenItem("Summary","Shows the final bill of your last order only"
                ,R.drawable.summary_symbol));
        screenpager=findViewById(R.id.screenpager);
        introViewPagerAdapter=new IntroViewPagerAdapter(this,mList);
        screenpager.setAdapter(introViewPagerAdapter);
        tab_indicator.setupWithViewPager(screenpager);
        tab_indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        if(player!=null)
                            player.release();
                        btnprev.setVisibility(View.INVISIBLE);
                        btnnext.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.camera_final);
                        if(soundon)
                            player.start();
                        break;
                    case 2:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.phone_final);
                        if(soundon)
                            player.start();
                        break;
                    case 3:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.mart_final);
                        if(soundon)
                            player.start();
                        break;
                    case 4:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.gallery_final);
                        if(soundon)
                            player.start();
                        break;
                    case 5:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.notes_final);
                        if(soundon)
                            player.start();
                        break;
                    case 6:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.cart_final);
                        if(soundon)
                            player.start();
                        break;
                    case 7:
                        if(player!=null)
                            player.release();
                        btnnext.setVisibility(View.VISIBLE);
                        btnprev.setVisibility(View.VISIBLE);
                        getstarted.setVisibility(View.INVISIBLE);
                        tab_indicator.setVisibility(View.VISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.history_final);
                        if(soundon)
                            player.start();
                        break;
                    case 8:
                        if(player!=null)
                            player.release();
                        btnprev.setVisibility(View.VISIBLE);
                        btnnext.setVisibility(View.INVISIBLE);
                        getstarted.setVisibility(View.VISIBLE);
                        tab_indicator.setVisibility(View.INVISIBLE);
                        sound.setVisibility(View.VISIBLE);
                        player=MediaPlayer.create(getApplicationContext(),R.raw.summary_final);
                        if(soundon)
                            player.start();
                        loadLastScreen();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=screenpager.getCurrentItem();
                if(position< mList.size()){
                    position++;
                    screenpager.setCurrentItem(position);
                }

            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=screenpager.getCurrentItem();
                if(position>0 && position<mList.size()-1){
                    position--;
                    screenpager.setCurrentItem(position);
                }
            }
        });
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (soundon) {
                        soundon = false;
                        sound.setText("SOUND:ON");
                        if (player != null)
                            player.pause();
                    } else {
                        soundon = true;
                        sound.setText("SOUND:OFF");
                        if (player != null)
                            player.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player!=null) {
                    player.pause();
                    player.release();
                }
                closeGuide();
            }
        });
    }

    private void closeGuide() {
        startActivity(new Intent(Guide.this, MainActivity.class));
        SharedPreferences pref=getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("IsIntroOpened",true);
        editor.commit();
        finish();
    }

    private void loadLastScreen(){
        getstarted.setAnimation(btnAnim);
    }

    @Override
    protected void onPause() {

        super.onPause();
        soundon = false;
        sound.setText("SOUND:ON");
        if(player!=null) {
            try {
                player.pause();
                player.release();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        closeGuide();

    }
}
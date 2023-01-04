package com.example.lanchanika;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    Button backbtn,nextbtn,skipbtn;
    TextView[] dots;
    ViewPagerAdapter  viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        hideNavigationBar();
        backbtn = findViewById(R.id.backbtn);
        nextbtn = findViewById(R.id.nextbtn);
        skipbtn = findViewById(R.id.skipbtn);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getitem(0) > 0){
                    mSlideViewPager.setCurrentItem(getitem(-1),true);
                }

            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getitem(0) < 4){
                    mSlideViewPager.setCurrentItem(getitem(1),true);
                }else{
                    Intent i = new Intent(OnboardingActivity.this,HomeMenuActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnboardingActivity.this,HomeMenuActivity.class);
                startActivity(i);
                finish();

            }
        });

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);
        viewPagerAdapter = new ViewPagerAdapter(this);

        mSlideViewPager.setAdapter(viewPagerAdapter);

        setUpindicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

    }
    public void setUpindicator(int position){
        dots = new TextView[5];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.bgcolorinactive, getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.belowbgcolor, getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpindicator(position);

            if (position > 0){
                backbtn.setVisibility(View.VISIBLE);
            }else{
                backbtn.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private int getitem(int i){
        return mSlideViewPager.getCurrentItem() + i;
    }
    private void hideNavigationBar() {
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}

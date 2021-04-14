package house.thelittlemountaindev.afii.auth;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import house.thelittlemountaindev.afii.R;
import house.thelittlemountaindev.afii.adapters.OnBoardpagerAdapter;

/**
 * Created by Charlie One on 2/8/2018.
 */

public class ProfileSetupActivity extends AppCompatActivity {

    private int dotsCount;
    private ImageView[] dots;
    private ViewPager mPager;
    private LinearLayout dotsLayout;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        dotsLayout = findViewById(R.id.viewPagerCountDots);

        mPager = findViewById(R.id.pager_profile_setup);
        mPagerAdapter = new OnBoardpagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Change the current position intimation

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(ProfileSetupActivity.this, R.drawable.non_selected_item_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(ProfileSetupActivity.this, R.drawable.selected_item_dot));


/*                int pos=position+1;
                if(pos == dotsCount && previous_pos == (dotsCount-1))
                    show_animation();
                else if(pos==(dotsCount-1)&&previous_pos==dotsCount)
                    hide_animation();

                previous_pos=pos;*/
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUiPageViewController();
    }


    private void setUiPageViewController() {

        dotsCount = mPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(ProfileSetupActivity.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(16, 0, 16, 0);

            dotsLayout.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(ProfileSetupActivity.this, R.drawable.selected_item_dot));
    }
}


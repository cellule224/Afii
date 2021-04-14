package house.thelittlemountaindev.afii.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import house.thelittlemountaindev.afii.FragmentProfileSetupOne;
import house.thelittlemountaindev.afii.FragmentProfileSetupThree;
import house.thelittlemountaindev.afii.FragmentProfileSetupTwo;

/**
 * Created by Charlie One on 2/8/2018.
 */
public class OnBoardpagerAdapter extends FragmentPagerAdapter {

    public OnBoardpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentProfileSetupOne();
            case 1: return new FragmentProfileSetupTwo();
            case 2: return new FragmentProfileSetupThree();
            default: break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
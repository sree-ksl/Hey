package com.example.Hey;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.Locale;
import android.content.Context;

/**
 * Created by hello on 12/08/14.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        //getItem is called to instantiate the fragment for the given page.
        //Return a dummysectionfragment (defined as a static inner class below) with the page number as its long argument.
        switch (position){
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    //@Override
    public CharSequence getPageTitle(int position){
        Locale l = Locale.getDefault();
        switch(position){
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
      return null;
    }

    public int getIcon(int position){
        switch(position){
            case 0:
                return R.drawable.ic_tab_inbox;
            case 1:
                return R.drawable.ic_tab_friends;
        }

        return R.drawable.ic_tab_inbox;
    }
}

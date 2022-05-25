package com.example.lastommg;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyAdapter extends FragmentStateAdapter {
    public int mCount;

    public MyAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if (index == 0) return new fragment_1();
        else if (index == 1) return new fragment_2();
        else return new fragment_3();
    }

    @Override
    public int getItemCount() {
        return 2000;
    }

    public int getRealPosition(int position) {
        return position % mCount;
    }
}

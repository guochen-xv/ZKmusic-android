package com.android.music.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.music.R;
import com.android.music.adapter.HomeFragmentPagerAdapter;
import com.android.music.fragment.home.rank.FindFragment;
import com.android.music.fragment.home.musichall.MusicHallFragment;
import com.android.music.fragment.home.my.MyFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        //初始化滑动页面
        ViewPager viewPager = view.findViewById(R.id.main_view_pager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MyFragment());
        fragments.add(new MusicHallFragment());
        fragments.add(new FindFragment());
        viewPager.setAdapter(new HomeFragmentPagerAdapter(getChildFragmentManager(),fragments));
        //将tabLayout与viewPager关联，为tabLayout自动填充标题
        TabLayout tabLayout = view.findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }


}

package com.gidi.bio_console.fragment.ctrl;

import java.util.ArrayList;

import com.gidi.bio_console.fragment.AlarmFragment;
import com.gidi.bio_console.fragment.PerfusionFragment;
import com.gidi.bio_console.fragment.RecordFragment;
import com.gidi.bio_console.fragment.SettingFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentController {

	private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;
    private static FragmentController controller;

    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new FragmentController(activity, containerId);
        }
        return controller;
    }

    private FragmentController(FragmentActivity activity, int containerId) {
        this.containerId = containerId;
        fm = activity.getSupportFragmentManager();
        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new AlarmFragment());
        fragments.add(new RecordFragment());
        fragments.add(new SettingFragment());
        fragments.add(new PerfusionFragment());
        //fragments.add(new BloodGasFragment());
        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : fragments) {
            ft.add(containerId, fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments() {
        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : fragments) {
            if(fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
    
    public void destoryController(){
        controller = null;  
        if(null != fragments){
        	fragments.clear();
        	fragments = null;
        }
       
    }

    
}

package com.gidi.bio_console.kidney;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ControllerKidneyFragment {

	private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;
    private static ControllerKidneyFragment controller;

    public static ControllerKidneyFragment getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new ControllerKidneyFragment(activity, containerId);
        }
        return controller;
    }

    private ControllerKidneyFragment(FragmentActivity activity, int containerId) {
        this.containerId = containerId;
        fm = activity.getSupportFragmentManager();
        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new KidneyAlarmFragment());
        fragments.add(new KidneyRecordFragment());
        fragments.add(new KidneySettingFragment());
        fragments.add(new KidneyPerfusionFragment());
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

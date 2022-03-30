package com.gidi.bio_console.kidney;

import java.util.ArrayList;

import com.gidi.bio_console.fragment.setting.SettingSystemFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ControllerKidneySetting {

	private int containerId;
	
	private ArrayList<Fragment> settingFragments;
	
	private FragmentManager fm;
	
	private static ControllerKidneySetting controller;
	
	public static ControllerKidneySetting getInstance(Fragment parentFragment, int containerId){
		if(null == controller){
			controller = new ControllerKidneySetting(parentFragment,containerId);
		}
		return controller;
	}
	

	private ControllerKidneySetting(Fragment fragment, int containerId){
		this.containerId = containerId;
		fm = fragment.getChildFragmentManager();
		
		initFragment();
	}
	
	private void initFragment(){
		settingFragments = new ArrayList<Fragment>();
		settingFragments.add(new KidneySetPerfusionFragment());
		settingFragments.add(new KidneySetAlertFragment());
		settingFragments.add(new SettingSystemFragment());
		
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment fragment: settingFragments){
			ft.add(containerId, fragment);
		}
		ft.commit();
	}
	
	public void showFragment(int position){
		hideFragment();
		Fragment fragment = settingFragments.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(fragment);
		ft.commit();
	}
	
	public void hideFragment(){
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment fragment : settingFragments){
			if(null != fragment){
				ft.hide(fragment);
			}
		}
		ft.commit();
	}
	
	public Fragment getFragment(int position){
		Fragment fragment = null;
		if(null != settingFragments && settingFragments.size()>0){
			fragment =  settingFragments.get(position);
		}
		return fragment;
	}
	
	
    public void destoryController(){
        controller = null;  
        if(null != settingFragments){
        	settingFragments.clear();
        	settingFragments = null;
        }
    }
}

package com.gidi.bio_console.fragment.ctrl;

import java.util.ArrayList;

import com.gidi.bio_console.fragment.setting.SettingAlertFragment;
import com.gidi.bio_console.fragment.setting.SettingPerfusionFragment;
import com.gidi.bio_console.fragment.setting.SettingSystemFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class SettingFragmentController {

	private int containerId;
	
	private ArrayList<Fragment> settingFragments;
	
	private FragmentManager fm;
	
	private static SettingFragmentController controller;
	
	public static SettingFragmentController getInstance(Fragment parentFragment, int containerId){
		if(null == controller){
			controller = new SettingFragmentController(parentFragment,containerId);
		}
		return controller;
	}
	

	private SettingFragmentController(Fragment fragment, int containerId){
		this.containerId = containerId;
		fm = fragment.getChildFragmentManager();
		
		initFragment();
	}
	
	private void initFragment(){
		settingFragments = new ArrayList<Fragment>();
		settingFragments.add(new SettingPerfusionFragment());
		settingFragments.add(new SettingAlertFragment());
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

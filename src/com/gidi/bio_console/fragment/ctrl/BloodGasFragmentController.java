package com.gidi.bio_console.fragment.ctrl;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gidi.bio_console.fragment.bloodgas.BloodGasChartFragment;
import com.gidi.bio_console.fragment.bloodgas.BloodGasDataFragment;



public class BloodGasFragmentController {

	private int containerId;
	
	private FragmentManager fm;
	
	private ArrayList<Fragment> fragments;
	
	private static BloodGasFragmentController controller;
	
	public static BloodGasFragmentController getInstance(Fragment parentFragment, int containerId){
		if(null == controller){
			controller = new BloodGasFragmentController(parentFragment, containerId);
		}
		return controller;
	}

	private BloodGasFragmentController (Fragment fragment, int containerId){
		this.containerId = containerId;
		fm = fragment.getChildFragmentManager();
		
		initFragment();
	}
	
	private void initFragment(){
		fragments = new ArrayList<Fragment>();
		fragments.add(new BloodGasDataFragment());
		fragments.add(new BloodGasChartFragment());
		
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment fragment: fragments){
			ft.add(containerId, fragment);
		}
		ft.commit();
	}
	
	public void showFragment(int position){
		hideFragment();
		Fragment fragment = fragments.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(fragment);
		ft.commit();
	}
	
	public void hideFragment(){
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment fragment : fragments){
			if(null != fragment){
				ft.hide(fragment);
			}
		}
		ft.commit();
	}
	
	public Fragment getFragment(int position){
		Fragment fragment = null;
		if(null != fragments && fragments.size()>0){
			fragment =  fragments.get(position);
		}
		return fragment;
	}
	
    public void destoryController(){
        controller = null; 
        if(null != fragments){
        	fragments.clear();
        	fragments = null;
        }
    }
}

package com.gidi.bio_console.fragment.ctrl;

import java.util.ArrayList;
import com.gidi.bio_console.fragment.record.RecordChartFragment;
import com.gidi.bio_console.fragment.record.RecordDataFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class RecordFragmentController {

	private int containerId;
	
	private FragmentManager fm;
	
	private ArrayList<Fragment> fragments;
	
	private static RecordFragmentController controller;
	
	public static RecordFragmentController getInstance(Fragment parentFragment, int containerId){
		if(null == controller){
			controller = new RecordFragmentController(parentFragment, containerId);
		}
		return controller;
	}
	
	private RecordFragmentController(Fragment fragment, int containerId){
		this.containerId = containerId;
		this.fm = fragment.getChildFragmentManager();
		initFragments();
	}
	
	private void initFragments(){
		fragments = new ArrayList<Fragment>();
		fragments.add(new RecordDataFragment());
		fragments.add(new RecordChartFragment());
		//fragments.add(new BileRecordFragment());
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

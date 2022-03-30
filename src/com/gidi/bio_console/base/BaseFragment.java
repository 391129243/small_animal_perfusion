package com.gidi.bio_console.base;

import com.gidi.bio_console.R;
import com.gidi.bio_console.utils.ToastUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment {

	public BaseFragmentActivity mActivity;
	protected View rootView;
	
	/**获取布局文件**/
    protected abstract int getLayoutResource();
    
    protected abstract void initView(View rootView);
    
    protected abstract void initListener();

        
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = (BaseFragmentActivity)getActivity();
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mActivity = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null == rootView){
    		rootView = inflater.inflate(getLayoutResource(), container, false);
    	}
		
    	initView(rootView);
    	initListener();
    	rootView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				InputMethodManager manager = (InputMethodManager) getActivity()
						.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            if(event.getAction() == MotionEvent.ACTION_DOWN){
	            	if(getActivity().getCurrentFocus()!= null && getActivity().getCurrentFocus().getWindowToken()!=null){  
	                   manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
	                }  
	            } 
				return false;
			}
		});
		return rootView;
	}
	
	public BaseFragmentActivity getBaseActivity(){
		return mActivity;
	}
	
	/**set the textview is selected **/
	public void setSelectedView(TextView view){
		view.setBackgroundResource(R.drawable.shape_temp_choice_pressed_bg);
		view.setTextColor(getResources().getColor(R.color.blue));
	}

	public void setNormalView(TextView view){
		view.setBackgroundResource(R.drawable.shape_temp_choice_normal_bg);
		view.setTextColor(getResources().getColor(R.color.white));
	}
	
	public void displayToast(int resourceId){
		if(null != mActivity){
			ToastUtils.showToast(mActivity.getApplicationContext(), mActivity.getResources().getString(resourceId));
		}
	}
	
}

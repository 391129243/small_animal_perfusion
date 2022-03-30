package com.gidi.bio_console.fragment.preset;

import java.util.ArrayList;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gidi.bio_console.R;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.constant.Constants;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.listener.KeyBoardActionListener;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.view.SystemKeyBoardEditText;

public class PresetParamFragment extends BaseFragment implements OnClickListener{
	private ImageView mNextBtn;
	private TextView mHintTv;
	private TextView mIdTitleTv;
	private TextView mWeightTitleTv;
	private TextView mRightKidneyTitleTv;
	private TextView mIdPrefixTv;
	private SystemKeyBoardEditText mLiverNumEdit;
	private SystemKeyBoardEditText mWeightEdit;
	private SystemKeyBoardEditText mRightKidneyWeightEt;
	private ArrayList<String> idNameList;
	private OnPresetParamListener mListener;
	private String perfusion_system;
	
	public interface OnPresetParamListener{
		void onPresetParamNext(String liverName);
	}
	
	public void setOnOnPresetParamListener(OnPresetParamListener listener){
		this.mListener = listener;
	}
		
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != idNameList){
			idNameList.clear();
			idNameList = null;
		}
		mListener = null;
	}



	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_preset_param;
	}

	
	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mHintTv = (TextView)rootView.findViewById(R.id.preset_param_hint_tv);
		mIdPrefixTv = (TextView)rootView.findViewById(R.id.setting_perfusion_id_prefix_tv);
		mIdTitleTv = (TextView)rootView.findViewById(R.id.setting_perfusion_livernum_title_tv);
		mWeightTitleTv= (TextView)rootView.findViewById(R.id.setting_perfusion_weight_title_tv);
		mRightKidneyTitleTv = (TextView)rootView.findViewById(R.id.preset_param_right_kidney_weight_tv);
		mNextBtn = (ImageView)rootView.findViewById(R.id.preset_param_next_btn);
		mLiverNumEdit = (SystemKeyBoardEditText)rootView.findViewById(R.id.preset_param_liver_num_et);
		mWeightEdit = (SystemKeyBoardEditText)rootView.findViewById(R.id.preset_param_liver_weight_et);
		mRightKidneyWeightEt = (SystemKeyBoardEditText)rootView.findViewById(R.id.preset_param_right_kidney_weight_et);
		perfusion_system = PreferenceUtil.getInstance(this.getActivity().getApplicationContext())
				.getStringValue(SharedConstants.PERFUION_STYSTEM, Constants.LIVER_PERFUSION_STYSTEM);
		if(perfusion_system.equals(Constants.LIVER_PERFUSION_STYSTEM)){
			mHintTv.setText(R.string.preset_param_liver_hint);
			mIdPrefixTv.setText(R.string.preset_param_id_prefix_liver);
			mIdTitleTv.setText(R.string.setting_liver_num);
			mWeightTitleTv.setText(R.string.setting_liver_weight);
			mRightKidneyTitleTv.setVisibility(View.INVISIBLE);
			mRightKidneyWeightEt.setVisibility(View.INVISIBLE);
		}else{
			mHintTv.setText(R.string.preset_param_kidney_hint);
			mIdPrefixTv.setText(R.string.preset_param_id_prefix_kidney);
			mIdTitleTv.setText(R.string.setting_kidney_num);
			mWeightTitleTv.setText(R.string.setting_kidney_weight);
			mRightKidneyTitleTv.setVisibility(View.VISIBLE);
			mRightKidneyWeightEt.setVisibility(View.VISIBLE);
		}
	}

	
	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mNextBtn.setOnClickListener(this);
		mLiverNumEdit.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub
			}
		});
		mWeightEdit.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub

			}
		}); 
		mRightKidneyWeightEt.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
			@Override
			public void onTextChange(Editable editable) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClearAll() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClear() {
				// TODO Auto-generated method stub

			}
		}); 

	}
	
	private void saveLiverInfo(String number, int weight){
		if(perfusion_system.equals(Constants.LIVER_PERFUSION_STYSTEM)){
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.LIVER_NUMBER, number);
		}else{
			PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.setValueByName(SharedConstants.KIDNEY_NUM, number);
		}
		
		PreferenceUtil.getInstance(getActivity().getApplicationContext())
					.setValueByName(SharedConstants.LIVER_WEIGHT, weight);
	}
	
	private void saveKidneyInfo(String number, int leftKidneyWeight, int rightKidneyWeight){
		PreferenceUtil.getInstance(getActivity().getApplicationContext())
			.setValueByName(SharedConstants.KIDNEY_NUM, number);
		PreferenceUtil.getInstance(getActivity().getApplicationContext())
			.setValueByName(SharedConstants.LEFT_KIDNEY_WEIGHT, leftKidneyWeight);
		PreferenceUtil.getInstance(getActivity().getApplicationContext())
			.setValueByName(SharedConstants.RIGHT_KIDNEY_WEIGHT, rightKidneyWeight);
	}
	
	public void setIDNameList(ArrayList<String> list){
		
		idNameList = new ArrayList<String>();
		idNameList.clear();
		idNameList.addAll(list);
	}
	
	private boolean checkLiverName(String name){
		boolean result = false;
		if(idNameList != null){
			if(idNameList.size()>0){
				for(String id :idNameList){
					if(id.equals(name)){
						return true;
					}
				}
			}			
		}		
		return result;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.preset_param_next_btn:
			StringBuilder liverNum = new StringBuilder();
			String prefix = mIdPrefixTv.getText().toString().trim();
			String liverID = liverNum.append(prefix)
					.append(mLiverNumEdit.getText().toString().trim()).toString();
			String liverWeight = mWeightEdit.getText().toString().trim();
			String rightKidneyWeight = mRightKidneyWeightEt.getText().toString().trim();
			if(perfusion_system.equals(Constants.LIVER_PERFUSION_STYSTEM)){
				if(StringUtil.isEmpty(liverID)){
					displayToast(R.string.error_liver_id_is_null);
					return;
				}else if(checkLiverName(liverID)){
					displayToast(R.string.error_liver_name_repeated);
					return;
				}else if(StringUtil.isEmpty(liverWeight)){
					displayToast(R.string.error_liver_weight_is_null);
					return;
				}
				saveLiverInfo(liverID, StringUtil.convertToInt(liverWeight, 0));
			}else{
				if(StringUtil.isEmpty(liverID)){
					displayToast(R.string.error_liver_id_is_null);
					return;
				}else if(checkLiverName(liverID)){
					displayToast(R.string.error_liver_name_repeated);
					return;
				}else if(StringUtil.isEmpty(liverWeight)){
					displayToast(R.string.error_left_kidnet_weight_is_null);
					return;
				}else if(StringUtil.isEmpty(rightKidneyWeight)){
					displayToast(R.string.error_right_kidney_weight_is_null);
				}
				saveKidneyInfo(liverID, StringUtil.convertToInt(liverWeight, 0), StringUtil.convertToInt(rightKidneyWeight, 0));
			}
			
			if(null != mListener){
				mListener.onPresetParamNext(liverID);
				Log.i("PresetParamFragment", "liverID--" + liverID);
			}
			break;

		default:
			break;
		}
	}



}

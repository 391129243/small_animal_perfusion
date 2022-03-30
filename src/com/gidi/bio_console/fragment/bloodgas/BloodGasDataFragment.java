package com.gidi.bio_console.fragment.bloodgas;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.gidi.bio_console.R;
import com.gidi.bio_console.adapter.BloodGasAdapter;
import com.gidi.bio_console.adapter.BloodGasSampleTimeAdapter;
import com.gidi.bio_console.base.BaseFragment;
import com.gidi.bio_console.bean.BloodGasBean;
import com.gidi.bio_console.bean.BloodGasSamplingBean;
import com.gidi.bio_console.constant.SharedConstants;
import com.gidi.bio_console.utils.DateFormatUtil;
import com.gidi.bio_console.utils.PreferenceUtil;
import com.gidi.bio_console.utils.StringUtil;
import com.gidi.bio_console.view.CommonPopupWindow;
import com.gidi.bio_console.view.CustomDialog;
import com.gidi.bio_console.view.CustomTitleEdit;

public class BloodGasDataFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "BloodGasDataFragment";
	private BloodGasAdapter mBloodGasAdapter;
	private BloodGasSampleTimeAdapter mBloodGasTimeAdapter;
	private CustomTitleEdit mASTEdit;
	private CustomTitleEdit mALTEdit;
	private CustomTitleEdit mGluEdit;
	private CustomTitleEdit mPhEdit;
	private CustomTitleEdit mPO2Edit;
	private CustomTitleEdit mPCO2Edit;
	private CustomTitleEdit mHctEdit;
	private CustomTitleEdit mBicarbonateEdit;
	private CustomTitleEdit mLacEdit;
	private CommonPopupWindow window_edit_del;
	private TextView mDelTxt;
	private TextView mEditTxt;
	private TextView mSamplTimeTxt;
	private Button mSaveBtn;
	private ListView mBloodGasListView;
	private ArrayList<BloodGasBean> mBloodGasDataList;
	private ArrayList<BloodGasSamplingBean> mSamplingTimeList;
	private CustomDialog mSamplingDialog;
	private String liver_Num;
	private String mSampleTime;

	private OnBloodGasDataChangeListener onDataChangeListener;
	
	public void setOnBloodGasDataChangeListener(OnBloodGasDataChangeListener listener){
		this.onDataChangeListener = listener;
	}
	
	public interface OnBloodGasDataChangeListener{
		void onSaveBloodGas(BloodGasBean bloodGasBean);
		void onDeleteBloodGas(BloodGasBean bloodGasBean);
		void onEditBloodGas();
	}
	
	/***
	 * 实例化fragment
	 */
	public static BloodGasDataFragment newInstance(){
		Bundle bundle = new Bundle();
		BloodGasDataFragment fragment = new BloodGasDataFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
				
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initVariables();
		initSearchTime();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(null != mSamplingTimeList){
			if(mSamplingTimeList.size()>0){
				mSamplTimeTxt.setText(mSamplingTimeList.get(0).getSamplingTime());
			}else{
				//如果是空显示进入的时间
				mSamplTimeTxt.setText(DateFormatUtil.formatFullDate(System.currentTimeMillis()));
			}
		}
		
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cancelDialog();
		clearList();
		if(null != onDataChangeListener){
			onDataChangeListener = null;
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(!hidden){
		{
			if(null != mSamplingTimeList)
				if(mSamplingTimeList.size()>0){
					mSamplTimeTxt.setText(mSamplingTimeList.get(0).getSamplingTime());
				}
			}
		}
	
		
	}
	
	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.module_fragment_bloodgas_data;
	}


	private void initSearchTime(){
		mSamplingTimeList = new ArrayList<BloodGasSamplingBean>();
		mSamplingTimeList.clear();		
	}

	@Override
	protected void initView(View rootView) {
		// TODO Auto-generated method stub
		mASTEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_ast);
		mALTEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_alt);
		mGluEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_glu);
		mPhEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_ph);
		mPO2Edit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_po2);
		mPCO2Edit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_pco2);
		mBicarbonateEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_Bicarbonate);	
		mHctEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_hct);
		mLacEdit = (CustomTitleEdit)rootView.findViewById(R.id.blood_gas_data_lac);
		mSamplTimeTxt = (TextView)rootView.findViewById(R.id.sampling_time_txt);
		mSaveBtn = (Button)rootView.findViewById(R.id.blood_gas_save_btn);
		mBloodGasListView = (ListView)rootView.findViewById(R.id.blood_gas_data_listview);
		mBloodGasListView.setAdapter(mBloodGasAdapter);
		rootView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
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
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		mSaveBtn.setOnClickListener(this);
		mSamplTimeTxt.setOnClickListener(this);
		//列表长按可以编辑
		mBloodGasListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				//在每个item的上方弹出编辑和删除的window
				int[] array = new int[2];
	            view.getLocationOnScreen(array);
	            int x = array[0];
	            int y = array[1];
				BloodGasBean bloodGasBean = mBloodGasDataList.get(position);
				showEditDelWindow(view,x,y,bloodGasBean);
			}
		});
	}

	private void initVariables(){
		liver_Num = PreferenceUtil.getInstance(getActivity().getApplicationContext())
				.getStringValue(SharedConstants.LIVER_NUMBER, "");
		mBloodGasDataList = new ArrayList<BloodGasBean>();		
		mBloodGasAdapter = new BloodGasAdapter(getActivity().getApplicationContext());
		mBloodGasTimeAdapter = new BloodGasSampleTimeAdapter(getActivity().getApplicationContext());
	}
	
	private void showEditDelWindow(View view,final int x, final int y,final BloodGasBean bean){
		window_edit_del = new CommonPopupWindow(getBaseActivity(), R.layout.popup_window_edit_delete_layout,
				180,
				ViewGroup.LayoutParams.WRAP_CONTENT) {
			
			@Override
			protected void initView() {
				// TODO Auto-generated method stub
				View view = getContentView();
				mDelTxt = (TextView) view.findViewById(R.id.pop_delete);
				mEditTxt = (TextView) view.findViewById(R.id.pop_edit);
			}
			
			@Override
			protected void initEvent() {
				// TODO Auto-generated method stub
				mDelTxt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						//删除当前的item
						deleteBloodData(bean);
						window_edit_del.dismissWindow();
					}
				});
				mEditTxt.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub5
						//编辑当前xiang
						editBloodData(bean);
						window_edit_del.dismissWindow();
					}
				});
			}
			
			@Override
			protected void initWindow() {
				super.initWindow();
				PopupWindow instance = getPopupWindow();
				// 设置PopupWindow是否能响应外部点击事件
				instance.setOutsideTouchable(true);
				// 设置PopupWindow是否能响应点击事件
				instance.setTouchable(true); // 设置PopupWindow可触摸
				instance.setFocusable(true); // 设置PopupWindow可获得焦点
			}
		};
		window_edit_del.showAtLocation(view, Gravity.NO_GRAVITY, x + 50, y - 20);
	}

	/**删除血气记录**/
	private void deleteBloodData(BloodGasBean bean){
		if(null != onDataChangeListener){
			onDataChangeListener.onDeleteBloodGas(bean);
		}
	}
	
	/**编辑血气记录**/
	private void editBloodData(BloodGasBean bean){
		
		String ast = bean.getAst();
		String alt = bean.getAlt();
		String glu = bean.getGlu();
		String ph = bean.getPh();
		String po2 = bean.getPo2();;
		String pco2 = bean.getPco2();
		String bicarbonate = bean.getBicarbonate();
		String hct = bean.getHct();
		String lac = bean.getLac();
		String sampleTime = bean.getSampleTime();
		mASTEdit.setText(ast);
		mALTEdit.setText(alt);
		mGluEdit.setText(glu);
		mPhEdit.setText(ph);
		mPO2Edit.setText(po2);
		mPCO2Edit.setText(pco2);
		mBicarbonateEdit.setText(bicarbonate);
		mHctEdit.setText(hct);
		mLacEdit.setText(lac);
		mSamplTimeTxt.setText(sampleTime);
		setSampleTimeEnable(false);
		if(null != onDataChangeListener){
			onDataChangeListener.onEditBloodGas();
		}
		
	}

	/**保存血气记录**/
	private void saveBloodGasData(){
	
		String mASTValue = mASTEdit.getText();
		String mALTValue = mALTEdit.getText();
		String mGluValue = mGluEdit.getText();
		String mPhValue = mPhEdit.getText();
		String mPO2Value = mPO2Edit.getText();
		String mPCO2Value = mPCO2Edit.getText();
		String mBicarValue = mBicarbonateEdit.getText();
		String mHctValue = mHctEdit.getText();
		String mLac = mLacEdit.getText();
		Log.i(TAG, "mASTValue--" + mASTValue + "  mALTValue---" + mALTValue + " mGluValue--" + mGluValue + " mPhValue---" + mPhValue + 
				" mPO2Value---" + mPO2Value + " mPCO2Value--" + mPCO2Value + " mBicarValue---" + mBicarValue + " mHctValue--" + mHctValue);
		if(!StringUtil.isEmpty(mASTValue) && !StringUtil.isEmpty(mALTValue)&& !StringUtil.isEmpty(mGluValue) 
				&& !StringUtil.isEmpty(mPhValue)&& !StringUtil.isEmpty(mPO2Value) && !StringUtil.isEmpty(mPCO2Value)
				&& !StringUtil.isEmpty(mBicarValue) && !StringUtil.isEmpty(mHctValue) && !StringUtil.isEmpty(mLac)){
			BloodGasBean mBloodGasBean = new BloodGasBean();
			mBloodGasBean.setLiverNum(liver_Num);
			mBloodGasBean.setAst(mASTValue);
			mBloodGasBean.setAlt(mALTValue);
			mBloodGasBean.setGlu(mGluValue);
			mBloodGasBean.setPh(mPhValue);
			mBloodGasBean.setPo2(mPO2Value);
			mBloodGasBean.setPco2(mPCO2Value);
			mBloodGasBean.setBicarbonate(mBicarValue);
			mBloodGasBean.setHct(mHctValue);
			mBloodGasBean.setLac(mLac);
			mBloodGasBean.setSampleTime(mSamplTimeTxt.getText().toString().trim());//血气采样时间
			if(null != onDataChangeListener){
				onDataChangeListener.onSaveBloodGas(mBloodGasBean);
			}
			clearAllEditText();
		}else{
			//请完整填入参数
			displayToast(R.string.bloodgas_toast_input_imcomplete);
		}
	}
	
	private void clearAllEditText(){
		mASTEdit.clearText();
		mALTEdit.clearText();
		mGluEdit.clearText();
		mPhEdit.clearText();
		mPO2Edit.clearText();
		mPCO2Edit.clearText();
		mBicarbonateEdit.clearText();
		mHctEdit.clearText();
		mLacEdit.clearText();
	}
	
	public void setSampleTimeEnable(boolean isEnable){
		mSamplTimeTxt.setEnabled(isEnable);
	}
	
	public void setBloodGasSampleTimeList(ArrayList<BloodGasSamplingBean> list){
		mSamplingTimeList.clear();
		mSamplingTimeList.addAll(list);
		if(null != mSamplTimeTxt){
			if(mSamplingTimeList.size()>0){
				mSamplTimeTxt.setText(mSamplingTimeList.get(0).getSamplingTime());
			}			
		}
		
	}
	
	public void setBloodGasList(ArrayList<BloodGasBean> bloodGasList){
		mBloodGasDataList.clear();
		mBloodGasDataList.addAll(bloodGasList);
		if(null != mBloodGasAdapter){
			mBloodGasAdapter.setBloodGasList(mBloodGasDataList);
			mBloodGasAdapter.notifyDataSetChanged();
		}		
	}
	
	private void cancelDialog(){
		if(null != mSamplingDialog){
			mSamplingDialog.dismiss();
			mSamplingDialog = null;
		}
	}
	
	private void clearList(){
		if(null != mBloodGasDataList){
			mBloodGasDataList.clear();
			mBloodGasDataList = null;
		}
		if(null != mSamplingTimeList){
			mSamplingTimeList.clear();
			mSamplingTimeList = null;
		}
	}
	
	private void showSamplingTimeDialog(){
		if(null != mSamplingDialog){
			mSamplingDialog.dismiss();
			mSamplingDialog = null;
		}
		if(null == mSamplingDialog){
			mSamplingDialog = new CustomDialog(getActivity());
			LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = (View)mInflater.inflate(R.layout.dialog_blood_gas_time_layout, null);
			ListView listView = (ListView)view.findViewById(R.id.blood_gas_time_listview);
			listView.setAdapter(mBloodGasTimeAdapter);
			mBloodGasTimeAdapter.setBloodTimeList(mSamplingTimeList);
			mBloodGasTimeAdapter.notifyDataSetChanged();
			CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
			builder.setContentView(view);
			builder.setTitle(R.string.dialog_title_blood_gas_time);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View parent,
						int position, long id) {
					// TODO Auto-generated method stub
					for(int i = 0;i<mSamplingTimeList.size();i++){
						//全部清零
						BloodGasSamplingBean bean = mSamplingTimeList.get(i);
						bean.setChecked(0);
						mBloodGasTimeAdapter.notifyDataSetChanged();
					}
					BloodGasSamplingBean checkBean = mSamplingTimeList.get(position);
					Log.i(TAG, "checkBean.getIsChecked()---" + checkBean.getIsChecked());
					if(checkBean.getIsChecked()==0){
						checkBean.setChecked(1);
					}else if(checkBean.getIsChecked()== 1){
						checkBean.setChecked(0);
					}
					mSampleTime = checkBean.getSamplingTime();
					mBloodGasTimeAdapter.setBloodTimeList(mSamplingTimeList);
					mBloodGasTimeAdapter.notifyDataSetChanged();
					
					
				}
			});
			builder.setPositiveButton(R.string.dialog_ok,  new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(!StringUtil.isEmpty(mSampleTime)){
						mSamplTimeTxt.setText(mSampleTime);
					}
					mSamplingDialog.dismiss();
				}
			});
			
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mSamplingDialog.dismiss();
					mSamplingDialog = null;
				}
			});
			mSamplingDialog = builder.create();
			mSamplingDialog.show();
		}
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.blood_gas_save_btn:
			saveBloodGasData();			
			break;
		
		case R.id.sampling_time_txt:
			showSamplingTimeDialog();
			break;
		default:
			break;
		}
	}
	

}

package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SystemSettingItem extends RelativeLayout{

	private RelativeLayout setItemLayout;
	private ImageView mTitleIcon;
	private TextView mTitleTxt;
	private TextView mConetentTxt;
	
	private int icon_id;
	private String title;
	private String content;
	
	

	
	public SystemSettingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initAttrArray(context, attrs);
		initView(context);	
		
	}

	
	
	public SystemSettingItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);	
	}

	private void initAttrArray(Context context, AttributeSet attrs){
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SystemSettingItem);
		title = mTypedArray.getString(R.styleable.SystemSettingItem_title_text);
		content = mTypedArray.getString(R.styleable.SystemSettingItem_content_text);
		icon_id = mTypedArray.getResourceId(R.styleable.SystemSettingItem_title_icon,R.drawable.shape_gradient_artery_txt_bg);	
		mTypedArray.recycle();
	}
	
	private void initView(Context context){
		LayoutInflater.from(context).inflate(R.layout.module_include_layout_system_setting_item, this, true);
		setItemLayout = (RelativeLayout)findViewById(R.id.system_item_layout);
		mTitleTxt = (TextView)findViewById(R.id.system_item_title_tv);
		mConetentTxt = (TextView)findViewById(R.id.system_item_title_content_tv);
		mTitleIcon = (ImageView)findViewById(R.id.system_item_img);
		mTitleTxt.setText(title);
		mConetentTxt.setText(content);
		mTitleIcon.setImageResource(icon_id);
			
	}
	
	public void setSysItemContent(String content){
		this.content = content;
		mConetentTxt.setText(content);
	}
	
	private OnSystemSetItemClickListener onSystemSetItemClickListener;
	public interface OnSystemSetItemClickListener{
		public void onSystemSetItemClick();
	}
	
	public void setOnSystemSetItemClickListener(OnSystemSetItemClickListener listener){
		this.onSystemSetItemClickListener = listener;
		setItemLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != onSystemSetItemClickListener){
					onSystemSetItemClickListener.onSystemSetItemClick();
				}
			}
		});

	}
}

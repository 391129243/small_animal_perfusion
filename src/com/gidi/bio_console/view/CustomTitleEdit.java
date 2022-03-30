package com.gidi.bio_console.view;

import com.gidi.bio_console.R;
import com.gidi.bio_console.listener.KeyBoardActionListener;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomTitleEdit extends RelativeLayout {

	private TextView mTitleTxt;
	private SystemKeyBoardEditText mContentEdit;

	
	public CustomTitleEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate( R.layout.layout_custom_title_edittext, this, true);
		initView();		
		init(context,attrs);
		
	}

	
	private void initView(){
		mTitleTxt = (TextView)findViewById(R.id.custom_edit_title_txt);
		mContentEdit = (SystemKeyBoardEditText)findViewById(R.id.custom_edit);
		mContentEdit.setOnKeyboardActionListener(new KeyBoardActionListener() {
			
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
		mContentEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContentEdit.setText("");
			}
		});
	}

	
	private void init(Context context, AttributeSet attrs){
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleEdit);
		String titleText = attrArray.getString(R.styleable.CustomTitleEdit_title_text);
		int titleColor = attrArray.getColor(R.styleable.CustomTitleEdit_title_text_color, Color.WHITE);
		int contentColor = attrArray.getColor(R.styleable.CustomTitleEdit_content_text_color, Color.BLACK);
		int edit_maxLength = attrArray.getInt(R.styleable.CustomTitleEdit_edit_maxlength, 5);
		mTitleTxt.setText(titleText);
		mTitleTxt.setTextColor(titleColor);
		mContentEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(edit_maxLength)});
		attrArray.recycle();
	}
	
	
	public String getText(){
		return mContentEdit.getText().toString().trim();
	}
	
	public void setText(String text){
		mContentEdit.setText(text);
	}
	
	public void clearText(){
		mContentEdit.setText("");
	}
}

package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.view.View.OnClickListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomImageButton extends RelativeLayout implements OnClickListener {

	private ImageView mImgView = null;  
    private TextView mTextView = null;  
    private RelativeLayout parentLayout;
    private Context mContext;
    private int content_color;
    private OnImageButtonClickListener mCtmImageButtonListener;
    public void setOnImageButtonClickListener(OnImageButtonClickListener customImgBtnClickListener){
        //在setter中把这个接口的实现赋值给这个loginview的上面定义的接口
         this.mCtmImageButtonListener = customImgBtnClickListener;
        
   }
   //接口
   public interface OnImageButtonClickListener{
       public void CustomImageButtonClicked(View v);//传的参数
   }
      
    public CustomImageButton(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_custom_imagebutton, this, true);
        parentLayout = (RelativeLayout)findViewById(R.id.parent_layout);
        mImgView = (ImageView)findViewById(R.id.img);  
        mTextView = (TextView)findViewById(R.id.text);
        mImgView.setOnClickListener(this);   		
        init(context,attrs);          
          
    }  
    
    private void init(Context context, AttributeSet attrs){
    	TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageButton);
    	String content_text = attrArray.getString(R.styleable.CustomImageButton_content_text);
    	content_color = attrArray.getColor(R.styleable.CustomImageButton_content_text_color, R.color.color_preset_artery_mode_normal);
    	int content_bg = attrArray.getResourceId(R.styleable.CustomImageButton_content_img, R.drawable.artery_perfusion_const_mode_normal);
    	mImgView.setImageResource(content_bg);
    	mTextView.setText(content_text);
    	mTextView.setTextColor(content_color);
    	attrArray.recycle();
    }
    
    public void setImageBtnResoure(int resId, int color){
    	this.content_color = color;
    	mImgView.setImageResource(resId); 
    	mTextView.setTextColor(mContext.getResources().getColor(content_color));
    }
   
   /*设置图片接口*/  
    public void setImageResource(int resId){  
        mImgView.setImageResource(resId);  
    }  
      
    /*设置文字接口*/  
    public void setText(String str){  
        mTextView.setText(str);  
    }  
    /*设置文字大小*/  
    public void setTextSize(float size){  
        mTextView.setTextSize(size);  
    }

    /*设置文字大小*/  
    public void setTextColor(int resId){  
        mTextView.setTextColor(resId);  
    }
    
    public void setBackgroudResId(int resId){
    	parentLayout.setBackgroundResource(resId);
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(null != mCtmImageButtonListener){
			mCtmImageButtonListener.CustomImageButtonClicked(v);
		}
	}   
    
}

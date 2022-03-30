package com.gidi.bio_console.view;



import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的温度灌注模式选择器
 * @author 80657
 *
 */
public class CustomTempModeButton extends RelativeLayout implements OnClickListener{
	private RelativeLayout parentLayout = null;
	private ImageView mImgView = null;  
    private TextView mTextView = null;  
    private int content_color;
    private Context mContext;
    private OnTempModeButtonClickListener mCtmITempModeBtnListener;
    
    public void setOnTempModeBtnClickListener(OnTempModeButtonClickListener ctmTempModeBtnClickListener){
        //在setter中把这个接口的实现赋值给这个loginview的上面定义的接口
         this.mCtmITempModeBtnListener = ctmTempModeBtnClickListener;        
    }
   //接口
    public interface OnTempModeButtonClickListener{
       public void CustomTempModeButtonClicked(View v);//传的参数
    }
     
    public CustomTempModeButton(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub         
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_custom_tempmode_button, this, true);
        parentLayout = (RelativeLayout)findViewById(R.id.parent_layout);
        mImgView = (ImageView)findViewById(R.id.temp_mode_img);  
        mTextView = (TextView)findViewById(R.id.temp_mode_text);
        parentLayout.setOnClickListener(this);   		
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
    	TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTempModeButton);
    	String content_text = attrArray.getString(R.styleable.CustomTempModeButton_content_text);
    	content_color = attrArray.getColor(R.styleable.CustomTempModeButton_content_text_color, R.color.color_preset_artery_mode_normal);
    	int content_bg = attrArray.getResourceId(R.styleable.CustomTempModeButton_content_image, R.drawable.temp_normal_mode);
    	mImgView.setImageResource(content_bg);
    	mTextView.setText(content_text);
    	mTextView.setTextColor(content_color);
    	attrArray.recycle();
    }
    
    /*设置文字颜色*/  
    public void setTextColor(int resId){  
        mTextView.setTextColor(resId);  
    }
    
    /**
     * 设置图片
     */
    public void setImageResId(int resId){
    	mImgView.setImageResource(resId);
    }
//    
    public void setBackgroudResId(int resId){
    	parentLayout.setBackgroundResource(resId);
    }
    
    public void setImageBtnResoure(int resId, int color){
    	this.content_color = color;
    	mImgView.setImageResource(resId); 
    	mTextView.setTextColor(mContext.getResources().getColor(content_color));
    }
        
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(null != mCtmITempModeBtnListener){
			mCtmITempModeBtnListener.CustomTempModeButtonClicked(v);
		}
	}  
    
}

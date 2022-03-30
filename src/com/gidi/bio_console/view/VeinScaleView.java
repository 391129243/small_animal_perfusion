package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class VeinScaleView extends View {

	private int mMax;
	private int mMin;
	private int mScaleHeight;
	private int mScaleWidth;
	private int mMarginValue;
	private int commonScaleColor;
	private int maxScaleColor;
	
	public VeinScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public VeinScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public VeinScaleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

    protected void init(AttributeSet attrs) {
        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.VeinScaleView);
       // mMin = ta.getInteger(R.styleable.m, 0);
        mMax = ta.getInteger(R.styleable.VeinScaleView_max_vein_scale_view,15);
        mMin = ta.getInteger(R.styleable.VeinScaleView_min_vein_scale_view, 0);
        commonScaleColor = ta.getColor(R.styleable.VeinScaleView_common_scaleColor, getColorResoure(R.color.white));
        maxScaleColor = ta.getColor(R.styleable.VeinScaleView_max_scaleColor, getColorResoure(R.color.color_axescolor));
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.VeinScaleView_height_vein_scale_view, 20);
        ta.recycle();
        initVar();
    }

    private int getColorResoure(int resId){
		 return getResources().getColor(resId);
	}
    
    private void initVar(){
    	mMarginValue = (mMax - mMin)/5;
    	
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int mWidth = MeasureSpec.getSize(widthMeasureSpec)-5;  
		mScaleWidth = mWidth/5;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		initPaint(canvas);
		
	}
    
	private void initPaint(Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(commonScaleColor);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextAlign(Paint.Align.CENTER);	
		paint.setTextSize(12);
		onDrawScale(canvas,paint);
	}
	
	private void onDrawScale(Canvas canvas, Paint paint){
		for(int i = 0;i <= 5 ; i++)
		if(i==0){
			canvas.drawText(String.valueOf(i*mMarginValue), i*mScaleWidth+2, mScaleHeight, paint);
		}else if(i==5){
			paint.setColor(maxScaleColor);
			paint.setTextSize(22);
			canvas.drawText(String.valueOf(i*mMarginValue), i*mScaleWidth-6, mScaleHeight, paint);
			//最后一个刻度标亮			
		}else{
			canvas.drawText(String.valueOf(i*mMarginValue), i*mScaleWidth, mScaleHeight, paint);
		}
		
	}
}

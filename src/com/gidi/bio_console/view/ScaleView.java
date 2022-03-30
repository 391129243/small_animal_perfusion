package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScaleView extends View {

	private int mMax;
	private int mMin;
	private int mScaleHeight;
	private int mScaleWidth;
	private int mMarginValue;
	private int comScaleColor;
	private int maxScaleColor;
	
	public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public ScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public ScaleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

    protected void init(AttributeSet attrs) {
        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ScaleView);
       // mMin = ta.getInteger(R.styleable.m, 0);
        mMax = ta.getInteger(R.styleable.ScaleView_max_scale_view,120);
        mMin = ta.getInteger(R.styleable.ScaleView_min_scale_view, 0);
        comScaleColor = ta.getColor(R.styleable.ScaleView_commonScaleColor, getColorResoure(R.color.white));
        maxScaleColor = ta.getColor(R.styleable.ScaleView_maxScaleColor, getColorResoure(R.color.color_axescolor));
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.ScaleView_height_scale_view, 20);
        ta.recycle();
        initVar();
    }
    
    private int getColorResoure(int resId){
		 return getResources().getColor(resId);
	}
	
    private void initVar(){
    	mMarginValue = (mMax - mMin)/10;    	
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int mWidth = MeasureSpec.getSize(widthMeasureSpec)-5;  
		mScaleWidth = mWidth/10;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		initPaint(canvas);
		
	}
    
	private void initPaint(Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(comScaleColor);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextAlign(Paint.Align.CENTER);	
		paint.setTextSize(12);
		onDrawScale(canvas,paint);
	}
	
	private void onDrawScale(Canvas canvas, Paint paint){
		for(int i = 0;i <= 10 ; i++)
		if(i==0){
			canvas.drawText(String.valueOf(i*mMarginValue), i*mScaleWidth+2, mScaleHeight, paint);
		}else if(i==10){
			paint.setColor(maxScaleColor);
			paint.setTextSize(22);
			canvas.drawText(String.valueOf(i*mMarginValue), i*mScaleWidth-6, mScaleHeight, paint);
			//最后一个刻度标亮			
		}else{
			canvas.drawText(String.valueOf(i*mMarginValue), i*mScaleWidth, mScaleHeight, paint);
		}
		
	}
    
}

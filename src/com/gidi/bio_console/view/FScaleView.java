package com.gidi.bio_console.view;

import java.text.DecimalFormat;

import com.gidi.bio_console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class FScaleView extends View {
	
	private float mMax;
	private float mMin;
	private int mScaleHeight;
	private int mScaleWidth;
	private double mMarginValue;
	private DecimalFormat nf = new  DecimalFormat( "0.0"); 
	private int comScaleColor;
	private int maxScaleColor;

	public FScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public FScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(attrs);
	}

	public FScaleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(null);
	}

	private void init(AttributeSet attrs) {
	        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FScaleView);
        mMax = ta.getFloat(R.styleable.FScaleView_scale_view_max,2.0f);
        mMin = ta.getFloat(R.styleable.FScaleView_scale_view_min,0.0f);
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.FScaleView_scale_view_height, 20);
        comScaleColor = ta.getColor(R.styleable.FScaleView_commonFscaleColor, getColorResoure(R.color.white));
        maxScaleColor = ta.getColor(R.styleable.FScaleView_maxFscaleColor, getResources().getColor(R.color.color_axescolor));
        ta.recycle();
        initVar();
	}
	
	private int getColorResoure(int resId){
		 return getResources().getColor(resId);
	}
	
    private void initVar(){	
    	String number = nf.format((float)(mMax - mMin)/5);
    	mMarginValue = Double.parseDouble(number);
    	
    }

    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int mWidth=MeasureSpec.getSize(widthMeasureSpec)-5;  
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
		paint.setColor(comScaleColor);
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
			canvas.drawText(nf.format(i*mMarginValue), i*mScaleWidth+8, mScaleHeight, paint);
		}else if(i==5){
			paint.setTextSize(22);
			paint.setColor(maxScaleColor);
			canvas.drawText(nf.format(i*mMarginValue), i*mScaleWidth-6, mScaleHeight, paint);
		}else{
			canvas.drawText(nf.format(i*mMarginValue), i*mScaleWidth, mScaleHeight, paint);
		}
		
	}
}

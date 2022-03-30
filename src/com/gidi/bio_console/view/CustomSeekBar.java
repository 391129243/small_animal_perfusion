package com.gidi.bio_console.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/***
 * 
 * @author admin
 *
 */
public class CustomSeekBar extends SeekBar{

	public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomSeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//return super.onTouchEvent(event);
		return false;
	}

	
}

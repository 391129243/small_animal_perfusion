package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomSpeedDialog extends Dialog{
	public CustomSpeedDialog(Context context) {  
		super(context);  
	}  
	public CustomSpeedDialog(Context context, int theme) {  
		super(context, theme);  
	}  

	public static class Builder {  
	private Context context;  
	private String title;  
	private String message;  
	private String positiveButtonText;  
	private String negativeButtonText;  
	private View contentView;  
	private int iconResId = 0 ;
	private DialogInterface.OnClickListener positiveButtonClickListener;  
	private DialogInterface.OnClickListener negativeButtonClickListener;  

	public Builder(Context context) {  
		this.context = context;  
	}  
	
	public Builder setTitleIcon(int resId){
		this.iconResId = resId;
		return this;
	}
		
	public Builder setMessage(String message) {  
		this.message = message;  
		return this;  
	}  

     /** 
      * Set the Dialog message from resource 
      *  
      * @param title 
      * @return 
      */  
	public Builder setMessage(int message) {  
		this.message = (String) context.getText(message);  
		return this;  
	}  
	/** 
	 * Set the Dialog title from resource 
	 *  
	 * @param title 
	 * @return 
	 */  
	public Builder setTitle(int title) {  
		this.title = (String) context.getText(title);  
		return this;  
	 }  
	
	/** 
	* Set the Dialog title from String 
	*  
	* @param title 
	* @return 
	 */  

	public Builder setTitle(String title) {  
		this.title = title;  
		return this;  
	}  
 
	public Builder setContentView(View v) {  
		this.contentView = v;  
		return this;  
	}  
 
	/** 
	* Set the positive button resource and it's listener 
	*  
	* @param positiveButtonText 
	* @return 
	*/  
	public Builder setPositiveButton(int positiveButtonText,  
			DialogInterface.OnClickListener listener) {  
			this.positiveButtonText = (String) context  
											.getText(positiveButtonText);  
			this.positiveButtonClickListener = listener;  
			return this;  
		}  

	 public Builder setPositiveButton(String positiveButtonText,  
			 DialogInterface.OnClickListener listener) {  
		     this.positiveButtonText = positiveButtonText;  
		     this.positiveButtonClickListener = listener;  
		     return this;  
	 }  
	 
	 public Builder setNegativeButton(int negativeButtonText,  
			 DialogInterface.OnClickListener listener) {  
		 	this.negativeButtonText = (String) context  
		 							.getText(negativeButtonText);  
		 	this.negativeButtonClickListener = listener;  
		 	return this;  
	 }  
	 
	 public Builder setNegativeButton(String negativeButtonText,  
			 DialogInterface.OnClickListener listener) {  
			this.negativeButtonText = negativeButtonText;  
			this.negativeButtonClickListener = listener;  
			return this;  
			}  

	 public CustomSpeedDialog create() {  
		 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		 // instantiate the dialog with the custom Theme  
		 final CustomSpeedDialog dialog = new CustomSpeedDialog(context,R.style.Dialog);  
		 View layout = inflater.inflate(R.layout.dialog_custom_speed_layout, null); 

		 dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
		 
		 ((TextView) layout.findViewById(R.id.title)).setText(title);  
		 
		 if(iconResId == 0){
			 ((ImageView)layout.findViewById(R.id.dialog_title_img)).setVisibility(View.GONE);
		 }else{
			 ((ImageView)layout.findViewById(R.id.dialog_title_img)).setImageResource(iconResId);
		 }
		 
		 if (positiveButtonText != null) {  
			 ((TextView) layout.findViewById(R.id.positive_btn)).setText(positiveButtonText);  
		 if (null != positiveButtonClickListener ) { 			 
			 ((TextView) layout.findViewById(R.id.positive_btn))
			 				.setOnClickListener(new View.OnClickListener() {  
				 	public void onClick(View v) {  
                              positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);  
		 								}  
			 						});  
		 					}  
		 			  } else {  
		 					// if no confirm button just set the visibility to GONE  
		 					layout.findViewById(R.id.positive_btn).setVisibility(View.GONE);  
		 			  }  
		 					// set the cancel button  
		 if (negativeButtonText != null) {
			 ((TextView) layout.findViewById(R.id.negative_btn)).setText(negativeButtonText);
			 if (negativeButtonClickListener != null) {
				 ((TextView) layout.findViewById(R.id.negative_btn)).setOnClickListener(new View.OnClickListener() {
					 public void onClick(View v) { 
						 negativeButtonClickListener.onClick(dialog,  
		 									DialogInterface.BUTTON_NEGATIVE);  
		 									}  
		 							});  
		 						}  
		 	} else {
		 		// if no confirm button just set the visibility to GONE  
		 		layout.findViewById(R.id.negative_btn).setVisibility(View.GONE);  
		 	}  
		 					// set the content message  
		 	if (null != message) {  
		 		((TextView) layout.findViewById(R.id.message)).setText(message);  
		 	} else if (null != contentView) {
		 		((LinearLayout)layout.findViewById(R.id.content)).removeAllViews();  
				((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));  
		 	} 
		 						// if no message set  
		 						// add the contentView to the dialog body  
		 	dialog.setContentView(layout);  
		 	return dialog;  
	 	}  
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		View view = getCurrentFocus();
        if(view instanceof TextView){
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
 
        super.dismiss();
		
	}
}

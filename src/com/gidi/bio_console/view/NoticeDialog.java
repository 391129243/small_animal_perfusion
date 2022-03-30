package com.gidi.bio_console.view;

import com.gidi.bio_console.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/****
 * 单确认按钮的提示框
 * @author 80657
 *
 */


public class NoticeDialog extends Dialog {

	public NoticeDialog(Context context) {  
		super(context);  
	}  
	public NoticeDialog(Context context, int theme) {  
		super(context, theme);  
	}  

	public static class Builder {  
	private Context context;  
	private String title;  
	private String message;  
	private String singleButtonText;
	private View contentView;  
 
	private DialogInterface.OnClickListener sigleButtonClickListner;

	public Builder(Context context) {  
		this.context = context;  
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
	 public Builder setSingleButton(String singleButtonText,
			 DialogInterface.OnClickListener listener){
		 this.singleButtonText = singleButtonText;
		 this.sigleButtonClickListner = listener;
		 return this;
	 }
	 
	 public Builder setSingleButton(int resId,
			 DialogInterface.OnClickListener listener){
		 this.singleButtonText = (String) context  
					.getText(resId); 
		 this.sigleButtonClickListner = listener;
		 return this;
	 }
	 
	 
	 
	 public NoticeDialog create() {  
		 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		 // instantiate the dialog with the custom Theme  
		 final NoticeDialog dialog = new NoticeDialog(context,R.style.Dialog);  
		 View layout = inflater.inflate(R.layout.dialog_notice_layout, null); 
		 dialog.setCancelable(true);
		 dialog.setCanceledOnTouchOutside(false);
		 dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  		 
		 
		 
		 // set the confirm button  
		 if (singleButtonText != null) {  
			 ((TextView) layout.findViewById(R.id.dialog_single_button)).setText(singleButtonText);  
			 if (null != sigleButtonClickListner ) { 			 
				 ((TextView) layout.findViewById(R.id.dialog_single_button))
				 				.setOnClickListener(new View.OnClickListener() {  
					 	public void onClick(View v) { 
					 		sigleButtonClickListner.onClick(dialog, DialogInterface.BUTTON_POSITIVE);  
			 			}  
				 	});  
			 }  
		} else {  
		 	// if no confirm button just set the visibility to GONE  
		 	layout.findViewById(R.id.dialog_single_button).setVisibility(View.GONE);  
		}  
		// set the title 
		if(title != null){
			 ((TextView) layout.findViewById(R.id.tv_dialog_notice_title)).setText(title); 
		}else{
			((TextView) layout.findViewById(R.id.tv_dialog_notice_title)).setVisibility(View.GONE); 
		}
		 
		 					// set the content message  
	 	if (null != message) {  
	 		((TextView) layout.findViewById(R.id.tv_dialog_message)).setText(message);  
	 	} else if (null != contentView) {
	 		((LinearLayout)layout.findViewById(R.id.li_dialog_content)).removeAllViews();  
			((LinearLayout) layout.findViewById(R.id.li_dialog_content)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));  
	 	} 
	 	// if no message set  
	 	// add the contentView to the dialog body  
	 	dialog.setContentView(layout);  
	 	return dialog;  
	 	}  
	}
}



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

public class WifiNoticeDialog extends Dialog {
	public WifiNoticeDialog(Context context) {  
		super(context);  
	}  
	public WifiNoticeDialog(Context context, int theme) {  
		super(context, theme);  
	}  

	public static class Builder {  
	private Context context;  
	private String title;  
	private String message;  
	private String okBtnText;
	private String cancelBtnText;
	private String forgetBtnText;
	private View contentView;  
 
	private DialogInterface.OnClickListener positiveButtonClickListener;  
	private DialogInterface.OnClickListener negativeButtonClickListener;  
	private DialogInterface.OnClickListener forgetButtonClickListener;

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
	public Builder setConnectButton(int positiveButtonText,  
		DialogInterface.OnClickListener listener) {  
		this.okBtnText = (String) context  .getText(positiveButtonText);  
		this.positiveButtonClickListener = listener;  
		return this;  
	}  

	 public Builder setConnectButton(String positiveButtonText,  
		DialogInterface.OnClickListener listener) {  
		this.okBtnText = positiveButtonText;  
		this.positiveButtonClickListener = listener;  
		return this;  
	 }  
	 
	 public Builder setNegativeButton(int negativeButtonText,  
		 DialogInterface.OnClickListener listener) {  
		 this.cancelBtnText = (String) context  
		 							.getText(negativeButtonText);  
		 this.negativeButtonClickListener = listener;  
		 return this;  
	 }  
	 
	 public Builder setNegativeButton(String negativeButtonText,  
		 DialogInterface.OnClickListener listener) {  
		 this.cancelBtnText = negativeButtonText;  
		 this.negativeButtonClickListener = listener;  
		 return this;  
 	}  
	 
	 public Builder setForgetButton(int forgeteButtonText,  
		DialogInterface.OnClickListener listener) {  
		this.forgetBtnText = (String) context  
			 							.getText(forgeteButtonText);  
		this.forgetButtonClickListener = listener;  
		return this;  
	 }  
		 
	 public Builder setForgetButton(String forgeteButtonText,  
		 DialogInterface.OnClickListener listener) {  
		 this.forgetBtnText = forgeteButtonText;  
		 this.forgetButtonClickListener = listener;  
		 return this;  
	 }  

	 
	 public WifiNoticeDialog create() {  
		 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		 // instantiate the dialog with the custom Theme  
		 final WifiNoticeDialog dialog = new WifiNoticeDialog(context,R.style.Dialog);  
		 View layout = inflater.inflate(R.layout.dialog_wifi_notice_layout, null); 
		 dialog.setCancelable(true);
		 dialog.setCanceledOnTouchOutside(true);
		 dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  		 
		 
		 
		 
		 if (okBtnText != null) {  
			 ((TextView) layout.findViewById(R.id.dialog_ok_button)).setText(okBtnText);  
			 if (null != positiveButtonClickListener ) { 			 
				 ((TextView) layout.findViewById(R.id.dialog_ok_button))
				 				.setOnClickListener(new View.OnClickListener() {  
					 	public void onClick(View v) {  
	                              positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);  
			 								}  
				 						});  
			 					}  
		 } else {  
		 					// if no confirm button just set the visibility to GONE  
			 ((TextView) layout.findViewById(R.id.dialog_ok_button)).setVisibility(View.INVISIBLE);  
		}  
		 					// set the cancel button  
		 if (cancelBtnText != null) {
			 ((TextView) layout.findViewById(R.id.dialog_cancel_button)).setText(cancelBtnText);
			 if (negativeButtonClickListener != null) {
				 ((TextView) layout.findViewById(R.id.dialog_cancel_button)).setOnClickListener(new View.OnClickListener() {
					 public void onClick(View v) { 
						 
						 negativeButtonClickListener.onClick(dialog,  DialogInterface.BUTTON_NEGATIVE);  
		 				 }  
		 			});  
		 		}  
		 }else{
		 		// if no confirm button just set the visibility to GONE  
			 ((TextView)layout.findViewById(R.id.dialog_cancel_button)).setVisibility(View.INVISIBLE);  
		 } 
		 
		 //forget
		 if (forgetBtnText != null) {
			 ((TextView) layout.findViewById(R.id.dialog_forget_button)).setText(forgetBtnText);
			 if (negativeButtonClickListener != null) {
				 ((TextView) layout.findViewById(R.id.dialog_forget_button)).setOnClickListener(new View.OnClickListener() {
					 public void onClick(View v) { 
						 forgetButtonClickListener.onClick(dialog,  DialogInterface.BUTTON_NEUTRAL);  
		 			 }  
		 		 });  
		 	}  
		 }else {			 
	 		 ((TextView) layout.findViewById(R.id.dialog_forget_button)).setVisibility(View.INVISIBLE);  
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

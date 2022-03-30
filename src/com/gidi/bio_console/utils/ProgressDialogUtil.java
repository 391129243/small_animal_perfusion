package com.gidi.bio_console.utils;



import com.gidi.bio_console.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProgressDialogUtil {

	public static Dialog createLoadingDialog(Context context, String msg) {  
		  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.module_dialog_progressbar, null);//得到加载View
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_progress_view); 

        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.dialog_img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.dialog_tip_txt);//提示文字
        // 使用ImageView显示动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        // ʹ  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);//
  
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 
  
        loadingDialog.setCancelable(false);// 
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));//
        return loadingDialog;  
  
    }  
}

package com.gidi.bio_console.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import com.gidi.bio_console.constant.LanguageType;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class CommonUtil {
	

	/***
	 * 获取版本号
	 * versionCode
	 */
	public static int getAppVersionCode(Context context){
		int versionCode = 0;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
			versionCode = pi.versionCode;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return versionCode;
	}
	
	/**
	 * 获取版本名称
	 * versionName
	 */
	public static String getAppVersionName(Context context){
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if(null == versionName || versionName.length()<=0){
				versionName = "";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return versionName;
	}
	
	public static void changeAppLanguage(Context context,String newLanguage){
		if (TextUtils.isEmpty(newLanguage)) {
	        return;
	    }
	    Resources resources = context.getResources();
	    //获取配置对象
	    Configuration configuration = resources.getConfiguration();
	    Locale locale = getLocaleByLanguage(newLanguage);
	    
	    //获取想要切换的语言类型	    
	    configuration.setLocale(locale);
	    // updateConfiguration
	    DisplayMetrics dm = resources.getDisplayMetrics();
	    resources.updateConfiguration(configuration, dm);
	}

	public static Locale getLocaleByLanguage(String language) {
	    Locale locale = Locale.SIMPLIFIED_CHINESE;
	    if (language.equals(LanguageType.CHINESE.getLanguage())) {
	        locale = Locale.SIMPLIFIED_CHINESE;
	    } else if (language.equals(LanguageType.ENGLISH.getLanguage())) {
	        locale = Locale.ENGLISH;
	    } 
	    return locale;
	}
	
	   //判断是否是字母
    public static boolean isLetter(String str) {
        String wordStr = "abcdefghijklmnopqrstuvwxyz";
        return wordStr.contains(str.toLowerCase());
    }


    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示,并且能正常选取光标
     */
    public static void disableShowSoftInput(EditText editText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 显示键盘
     * */
    public static void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                view.requestFocus();
                imm.showSoftInput(view, 0);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Context context) {
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    /**
     * 获取实际内容高度
     *
     * @param context
     * @return
     */
    public static int getContentHeight(Context context) {
        int screenh_nonavbar = 0;
        DisplayMetrics dMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            display.getMetrics(dMetrics);
            screenh_nonavbar = dMetrics.heightPixels;

            int ver = Build.VERSION.SDK_INT;

            // 新版本的android 系统有导航栏，造成无法正确获取高度
            if (ver == 13) {
                try {
                    Method mt = display.getClass().getMethod("getRealHeight");
                    screenh_nonavbar = (Integer) mt.invoke(display);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ver > 13) {
//                try {
//                    Method mt = display.getClass().getMethod("getRawHeight");
//                    screenh_nonavbar = (Integer) mt.invoke(display);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        return screenh_nonavbar - getStatusBarHeight(context);
    }
    
    /**
     * 电量栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0,
                sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return sbar;
    }

    public static void checkNull(Object object, String info) {
        if (object == null) {
            throw new NullPointerException(info);
        }
    }
}

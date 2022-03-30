package com.gidi.bio_console.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.gidi.bio_console.R;
import com.gidi.bio_console.utils.CommonUtil;

/**
 * Created by huangjie on 2018/2/5.
 * 类名：能弹出自定义键盘的EditText抽象类
 */
public abstract class KeyBoardEditText extends EditText {
    private Activity activity;
    private int realHeight; //界面实际高度
    private PopupWindow mKeyboardWindow;
    private View mDecorView;
    private View mContentView;
    private int mDifference;


    public KeyBoardEditText(Context context) {
        this(context, null);
    }

    public KeyBoardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyBoardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context instanceof Activity) activity = (Activity) context;
        realHeight = CommonUtil.getContentHeight(context);
        this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        if (this.getText() != null) {
            this.setSelection(this.getText().length());
        }

    }

    /**
     * 初始化popindow
     * @param contentView layout
     */
    protected void initPopWindow(View contentView) {
        //mKeyboardWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyboardWindow = new PopupWindow(contentView, 300, ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyboardWindow.setAnimationStyle(R.style.AnimationFade);

        mKeyboardWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mKeyboardWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mDifference > 0) {
                    int temp = mDifference;
                    mDifference = 0;
                    if (null != mContentView) {
                        mContentView.scrollBy(0, -temp);
                    }
                }
            }
        });
    }

    protected void initPopWindow(View contentView,float x,float y) {
        //mKeyboardWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyboardWindow = new PopupWindow(contentView, 300, ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyboardWindow.setAnimationStyle(R.style.AnimationFade);

        mKeyboardWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mKeyboardWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mDifference > 0) {
                    int temp = mDifference;
                    mDifference = 0;
                    if (null != mContentView) {
                        mContentView.scrollBy(0, -temp);
                    }
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != activity) {
            Window mWindow = activity.getWindow();
            //mDecorView = mWindow.getDecorView();
            mDecorView = mWindow.getDecorView();
            mContentView = mDecorView.findViewById(Window.ID_ANDROID_CONTENT);
        }
        CommonUtil.disableShowSoftInput(this);
    }
    /**
     * 打开键盘
     */
    protected void showKeyboardWindow(View rootView) {
        if (null != mKeyboardWindow) {
            if (!mKeyboardWindow.isShowing()) {
                int[] location = {0,0};
                this.getLocationInWindow(location);
                //float y = this.getY();
                //键盘显示的位置
                if(rootView == null){
                	rootView = this.mDecorView;
                }
                mKeyboardWindow.showAtLocation(rootView , Gravity.TOP | Gravity.LEFT, 
                		location[0], location[1]+this.getHeight());
                if (null != mDecorView && null != mContentView) {
                    final View popContentView = mKeyboardWindow.getContentView();
                    popContentView.post(new Runnable() {
                        @Override
                        public void run() {
                            int[] pos = new int[2];
                            getLocationOnScreen(pos);
                            float height = popContentView.getMeasuredHeight();
                            Rect outRect = new Rect();
                            mDecorView.getWindowVisibleDisplayFrame(outRect);
                            int screen = realHeight;
                            mDifference = (int) ((pos[1] + getMeasuredHeight() - outRect.top) - (screen - height));
                            if (mDifference > 0) {
                                mContentView.scrollBy(0, mDifference);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 打开键盘
     */
    protected void showKeyboardWindow() {
        if (null != mKeyboardWindow) {
            if (!mKeyboardWindow.isShowing()) {
                int[] location = {0,0};
                this.getLocationOnScreen(location);
                //float y = this.getY();
                //键盘显示的位置
                mKeyboardWindow.showAtLocation(this.mDecorView, Gravity.TOP | Gravity.LEFT, 
                		location[0] + this.getWidth(), location[1]+this.getHeight());
                if (null != mDecorView && null != mContentView) {
                    final View popContentView = mKeyboardWindow.getContentView();
                    popContentView.post(new Runnable() {
                        @Override
                        public void run() {
                            int[] pos = new int[2];
                            getLocationOnScreen(pos);
                            float height = popContentView.getMeasuredHeight();
                            Rect outRect = new Rect();
                            mDecorView.getWindowVisibleDisplayFrame(outRect);
                            int screen = realHeight;
                            mDifference = (int) ((pos[1] + getMeasuredHeight() - outRect.top) - (screen - height));
                            if (mDifference > 0) {
                                mContentView.scrollBy(0, mDifference);
                            }
                        }
                    });
                }
            }
        }
    }

    
    /**
     * 关闭键盘
     */
    protected void dismissKeyboardWindow() {
        if (null != mKeyboardWindow) {
            if (mKeyboardWindow.isShowing()) {
                mKeyboardWindow.dismiss();
                clearFocus();
            }
        }
    }

    /**
     * 判断键盘是否打开
     * @return isShowing
     */
    protected boolean isShowing() {
        boolean isShowing = false;
        if (null != mKeyboardWindow) {
            isShowing = mKeyboardWindow.isShowing();
        }
        return isShowing;
    }

    /**
     * 获取popwindow对象
     * @return mKeyboardWindow
     */
    public PopupWindow getKeyboardWindow() {
        return mKeyboardWindow;
    }


    /**
     * 屏蔽EditText长按复制功能,启用后粘贴功能也会失效
     */
    public void removeCopyAndPaste() {
        this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        setLongClickable(false);
    }
}

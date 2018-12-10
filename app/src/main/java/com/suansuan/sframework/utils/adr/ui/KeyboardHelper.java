package com.suansuan.sframework.utils.adr.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by wzx on 2018/8/9.
 */
@SuppressWarnings("all")
public class KeyboardHelper {
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout content;
    private FrameLayout.LayoutParams frameLayoutParams;

    KeyboardHelper(Activity activity) {
        content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = content.getHeight() - content.getPaddingTop();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            //界面的高度变化超过1/4的屏幕高度 才会进行重新设置高度
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 键盘可能展示
                frameLayoutParams.height = usableHeightNow;
            } else {
                // 键盘可能隐藏
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
}

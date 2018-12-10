/*
 * Copyright (C) 2013 Qunar.Inc All rights reserved.
 */
package com.suansuan.sframework.utils.adr.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.suansuan.sframework.utils.java.CheckUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewSetter {

    private static final boolean DEBUG = true;

    public static ViewSetting getSetting(View view) {

        if (view == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("view must be not null...");
            } else {
                return new FakeProxy();
            }
        }
        Class<?> clazz = view.getClass();
        while (clazz != null) {
            CommonViewProxy<View> setting = null;
            if(clazz == View.class) {
                setting = new ViewProxy<View>();
            } else if(clazz == TextView.class) {
                setting = new TextViewProxy();
            } else if(clazz == ImageView.class) {
                setting = new ImageViewProxy();
            }
            if (setting == null) {
                clazz = clazz.getSuperclass();
            } else {
                setting.init(view);
                return setting;
            }
        }
        if (DEBUG) {
            throw new IllegalArgumentException("can't get view setting");
        } else {
            return new FakeProxy();
        }
    }

    public interface ViewSetting {
        boolean setOr(Method method, boolean condition, int state, Object... params);
    }

    public enum Method {
        Text, Src, Background, NotCare;
    }

    public static abstract class CommonViewProxy<T extends View> implements ViewSetting {
        protected T mView;
        public void init(T view) {
            this.mView = view;
        }

        @Override
        public final boolean setOr(Method method, boolean condition, int state, Object... params) {
            if (!condition) {
                // 不满足条件
                return gone(state);
            }
            if (method == Method.NotCare) {
                // 只关心condition
                return visible();
            }
            if (CheckUtils.isContainsEmpty(params)) {
                return gone(state);
            }
            List<Method> methods = new ArrayList<Method>();
            get(methods);
            for (Method ori : methods) {
                if (ori == method) {
                    return set(method, state, params);
                }
            }
            throw ex(mView, params[0]);
        }

        abstract protected boolean set(Method method, int state, Object... params);
        abstract protected void get(List<Method> methods);

        protected boolean gone(int state) {
            mView.setVisibility(state);
            return false;
        }

        protected boolean visible() {
            mView.setVisibility(View.VISIBLE);
            return true;
        }

        protected RuntimeException ex(View v, Object param) {
            return new RuntimeException("can't handle... view " + v.getClass().getSimpleName() + " data :" + param.toString());
        }
    }

    public static class FakeProxy implements ViewSetting {

        @Override
        public boolean setOr(Method method, boolean condition, int state, Object... params) {
            return false;
        }

    }

    public static class ViewProxy<T extends View> extends CommonViewProxy<View> {

        protected T getView() {
            return (T) mView;
        }

        @Override
        protected boolean set(Method method, int state, Object... params) {
            if (method == Method.Background) {
                setBackground(mView, params[0]);
                return visible();
            }
            throw ex(mView, params[0]);
        }

        private void setBackground(View v, Object param) {
            if (param instanceof Drawable) {
                v.setBackgroundDrawable((Drawable) param);
            } else if (param instanceof Integer) {
                v.setBackgroundResource((Integer) param);
            } else {
                throw ex(v, param);
            }
        }

        @Override
        protected void get(List<Method> methods) {
            methods.add(Method.Background);
        }

    }

    public static class TextViewProxy extends ViewProxy<TextView> {

        @Override
        protected boolean set(Method method, int state, Object... params) {
            if (method == Method.Text) {
                setViewText(getView(), params);
                return visible();
            }
            return super.set(method, state, params);
        }

        @Override
        protected void get(List<Method> methods) {
            methods.add(Method.Text);
        }

        private void setViewText(TextView view, Object... obj) {

            Object data;

            if (obj.length != 1) {
                data = ViewUtils.joinNotAllowedNull(obj);
            } else {
                data = obj[0];
            }

            if (data instanceof CharSequence) {
                view.setText((CharSequence) data);
            } else if (data instanceof Integer) {
                view.setText((Integer) data);
            } else {
                throw ex(getView(), obj);
            }

        }
    }

    public static class ImageViewProxy extends ViewProxy<ImageView> {

        @Override
        protected boolean set(Method method, int state, Object... params) {
            if (method == Method.Src) {
                setViewImage(getView(), params[0]);
                return visible();
            }
            return super.set(method, state, params);
        }

        @Override
        protected void get(List<Method> methods) {
            super.get(methods);
            methods.add(Method.Src);
        }

        private void setViewImage(ImageView view, Object obj) {
            if (obj instanceof Bitmap) {
                view.setImageBitmap((Bitmap) obj);
            } else if (obj instanceof Drawable) {
                view.setImageDrawable((Drawable) obj);
            } else if (obj instanceof Integer) {
                view.setImageResource((Integer) obj);
            } else {
                throw ex(view, obj);
            }
        }
    }
}

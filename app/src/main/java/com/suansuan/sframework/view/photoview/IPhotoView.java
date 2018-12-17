package com.suansuan.sframework.view.photoview;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;


/**
 * 规定标准，规定最终给外暴露的PhotoView的接口
 */
@SuppressWarnings("all")
public interface IPhotoView {


    boolean canZoom();

    void setZoomable(boolean zommable);



    RectF getDisplayRect();


    boolean setDisplayMatrix(Matrix fianlMatrix);

    Matrix getDisplayMatrix();



    float getScale();

    void setScale(float scale);

    void setScale(float scale, boolean animate);

    void setScale(float scale, float focalX, float focalY, boolean animate);


    void setPhotoViewRotation(float rotationDegree);

    void setOnLongClickListener(View.OnClickListener listener);




    ImageView.ScaleType getScaleType();

    void setScaleType(ImageView.ScaleType scaleType);




}

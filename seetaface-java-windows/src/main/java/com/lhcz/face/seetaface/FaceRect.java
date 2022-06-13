package com.lhcz.face.seetaface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  人脸位置
 */
public class FaceRect implements Serializable {
    public static final long serialVersionUID=1L;
    private float x;
    private float y;
    private float width;
    private float height;

    protected FaceRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    protected static List<FaceRect> covertFromJnaResult(int faceNum,float[] rect){
        List<FaceRect> rects = new ArrayList<FaceRect>();
        for(int i=0;i<faceNum;i++){
            int index = i*4;
            FaceRect r = new FaceRect(rect[index],rect[index+1],rect[index+2],rect[index+3]);
            rects.add(r);
        }
        return rects;
    }

    @Override
    public String toString() {
        return "{" +
                "x:" + x +
                ", y:" + y +
                ", width:" + width +
                ", height:" + height +
                '}';
    }
}

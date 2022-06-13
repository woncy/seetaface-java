package com.lhcz.face.seetaface;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 人脸定位点
 */
public class FacePoints implements Serializable {
    public static final long serialVersionUID=1L;
    /**
     * 人脸位置
     */
    FaceRect rect;

    /**
     * 定位点
     */
    List<PointF> points;

    protected FacePoints(){

    }

    public FaceRect getRect() {
        return rect;
    }

    public List<PointF> getPoints() {
        return points;
    }



    protected static List<FacePoints> covertFromJnaResult(int faceNum,float[] rectResult,float[] pointResult){
        List<FacePoints> points = new ArrayList<FacePoints>();
        for(int i=0;i<faceNum;i++){
            int index = i*4;
            FaceRect r = new FaceRect(rectResult[index],rectResult[index+1],rectResult[index+2],rectResult[index+3]);

            List<PointF> pointList = new ArrayList<>();
            int pointIndex = i*10;
            for(int j=pointIndex;j<pointIndex+10;j+=2){
                PointF p = new PointF(pointResult[j],pointResult[j+1]);
                pointList.add(p);
            }
            FacePoints fp = new FacePoints();
            fp.points=pointList;
            fp.rect=r;
            points.add(fp);

        }
        return points;
    }

    @Override
    public String toString() {
        return "{" +
                "rect:" + rect +
                ", points:" + points +
                '}';
    }
}

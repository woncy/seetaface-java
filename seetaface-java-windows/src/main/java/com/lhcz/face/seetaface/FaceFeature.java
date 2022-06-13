package com.lhcz.face.seetaface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 人脸特征
 */
public class FaceFeature implements Serializable {


    FacePoints points;
    float[] featrue;
    protected   FaceFeature(){

    }


    public FacePoints getPoints() {
        return points;
    }


    public float[] getFeatrue() {
        return featrue;
    }

    public static List<FaceFeature> convertFromJnaResult(int faceNum,float[] rect,float[] featrues,float[] points){
        List<FaceFeature> faceFeatures = new ArrayList<>();
        for(int i=0;i<faceNum;i++){
            int index = i*4;
            FaceRect r = new FaceRect(rect[index],rect[index+1],rect[index+2],rect[index+3]);

            List<PointF> pointList = new ArrayList<>();
            int pointIndex = i*10;
            for(int j=pointIndex;j<pointIndex+10;j+=2){
                PointF p = new PointF(points[j],points[j+1]);
                pointList.add(p);
            }
            FacePoints fp = new FacePoints();
            fp.points=pointList;
            fp.rect=r;
            float[] feature=new float[1024];
            System.arraycopy(featrues,i*1024,feature,0,1024);
            FaceFeature faceFeature = new FaceFeature();
            faceFeature.featrue=feature;
            faceFeature.points=fp;
            faceFeatures.add(faceFeature);
        }
        return faceFeatures;
    }


    public byte[] featureToByteArray(){
        byte[] bytes = new byte[4096];
        floatFeatureToByteArr(this.featrue,bytes,0);
        return bytes;
    }


    private static void floatFeatureToByteArr(float[] floats,byte[] featrue,int faceIndex){
        for (int i=faceIndex*1024,b=0;i<(faceIndex+1)*1024;i++,b+=4){
            floatArrToByteArr(floats,featrue,i,b);
        }
    }


    protected static void floatArrToByteArr(float[] floats,byte[] bytes,int findex,int bIndex){
        float f = floats[findex];
        int b = Float.floatToIntBits(f);
        bytes[bIndex] = (byte) (b & 0xff);
        bytes[bIndex+1] = (byte) ((b & 0xff00)>>>8);
        bytes[bIndex+2] = (byte) ((b & 0xff0000)>>>16);
        bytes[bIndex+3] = (byte) ((b & 0xff000000)>>>24);
    }

    /**
     * 将四个字节的数组转为int;
     *
     * @param b
     * @return
     */
    private static int bytesToInt(int... b) {
        return b[0] & 0xFF | (b[1] & 0xFF) << 8 | (b[2] & 0xFF) << 16 | (b[3] & 0xFF) << 24;
    }

    protected static void byteArrToFloatArr(float[] floats,byte[] bytes,int findex,int bIndex){
        floats[findex]=Float.intBitsToFloat(
                    bytesToInt(bytes[bIndex],bytes[bIndex+1],bytes[bIndex+2],bytes[bIndex+3])
        );
    }
    @Override
    public String toString() {
        return "{" +
                "points:" + points +
                ", featrue:" + Arrays.toString(featrue) +
                '}';
    }
}

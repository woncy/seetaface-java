package com.lhcz.face.seetaface;

import com.sun.jna.Library;
import com.sun.jna.WString;

interface Jna extends Library {

    long initFaceDetector(int cpuType, String modelPath,int gpuId);
    long initFaceDetectorForWindows(int cpuType, WString modelPath, int gpuId);

    long initFaceLandmarker(int cpuType, String modelPath,int gpuId);
    long initFaceLandmarkerForWindows(int cpuType, WString modelPath,int gpuId);


    long initFaceRecognizer(int cpuType, String modelPath,int gpuId);
    long initFaceRecognizerForWindows(int cpuType, WString modelPath,int gpuId);

    void releaseFaceLandmarker(long handle);

    void releaseFaceDetector(long handle);

    void releaseFaceRecognizer(long handle);


    /**
     * 根据byte[]检测人脸
     */
    // void faceDetectByByteArray(long hande,BYTE * bytes, int length,int & faceNum,float * result,int maxFaceNum);
    void faceDetectByByteArray(long hande,int width,int height,int channels,byte [] bytes, int length,int[] faceNum,float[] result,int maxFaceNum);



    /**
     * 根据byte[]获取人脸定位
     */
    void faceMarkByByteArray(long faceDetectorHandle, long faceLandmarkerHandle,int width,int height,int channels, byte[] bytes, int length, int [] faceNum, float [] result,float []points, int maxFaceNum);


    /**
     * 根据图片数据提取特征
     */
    void faceRecognizerByByteArray(long faceDetectorHandle, long faceLandmarkerHandle, long faceRecognizerHanle,int width,int height,int channels, byte [] bytes, int length, int[] faceNum, int maxFaceNum, float [] faceDetectResult, float [] features, float[] pointResults);

    /**
     * 比对两个特征的相似度
     */
    float calculateSimilarity(long faceRecognizerHanle,float [] feature1,float [] feature2);
}

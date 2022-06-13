package com.lhcz.face.seetaface;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 人脸比对，人脸特征提取
 */
public final class FaceRecognizer {
    public static final Logger log = LoggerFactory.getLogger(FaceRecognizer.class);
    private long handle;
    private long threadId;
    private FaceDetector detector;
    private FaceLandmarker landmarker;
    private Device deviceType;
    private Jna jna;
    private boolean release;

    public FaceRecognizer() throws FaceException {
        this(Device.AUTO);
    }
    public FaceRecognizer(Device deviceType) throws FaceException {
        this(deviceType,0);
    }

    public FaceRecognizer(Device deviceType,int gpuId) throws FaceException {
        if(deviceType==Device.GPU){
            throw new FaceException("暂不支持GPU");
        }
        this.deviceType = deviceType;
        this.deviceType = deviceType;
        jna=JnaFactory.getInstance();
        final String MODEL=Resource.modelRoot+"/face_recognizer.csta\0";
        if(Platform.isWindows()){
            handle=jna.initFaceRecognizerForWindows(deviceType.getType(),WindowsUtil.toWChar(MODEL),gpuId);
        }else{
            handle = jna.initFaceRecognizer(deviceType.getType(),MODEL,gpuId);
        }
        if(handle<=0){
            throw new FaceException(ErrorCode.getByCode((int)handle));
        }
        detector=new FaceDetector(deviceType,gpuId);
        landmarker=new FaceLandmarker(detector,deviceType,gpuId);
        threadId=Thread.currentThread().getId();
        log.debug("初始化:{},thread:{}","FaceRecognizer",threadId);
    }

    private boolean checkThread(){
        long tId = Thread.currentThread().getId();
        if(this.threadId!=tId){
            return false;
        }
        return true;
    }

     void feature(long faceDetectorHandle, long faceLandmarkerHandle, long faceRecognizerHanle,FaceImageData data, int[] faceNum, int maxFaceNum, float [] faceDetectResult, float [] features, float[] pointResults){
        jna.faceRecognizerByByteArray(faceDetectorHandle,faceLandmarkerHandle,faceRecognizerHanle,data.getWidth(),data.getHeight(),data.getChannels(),data.getData(),data.getData().length,faceNum,maxFaceNum,faceDetectResult,features,pointResults);
    }
    void feature(long faceDetectorHandle, long faceLandmarkerHandle, long faceRecognizerHanle,BufferedImage data, int[] faceNum, int maxFaceNum, float [] faceDetectResult, float [] features, float[] pointResults){
        feature(faceDetectorHandle,faceLandmarkerHandle,faceRecognizerHanle,FaceImageData.fromBufferedImage(data),faceNum,maxFaceNum,faceDetectResult,features,pointResults);
    }


    /**
     *
     * @param img 图片
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceFeature> feature(BufferedImage img, int maxFaceNum) throws FaceException {
        if(!checkThread()){
            throw new FaceException("不可跨线程使用人脸检测");
        }
        if(maxFaceNum<0||maxFaceNum>FaceDetector.RangeMaxFaceNum){
            throw new FaceException("检测人脸数量超出范围");
        }
        if(img==null){
            throw new FaceException("图像数据非法");
        }
        float rect[] = new float[maxFaceNum*4];

        int faceNumAddr[] = new int[1];
        float points[] = new float[maxFaceNum*10];
        float features[] = new float[1024*maxFaceNum];
        feature(detector.getHandle(),landmarker.getHandle(),this.handle,img,faceNumAddr,maxFaceNum,rect,features,points);
        int faceNum=faceNumAddr[0];
        if(faceNum<0){
            throw new FaceException(ErrorCode.getByCode(faceNum));
        }else if(faceNum>0){
            return FaceFeature.convertFromJnaResult(faceNum,rect,features,points);
        }else {
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param img 图片数据
     * @return
     */
    public List<FaceFeature> feature(BufferedImage img) throws FaceException {
        return feature(img,FaceDetector.DefaultMaxFaceNum);
    }


    /**
     *
     * @param imgData 图片原始数据
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceFeature> feature(InputStream imgData, int maxFaceNum) throws FaceException {
        if(imgData==null){
            throw new FaceException("数据不能未空");
        }
        BufferedImage read =null;
        try {
            read = ImageIO.read(imgData);
        } catch (IOException e) {
            throw new FaceException(e);
        }

        List<FaceFeature> feature = feature(read, maxFaceNum);
        return feature;
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceFeature> feature(InputStream imgData) throws FaceException {
        return feature(imgData,FaceDetector.DefaultMaxFaceNum);
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceFeature> feature(byte[] imgData,int maxFaceNum) throws FaceException {
        if(imgData==null){
            throw new FaceException("数据不能为空");
        }
        return feature(new ByteArrayInputStream(imgData),maxFaceNum);
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceFeature> feature(byte[] imgData) throws FaceException {
        return feature(imgData,FaceDetector.DefaultMaxFaceNum);
    }


    /**
     *
     * @param path 图片地址
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceFeature> feature(String path,int maxFaceNum) throws FaceException {
        if(path==null){
            throw new FaceException("路径不能为空");
        }
        FileInputStream imgData = null;
        try {
            imgData = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new FaceException("路径错误:"+path);
        }

        return feature(imgData,maxFaceNum);
    }

    /**
     *
     * @param path 图片地址
     * @return
     */
    public List<FaceFeature> feature(String path) throws FaceException {
        return feature(path,FaceDetector.DefaultMaxFaceNum);
    }

    public static float similarityToRel(float a) {
        if (a >= 1.0f ) // 保证相同照片结果为1.0f
        {
            return 1.0f;
        } else if (a > 0.5f) {
            return (float) (0.5f + 0.5f * Math.tanh(10 * a - 5));
        } else {
            return (float) (0.5f + 0.5f * Math.tanh(5 * a - 2.5));
        }
    }

    /**
     * 检测相似度-java算法
     * @param feature1
     * @param feature2
     * @return
     */
    public static float similarityJava(float[] feature1,float[] feature2){
        float AModel=0,BModel=0,CModel=0;
        for(int i=0;i<1024;i+=4) {
            float temp = feature1[i]*feature1[i];
            float tempb=feature2[i]*feature2[i];
            float tempc=feature1[i]*feature2[i];
            AModel+=temp;
            BModel+=tempb;
            CModel+=tempc;
        }
        return  (float) (CModel /  Math.sqrt( Math.abs(AModel * BModel)));
    }

    /**
     * 检测相似度-java算法
     * @param feature1
     * @param feature2
     * @return
     */
    public static float similarityJava(byte[] feature1,byte[] feature2){
        float AModel=0,BModel=0,CModel=0;
        float a=0,b=0;
        int i1,i2,i3;
        for(int i=0;i<4096;i+=4) {
            i1=i+1;i2=i+2;i3=i+3;
            a =(Float.intBitsToFloat( bytesToInt(feature1[i], feature1[i1], feature1[i2], feature1[i3])));
            b =(Float.intBitsToFloat(bytesToInt(feature2[i], feature2[i1], feature2[i2], feature2[i3])));
            float temp = a*a;
            float tempb=b*b;
            float tempc=a*b;
            AModel = (temp+AModel);
            BModel = (tempb+BModel);
            CModel = (tempc+CModel);

        }
        return  (float) (CModel / Math.sqrt( Math.abs(AModel * BModel)) );
    }

    /**
     * 检测相似度-java算法
     * @param feature1
     * @param feature2
     * @return
     */
    public static float similarityJava(FaceFeature feature1,FaceFeature feature2){
        return similarityJava(feature1.featrue,feature2.featrue);
    }


    /**
     * 检测相似度-C算法
     * @param feature1
     * @param feature2
     * @return
     */
    public float similarityC(byte[] feature1,byte[] feature2){
        float ffeature1[] = new float[1024];
        float ffeature2[] = new float[1024];
        for (int i=0,b=0;i<ffeature1.length;i++,b+=4){
            FaceFeature.byteArrToFloatArr(ffeature1,feature1,i,b);
            FaceFeature.byteArrToFloatArr(ffeature2,feature2,i,b);
        }
        return similarityC(ffeature1,ffeature2);

    }

    /**
     * 检测相似度-C算法
     * @param feature1
     * @param feature2
     * @return
     */
    public float similarityC(float[] feature1,float[] feature2){
        return jna.calculateSimilarity(this.handle,feature1,feature2);
    }

    /**
     * 检测相似度-C算法
     * @param feature1
     * @param feature2
     * @return
     */
    public float similarityC(FaceFeature feature1,FaceFeature feature2){
        return similarityC(feature1.featrue,feature2.featrue);
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


    protected long getHandle() {
        return handle;
    }

    public long getThreadId() {
        return threadId;
    }

    public FaceDetector getDetector() {
        return detector;
    }

    public FaceLandmarker getLandmarker() {
        return landmarker;
    }

    public Device getDeviceType() {
        return deviceType;
    }

    @Override
    protected void finalize() throws Throwable {
        if(!release) {
            this.jna.releaseFaceRecognizer(this.handle);
            log.debug("释放:{}-{}", "FaceRecognizer", threadId);
            release=true;
        }
        this.landmarker.finalize();
        this.detector.finalize();
        super.finalize();
    }
}

package com.lhcz.face.seetaface;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class FaceLandmarker {
    public static final Logger log = LoggerFactory.getLogger(FaceLandmarker.class);
    private long handle;
    private long threadId;
    private Device deviceType;
    private Jna jna;
    private FaceDetector detector;
    private boolean release=false;

    public FaceLandmarker() throws FaceException {
        this(Device.AUTO);
    }

    public FaceLandmarker(Device deviceType) throws FaceException {
        this(deviceType,0);
    }

    public FaceLandmarker(Device deviceType, int gpuId) throws FaceException {
        this(new FaceDetector(deviceType,gpuId),deviceType,gpuId);
    }

    protected FaceLandmarker(FaceDetector detector,Device deviceType, int gpuId) throws FaceException {
        if(deviceType==Device.GPU){
            throw new FaceException("暂不支持GPU");
        }
        this.detector=detector;
        this.deviceType = deviceType;
        jna=JnaFactory.getInstance();
        final String MODEL=Resource.modelRoot+"/face_landmarker_pts5.csta\0";
        if(Platform.isWindows()){
            handle=jna.initFaceLandmarkerForWindows(deviceType.getType(),WindowsUtil.toWChar(MODEL),gpuId);
        }else {
            handle = jna.initFaceLandmarker(deviceType.getType(), MODEL, gpuId);
        }
        if(handle<=0){
            throw new FaceException(ErrorCode.getByCode((int)handle));
        }
        threadId=Thread.currentThread().getId();
        log.debug("初始化:{},thread:{}","FaceLandmarker",threadId);
    }

    protected long getHandle() {
        return handle;
    }

    public long getThreadId() {
        return threadId;
    }

    public Device getDeviceType() {
        return deviceType;
    }

    private boolean checkThread(){
        long tId = Thread.currentThread().getId();
        if(this.threadId!=tId){
            return false;
        }
        return true;
    }

    void mark(long faceDetectorHandle, long faceLandmarkerHandle,FaceImageData data, int [] faceNum, float [] result,float []points, int maxFaceNum){
        jna.faceMarkByByteArray(faceDetectorHandle, faceLandmarkerHandle,data.getWidth(),data.getHeight(),data.channels,data.getData(),data.getData().length, faceNum,  result,points, maxFaceNum);
    }
    void mark(long faceDetectorHandle, long faceLandmarkerHandle,BufferedImage data, int [] faceNum, float [] result,float []points, int maxFaceNum){
        mark(faceDetectorHandle, faceLandmarkerHandle,FaceImageData.fromBufferedImage(data),faceNum,  result,points, maxFaceNum);
    }


    /**
     *
     * @param path 图像地址
     * @param maxFaceNum 一张图片最大允许提取多少个人脸
     * @return
     */
    public List<FacePoints> mark(String path,int maxFaceNum) throws FaceException, IOException {
        return mark(ImageIO.read(new File(path)),maxFaceNum);

    }

    /**
     *
     * @param imgData 图像数据
     * @param maxFaceNum 一张图片最大允许提取多少个人脸
     * @return
     */
    public List<FacePoints> mark(InputStream imgData, int maxFaceNum) throws FaceException, IOException {
        return mark(ImageIO.read(imgData),maxFaceNum);
    }

    /**
     * @param img 图片
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FacePoints> mark(BufferedImage img, int maxFaceNum) throws FaceException {
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

        mark(this.detector.getHandle(),this.handle,img,faceNumAddr,rect,points,maxFaceNum);
        int faceNum=faceNumAddr[0];
        if(faceNum<0){
            throw new FaceException(ErrorCode.getByCode(faceNum));
        }else if(faceNum>0){
            return FacePoints.covertFromJnaResult(faceNum,rect,points);
        }else {
            return new ArrayList<FacePoints>();
        }
    }

    /**
     * @param imgData 图像数据
     * @return
     */
    public List<FacePoints> mark(byte[] imgData) throws FaceException, IOException {
        return mark(new ByteArrayInputStream(imgData),FaceDetector.DefaultMaxFaceNum);
    }

    /**
     * @param imgData 图像数据
     * @return
     */
    public List<FacePoints> mark(InputStream imgData) throws FaceException, IOException {
        return mark(imgData,FaceDetector.DefaultMaxFaceNum);
    }

    /**
     * @param imgData 图像数据
     * @return
     */
    public List<FacePoints> mark(BufferedImage imgData) throws FaceException {
        return mark(imgData,FaceDetector.DefaultMaxFaceNum);
    }


    /**
     * @param path 图像地址
     * @return
     */
    public List<FacePoints> mark(String path) throws FaceException, IOException {
        return mark(path,FaceDetector.DefaultMaxFaceNum);
    }

    /**
     * 释放资源
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if(!this.release){
            this.jna.releaseFaceLandmarker(this.handle);
            log.debug("释放:{}-{}","FaceLandmarker",threadId);
            this.release=true;
        }
        this.detector.finalize();
        super.finalize();
    }
}

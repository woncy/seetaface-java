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
 * 人脸检测
 */
public final class FaceDetector {
    public static final Logger log = LoggerFactory.getLogger(FaceDetector.class);

    /**
     * 一张图片默认提取20个人脸
     */
    static final int DefaultMaxFaceNum=20;

    /**
     * 一张图片最大允许提取人脸100个
     */
    static final int RangeMaxFaceNum=100;

    private Jna jna;
    private Device deviceType;
    private long handle;
    private long threadId=-1;
    private boolean release=false;
    public FaceDetector() throws FaceException {
        this(Device.AUTO);
    }
    public FaceDetector(Device deviceType) throws FaceException {
       this(deviceType,0);
    }

    public FaceDetector(Device deviceType,int gpuId) throws FaceException {
        this.deviceType = deviceType;
        jna=JnaFactory.getInstance();
        String MODEL=Resource.modelRoot+"/face_detector.csta\0";
        if(Platform.isWindows()){
            handle=jna.initFaceDetectorForWindows(deviceType.getType(),WindowsUtil.toWChar(MODEL),gpuId);
        }else{
            handle = jna.initFaceDetector(deviceType.getType(),MODEL,gpuId);
        }
        if(handle<=0){
            throw new FaceException(ErrorCode.getByCode((int)handle));
        }
        threadId=Thread.currentThread().getId();
        log.debug("初始化:{},thread:{}","FaceDetector",threadId);
    }

    private boolean checkThread(){
        long tId = Thread.currentThread().getId();
        if(this.threadId!=tId){
            return false;
        }
        return true;
    }



    void detect(long hande,FaceImageData data,int[] faceNum,float[] result,int maxFaceNum){
        jna.faceDetectByByteArray(hande,data.getWidth(),data.getHeight(),data.getChannels(),data.getData(),data.getData().length,faceNum,result,maxFaceNum);
    }
    void detect(long hande, BufferedImage image, int[] faceNum, float[] result, int maxFaceNum){
        detect(hande,FaceImageData.fromBufferedImage(image),faceNum,result,maxFaceNum);
    }
    /**
     *
     * @param imgData 图片原始数据
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceRect> detect(InputStream imgData, int maxFaceNum) throws FaceException {
        if(imgData==null){
            throw new FaceException("数据不能为空");
        }
        BufferedImage read = null;
        try {
            read = ImageIO.read(imgData);
        } catch (IOException e) {
            throw new FaceException(e);
        }finally {
            IOUtil.close(imgData);
        }
        return detect(read,maxFaceNum);
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceRect> detect(InputStream imgData) throws FaceException {
        return detect(imgData,DefaultMaxFaceNum);
    }

    /**
     *
     * @param img 图片
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceRect> detect(BufferedImage img, int maxFaceNum) throws FaceException {
        if(img==null){
            throw new FaceException("图像数据不能为空");
        }
        if(!checkThread()){
            throw new FaceException("不可跨线程使用人脸检测");
        }
        if(maxFaceNum<0||maxFaceNum>RangeMaxFaceNum){
            throw new FaceException("检测人脸数量超出范围");
        }
        float rect[] = new float[maxFaceNum*4];
        int faceNumAddr[] = new int[1];

        detect(this.handle,img,faceNumAddr,rect,maxFaceNum);
        int faceNum=faceNumAddr[0];
        if(faceNum<0){
            throw new FaceException(ErrorCode.getByCode(faceNum));
        }else if(faceNum>0){
            return FaceRect.covertFromJnaResult(faceNum,rect);
        }else {
            return new ArrayList<FaceRect>();
        }

    }

    /**
     *
     * @param img 图片数据
     * @return
     */
    public List<FaceRect> detect(BufferedImage img) throws FaceException {
        return detect(img,DefaultMaxFaceNum);
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceRect> detect(byte[] imgData,int maxFaceNum) throws FaceException {
        if(imgData==null){
            throw new FaceException("数据不能为空");
        }
        return detect(new ByteArrayInputStream(imgData),maxFaceNum);
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceRect> detect(byte[] imgData) throws FaceException {
        return detect(imgData,DefaultMaxFaceNum);
    }




    /**
     *
     * @param path 图片地址
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceRect> detect(String path,int maxFaceNum) throws FaceException {
        if(path==null){
            throw new FaceException("路径不能为空");
        }
        FileInputStream imgData = null;
        try {
            imgData = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new FaceException("路径错误:"+path);
        }
        return detect(imgData,maxFaceNum);
    }

    /**
     *
     * @param path 图片地址
     * @return
     */
    public List<FaceRect> detect(String path) throws FaceException {
        return detect(path,DefaultMaxFaceNum);
    }

    protected long getHandle() {
        return handle;
    }
    public long getThreadId() {
        return threadId;
    }
    /**
     * 释放句柄
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if(!release) {
            jna.releaseFaceDetector(this.handle);
            log.debug("释放:{}-{}", "FaceDetector", threadId);
            release=true;
        }
        super.finalize();
    }
    public Device getDeviceType() {
        return deviceType;
    }
}

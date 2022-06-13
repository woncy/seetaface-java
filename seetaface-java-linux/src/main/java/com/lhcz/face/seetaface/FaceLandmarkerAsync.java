package com.lhcz.face.seetaface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-06-15
 **/
public class FaceLandmarkerAsync extends Async<FaceLandmarker> {
    public FaceLandmarkerAsync() throws FaceException {
        this(1);
    }

    public FaceLandmarkerAsync(int threadNum) throws FaceException {
        this(threadNum,Device.AUTO);
    }

    public FaceLandmarkerAsync(int threadNum,Device deviceType) throws FaceException {
        this(threadNum,deviceType,0);
    }

    public FaceLandmarkerAsync(int threadNum,Device deviceType, int gpuId) throws FaceException {
        this(threadNum,()->{
            return new FaceLandmarker(new FaceDetector(deviceType,gpuId),deviceType,gpuId);
        });
    }

    protected FaceLandmarkerAsync(int threadNum,Callable<FaceLandmarker> newFun) throws FaceException {
        super(threadNum,newFun);
    }

    /**
     * @param imgData 图像数据
     * @return
     */
    public List<FacePoints> mark(byte[] imgData) throws FaceException {
        return mark(new ByteArrayInputStream(imgData),FaceDetector.DefaultMaxFaceNum);
    }

    /**
     * @param imgData 图像数据
     * @return
     */
    public List<FacePoints> mark(InputStream imgData) throws FaceException {
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
    public List<FacePoints> mark(String path) throws FaceException {
        return mark(path,FaceDetector.DefaultMaxFaceNum);
    }

    /**
     *
     * @param path 图像地址
     * @param maxFaceNum 一张图片最大允许提取多少个人脸
     * @return
     */
    public List<FacePoints> mark(String path,int maxFaceNum) throws FaceException {
        try {
            return mark(ImageIO.read(new File(path)),maxFaceNum);
        } catch (IOException e) {
            throw new FaceException(e);
        }

    }

    /**
     *
     * @param imgData 图像数据
     * @param maxFaceNum 一张图片最大允许提取多少个人脸
     * @return
     */
    public List<FacePoints> mark(InputStream imgData, int maxFaceNum) throws FaceException {
        try {
            return mark(ImageIO.read(imgData),maxFaceNum);
        } catch (IOException e) {
            throw new FaceException(e);
        }
    }

    /**
     * @param img 图片
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FacePoints> mark(BufferedImage img, int maxFaceNum) throws FaceException {
        FaceException[] ex = new FaceException[1];

        List<FacePoints> execute = this.execute(t -> {
            try {
                return t.mark(img, maxFaceNum);
            } catch (FaceException e) {
                ex[0] = e;
                return null;
            }
        });
        if(ex[0]!=null){
            throw ex[0];
        }else {
            return  execute;
        }
    }
}

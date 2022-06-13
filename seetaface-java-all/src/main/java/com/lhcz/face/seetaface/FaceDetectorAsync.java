package com.lhcz.face.seetaface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-06-11
 **/
public class FaceDetectorAsync extends Async<FaceDetector> {
    public FaceDetectorAsync() throws  FaceException {
        this(1);
    }
    public FaceDetectorAsync(int  threadNum) throws  FaceException {
        this(threadNum,Device.AUTO);
    }
    public FaceDetectorAsync(int  threadNum,Device device) throws FaceException {
        this(threadNum,device,0);
    }

    public FaceDetectorAsync(int  threadNum,Device device,int gpu) throws  FaceException {
        this(threadNum,()->new FaceDetector(device,gpu));
    }

    protected FaceDetectorAsync(int  threadNum,Callable<FaceDetector> newFun) throws FaceException {
        super(threadNum,newFun);
    }

    /**
     *
     * @param imgData 图片原始数据
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceRect> detect(InputStream imgData, int maxFaceNum) throws FaceException {
        try {
            return detect(ImageIO.read(imgData),maxFaceNum);
        } catch (IOException e) {
            throw new FaceException(e);
        }
    }

    /**
     *
     * @param imgData 图片原始数据
     * @return
     */
    public List<FaceRect> detect(InputStream imgData) throws FaceException {
        return detect(imgData,FaceDetector.DefaultMaxFaceNum);
    }

    /**
     *
     * @param img 图片
     * @param maxFaceNum 一张图片最大提取多少个人脸
     * @return
     */
    public List<FaceRect> detect(BufferedImage img, int maxFaceNum) throws FaceException {
        FaceException[] ex = new FaceException[1];
        List<FaceRect> results= this.execute(t->{
            try {
                return t.detect(img,maxFaceNum);
            } catch (FaceException e) {
                ex[0]=e;
                return null;
            }
        });

        if(ex[0]!=null){
            throw ex[0];
        }else{
            return results;
        }

    }

    /**
     *
     * @param img 图片数据
     * @return
     */
    public List<FaceRect> detect(BufferedImage img) throws FaceException {
        return detect(img,FaceDetector.DefaultMaxFaceNum);
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
        return detect(imgData,FaceDetector.DefaultMaxFaceNum);
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
        return detect(path,FaceDetector.DefaultMaxFaceNum);
    }

    public static void main(String[] args) throws FaceException, IOException {
        String path = "D:/桌面/临时文件/b.jpg";
        ExecutorService es = Executors.newFixedThreadPool(8);
        FaceRecognizerAsync detectorAsync = new FaceRecognizerAsync(2);
        BufferedImage img = ImageIO.read(new File(path));
        int num=100;
        List<Future<Boolean>> task = new ArrayList<>();
        long time = System.currentTimeMillis();
        for(int i=0;i<num;i++){
            Future<Boolean> submit = es.submit(() -> {
                List<FaceFeature> detect = null;
                try {
                    detect = detectorAsync.feature(img);
                    return true;
                } catch (FaceException e) {
                    e.printStackTrace();
                }
                return false;
            });
            task.add(submit);
        }
        task.forEach(t->{
            try {
                t.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("time:"+(end-time)+"ms");

    }
}

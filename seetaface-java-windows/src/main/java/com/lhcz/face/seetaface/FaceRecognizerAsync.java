package com.lhcz.face.seetaface;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 描述:
 *  人脸功能异步支持
 *
 * @author wangxxxi@163.com
 * @date 2020-07-28
 **/
public class FaceRecognizerAsync extends Async<FaceRecognizer> {
    public FaceRecognizerAsync() throws FaceException {
        this(1);
    }
    public FaceRecognizerAsync(int threadNum) throws FaceException {
        this(threadNum,Device.AUTO);
    }

    public FaceRecognizerAsync(int threadNum,Device device) throws FaceException {
        this(threadNum,device,0);
    }

    public FaceRecognizerAsync(int threadNum,Device deviceType, int gpuId) throws FaceException {
        this(threadNum,()->new FaceRecognizer(deviceType,gpuId));
    }
    protected FaceRecognizerAsync(int threadNum,Callable<FaceRecognizer> newFun) throws FaceException {
        super(threadNum,newFun);
    }

    public List<FaceFeature> feature(String path) throws FaceException {
        try {
            return feature(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new FaceException(e);
        }
    }
    public List<FaceFeature> feature(byte[] data) throws FaceException {
        return feature(new ByteArrayInputStream(data));
    }
    public List<FaceFeature> feature(InputStream data) throws FaceException {
        try {
            return feature(ImageUtil.toImage(data));
        } catch (IOException e) {
            throw new FaceException(e);
        }
    }
    public List<FaceFeature> feature(BufferedImage data) throws FaceException {
        FaceException ex[] = new FaceException[1];
        List<FaceFeature> execute = this.execute(t -> {
            try {
                return t.feature(data);
            } catch (FaceException e) {
                ex[0] = e;
                return null;
            }
        });
        if(ex[0]!=null){
            throw ex[0];
        }else{
            return execute;
        }
    }

    public List<FacePoints> mark(String  path) throws FaceException {
        try {
            return mark(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new FaceException(e);
        }
    }

    public List<FacePoints> mark(byte[] data) throws FaceException {
        return mark(new ByteArrayInputStream(data));
    }
    public List<FacePoints> mark(InputStream data) throws FaceException {
        try {
            return mark(ImageUtil.toImage(data));
        } catch (IOException e) {
            throw new FaceException(e);
        }
    }
    public List<FacePoints> mark(BufferedImage data) throws FaceException {
        FaceException ex[] = new FaceException[1];
        List<FacePoints> execute = this.execute(t -> {
            try {
                return t.getLandmarker().mark(data);
            } catch (FaceException e) {
                ex[0] = e;
                return null;
            }
        });
        if(ex[0]!=null){
            throw ex[0];
        }else{
            return execute;
        }
    }

    public List<FaceRect> detect(String path) throws FaceException {
        try {
            return detect(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new FaceException(e);
        }
    }

    public List<FaceRect> detect(byte[] data) throws FaceException {
        return detect(new ByteArrayInputStream(data));
    }

    public List<FaceRect> detect(InputStream data) throws FaceException {
        try {
            return detect(ImageUtil.toImage(data));
        } catch (IOException e) {
            throw new FaceException(e);
        }
    }

    public List<FaceRect> detect(BufferedImage data) throws FaceException {
        FaceException ex[] = new FaceException[1];
        List<FaceRect> execute = this.execute(t -> {
            try {
                return t.getDetector().detect(data);
            } catch (FaceException e) {
                ex[0] = e;
                return null;
            }
        });
        if(ex[0]!=null){
            throw ex[0];
        }else{
            return execute;
        }
    }


}

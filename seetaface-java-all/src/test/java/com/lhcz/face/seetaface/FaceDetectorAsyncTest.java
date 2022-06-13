package com.lhcz.face.seetaface;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;


public class FaceDetectorAsyncTest {

    /**
     * 单线程应用,人脸检测
     * @throws FaceException
     */
    @Test
    public void testDetect() throws FaceException, IOException {
        FaceDetector detector = new FaceDetector();
        String path="D:\\桌面\\临时文件\\test.jpg";

        //path
        List<FaceRect> t1 = detector.detect(path);
        System.out.println(t1);

        //stream
        FileInputStream fis = new FileInputStream(path);
        List<FaceRect> t2 = detector.detect(fis);
        System.out.println(t2);

        //image
        BufferedImage image = ImageIO.read(new File(path));
        List<FaceRect> t3 = detector.detect(image);
        System.out.println(t3);

        //字节码
        byte[] bytes = IOUtil.readAll(new FileInputStream(path));
        List<FaceRect> t4 = detector.detect(bytes);
        System.out.println(t4);

    }

    /**
     * 多线程应用
     * @throws FaceException
     */
    @Test
    public void testDetectAsync() throws FaceException {
        int threadNum=4;
        FaceDetectorAsync detector = new FaceDetectorAsync(threadNum);

        String path="D:\\桌面\\临时文件\\test.jpg";
        //path
        List<FaceRect> t1 = detector.detect(path);
        System.out.println(t1);


    }


    /**
     * 单线程应用,提取特征
     * @throws FaceException
     */
    @Test
    public void testFeature() throws FaceException, IOException {
        FaceRecognizer recognizer = new FaceRecognizer();
        String path="D:\\桌面\\临时文件\\test.jpg";

        //path
        List<FaceFeature> t1 = recognizer.feature(path);
        System.out.println(t1);

        //stream
        FileInputStream fis = new FileInputStream(path);
        List<FaceFeature> t2 = recognizer.feature(fis);
        System.out.println(t2);

        //image
        BufferedImage image = ImageIO.read(new File(path));
        List<FaceFeature> t3 = recognizer.feature(image);

        FaceFeature faceFeature = t3.get(0);
        byte[] bytes1 = faceFeature.featureToByteArray();


        //字节码
        byte[] bytes = IOUtil.readAll(new FileInputStream(path));
        List<FaceFeature> t4 = recognizer.feature(bytes);
        System.out.println(t4);

    }

}
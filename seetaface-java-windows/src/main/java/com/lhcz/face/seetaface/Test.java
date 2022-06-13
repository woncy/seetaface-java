package com.lhcz.face.seetaface;//package com.lhcz.face.seetaface;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
///**
// * 描述:
// *
// * @author wangxxxi@163.com
// * @date 2021-04-28
// **/
//public class Test {
//    public static void main1(String[] args) throws IOException, FaceException, InterruptedException {
//        String file = "D:/桌面/临时文件/a.jpg";
//        if(args.length>=1&&args[0]!=null){
//            file = args[0];
//        }
//        FaceDetector detector = new FaceDetector(Device.CPU);
//        List<FaceRect> detect = detector.detect(file);
//        if(args.length>=2&&args[1]!=null){
//            BufferedImage image = ImageIO.read(new File(file));
//            ImageUtil.drawRect(image,detect);
//            ImageIO.write(image,"jpg",new FileOutputStream(args[1]));
//        }
//        System.out.println(detect);
//    }
//
//    public static void main(String[] args) throws FaceException {
//        String file = "D:/桌面/临时文件/a.jpg";
//        FaceDetector detector = new FaceDetector();
//        FaceRecognizerAsync async = new FaceRecognizerAsync();
//
//        long time1 = time(() -> {
//            try {
//                detector.detect(file);
//            } catch (FaceException e) {
//                e.printStackTrace();
//            }
//        });
//
//        long time2 = time(() -> {
//            try {
//                async.detect(file);
//            } catch (FaceException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        System.out.printf("time1=%s,time2=%s",time1,time2);
//    }
//
//
//    public static long time(Runnable task){
//        long start = System.currentTimeMillis();
//        task.run();
//        long end = System.currentTimeMillis();
//        return end-start;
//    }
//
//
//
//
//    public static void testTime(String path,int num) throws FaceException, InterruptedException, IOException {
//        FaceRecognizerAsync async = new FaceRecognizerAsync(4);
//        ExecutorService es = Executors.newFixedThreadPool(3);
//        List<Future<List<FaceRect>>> tasks = new ArrayList<>();
//        long start = System.currentTimeMillis();
//        for (int i=0;i<num;i++){
//           tasks.add(es.submit(()->async.detect(path)));
//        }
//        tasks.forEach(t-> {
//            try {
//                t.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        });
//        long end = System.currentTimeMillis();
//        System.out.println(String.format("耗时:%sms",(end-start)));
//
//    }
//}

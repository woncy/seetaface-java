package com.lhcz.face.seetaface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-06-11
 **/
public class FaceAsync {
    static final int threadNum=4;
    static ExecutorService es = Executors.newFixedThreadPool(threadNum);
    static ExecutorService exeEs = Executors.newFixedThreadPool(1);
    static   Map<Long,FaceDetector> detectorMap=new HashMap<>();

    public static void main(String[] args) throws FaceException, ExecutionException, InterruptedException {


        for(int i=0;i<threadNum;i++) {
            es.submit(() -> {
                try {
                    detectorMap.put(Thread.currentThread().getId(), new FaceDetector());
                } catch (FaceException e) {
                    e.printStackTrace();
                }
                return true;
            }).get();
        };

        String path="D:/桌面/临时文件/a.jpg";
        List<Future<Long>> tasks = new ArrayList<>();
        int num=100;
        for (int i=0;i<num;i++){
            tasks.add(exeEs.submit(()->{
                long time = time(() -> {
                    try {
                        detect(path);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                return time;
            }));
        }

        long sum = tasks.stream().mapToLong(t -> {
            try {
                return t.get();
            } catch (Exception e) {
                e.printStackTrace();
                return 0L;
            }

        }).sum();
        System.out.printf("%s个共耗时%sms",num,sum);


    }

    public static long time(Runnable task){
        long start = System.currentTimeMillis();
        task.run();
        long end = System.currentTimeMillis();
        return end-start;
    }

    public static List<FaceRect> detect(String path) throws ExecutionException, InterruptedException {
        return es.submit(()->{
            return detectorMap.get(Thread.currentThread().getId()).detect(path);
        }).get();
    }

}

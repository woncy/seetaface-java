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

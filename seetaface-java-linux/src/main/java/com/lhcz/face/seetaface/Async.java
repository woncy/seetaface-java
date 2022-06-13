package com.lhcz.face.seetaface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-06-11
 **/
abstract class Async<T> {
    private ExecutorService es;
    Map<Long,T> tMap;

    protected Async(int threadNum, Callable<T> callable) throws FaceException {
        es= Executors.newFixedThreadPool(threadNum);
        tMap = new HashMap<>();
        for (int i = 0; i <threadNum ; i++) {
            FaceException[] ex = new FaceException[1];
            try {
                es.submit(()->{
                    long threadId = Thread.currentThread().getId();
                    try {
                        T call = callable.call();
                        tMap.put(threadId,call);
                    } catch (Exception e) {
                        ex[0]=new FaceException(e);
                    }
                }).get();
            } catch (Exception e) {
                throw new FaceException(e);
            }
            if(ex[0]!=null){
                throw ex[0];
            }
        }
    }

    protected  <R> R execute(Function<T,R> fun) throws FaceException {
        FaceException ex[] = new FaceException[1];
        try {
            R r = es.submit(() -> {
                long threadId = Thread.currentThread().getId();
                T t = tMap.get(threadId);
                if (t != null) {
                    return fun.apply(t);
                } else {
                    ex[0] = new FaceException("未找到合适的执行线程");
                    return null;
                }
            }).get();
            if(ex[0]!=null){
                throw ex[0];
            }
            return r;
        } catch (Exception e) {
            throw new FaceException(e);
        }
    }
}

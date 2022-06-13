package com.lhcz.face.seetaface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

/**
 * 描述:
 *   人脸相关功能的封装,因人脸api不能异步调用，所以封装此类提供异步支持
 *   
 * @author wangxxxi@163.com
 * @date 2020-07-28
 **/
class FaceExecutor implements Runnable {
    protected static final Logger log = LoggerFactory.getLogger(FaceExecutor.class);

    //人脸检测和比对的句柄
    private FaceRecognizer recognizer;

    private Device deviceType;
    private boolean start;
    private Queue<FaceExecutorBean> tasks;
    private int maxQueue;

    protected FaceExecutor(Device deviceType,int maxQueue) {
        this.deviceType = deviceType;
        tasks=new ConcurrentLinkedDeque<>();
        start = true;
        this.maxQueue=maxQueue;
    }

    protected void stop(){
        start = false;
    }


    /**
     * 描述:
     *  提取特征
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FaceFeature>
     */
    protected void feature(BufferedImage img,BiConsumer<List<FaceFeature>,FaceException > callback) throws FaceException {
        if(!start){
            throw new RuntimeException("faceExecutor is stoped");
        }else{
            executor(img, 2, callback);
        }
    }

    /**
     * 描述:
     *  提取特征
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FaceFeature>
     */
    protected List<FaceFeature> feature(BufferedImage img) throws FaceException, InterruptedException {
        if(!start){
            throw new RuntimeException("faceExecutor is stoped");
        }else{
            return executor(img, 2, FaceFeature.class);
        }
    }

    /**
     * 描述:
     *  检测人脸位置
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FaceRect>
     */
    protected void detect(BufferedImage img,BiConsumer<List<FaceRect>,FaceException> callback) throws FaceException {
        if(!start){
            throw new RuntimeException("faceExecutor is stoped");
        }else{
            executor(img, 0, callback);
        }
    }

    /**
     * 描述:
     *  检测人脸位置
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FaceRect>
     */
    protected List<FaceRect> detect(BufferedImage img) throws FaceException, InterruptedException {
        if(!start){
            throw new RuntimeException("faceExecutor is stoped");
        }else{
            return executor(img, 0, FaceRect.class);
        }
    }



    /**
     * 描述:
     *  检测人脸定位点
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FacePoints>
     */
    protected void mark(BufferedImage img,BiConsumer<List<FacePoints>,FaceException> callback) {
        if(!start){
            throw new RuntimeException("FaceExecutor is stoped");
        }else{
            executor(img, 1, callback);
        }
    }

    /**
     * 描述:
     *  检测人脸定位点
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FacePoints>
     */
    protected List<FacePoints> mark(BufferedImage img) throws FaceException, InterruptedException {
        if(!start){
            throw new RuntimeException("FaceExecutor is stoped");
        }else{
           return executor(img, 1, FacePoints.class);
        }
    }


    /**
     * 描述:
     *  检测人脸定位点
     *
     * @param img
     * @author $wangxxxi@163.com$
     * @date 2020/7/28
     * @return java.util.List<com.lhcz.face.seetaface.FacePoints>
     */
    private <T> List<T> executor(BufferedImage img,int type,Class<T> tClass) throws FaceException, InterruptedException {
        if(!start){
            throw new RuntimeException("FaceExecutor is stoped");
        }else{
            List<T> results[]=new List[1];
            CountDownLatch countDownLatch = new CountDownLatch(1);
            FaceException[] exceptions = new FaceException[1];
            executor(img, type, (result,exception)->{
                results[0]= (List<T>) result;
                exceptions[0]=exception;
                countDownLatch.countDown();

            });
            countDownLatch.await();
            if (exceptions[0] != null) {
                throw exceptions[0];
            }
            return results[0];
        }
    }



    private<T> void executor(BufferedImage img,int type,BiConsumer<T,FaceException> callback) {
        if(!start){
            throw new RuntimeException("FaceExecutor is stoped");
        }
        while (this.tasks.size()>=this.maxQueue){
            sleep(1L);
        }
        if(img==null){
            return;
        }
        FaceExecutorBean<T> bean = new FaceExecutorBean<>();
        bean.img=img;
        bean.type=type;
        bean.callback= callback;
        tasks.add(bean);
    }

    protected int getQueueNum(){
        return this.tasks.size();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("人脸检测线程-"+Thread.currentThread().getId());
        try {
            recognizer = new FaceRecognizer();
        } catch (FaceException e) {
            log.error("FaceExecutor init error:"+e.getMessage(),e);
            return;
        }
        int spaceNum=0;
        while(start){
            FaceExecutorBean poll = this.tasks.poll();
            try {
                if (poll != null) {
                    spaceNum=0;
                    if (poll.type == 0) {
                        detect(poll);
                    } else if (poll.type == 1) {
                        mark(poll);
                    } else if (poll.type == 2) {
                        feature(poll);
                    }else {
                        continue;
                    }
                } else {
                    spaceNum++;
                    if(spaceNum%10000==0){
                        sleep(1L);
                    }
                }
            }catch (Throwable e){
                log.error("人脸异步执行异常",e);
            }
        }
    }

    private void feature(FaceExecutorBean<List<FaceFeature>> bean){
        try {
            List<FaceFeature> feature = recognizer.feature(bean.img);
            bean.callback.accept(feature,null);
        } catch (FaceException e) {
            bean.callback.accept(null,e);
        }finally {
        }
    }

    private void detect(FaceExecutorBean<List<FaceRect>> bean){
        try {
            List<FaceRect> feature = recognizer.getDetector().detect(bean.img);
            bean.callback.accept(feature,null);
        } catch (FaceException e) {
            bean.callback.accept(null,e);
        }finally {
        }
    }

    private void mark(FaceExecutorBean<List<FacePoints>> bean ){
        try {
            List<FacePoints> feature = recognizer.getLandmarker().mark(bean.img);
            bean.callback.accept(feature,null);
        } catch (FaceException e) {
            bean.callback.accept(null,e);
        }finally {
        }
    }

    private void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.warn("线程异常:"+e.getMessage(),e);
        }
    }

    class FaceExecutorBean<T>{
        private BufferedImage img;
        private BiConsumer<T,FaceException> callback;
        private int type; // 0 detect, 1 mark, 2 feature
    }

    @Override
    protected void finalize() throws Throwable {
        this.recognizer.finalize();
    }
}



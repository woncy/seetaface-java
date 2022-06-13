package com.lhcz.face.seetaface;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

final class Resource {
    public static final Logger log = LoggerFactory.getLogger(Resource.class);
    private static final String MODE_PATH="/model";
    private static final String DLL_PATH_X64="/win32-x86-64";
    private static final String SO_PATH_X64= "/linux-x86-64";
    protected static String root;
    protected static String modelRoot;
    protected static String dllRoot;
    private static  boolean isInit=false;
    private static boolean initFirst=false;

    private static String getPathFromClass(){
        String property = System.getProperty("face.root.path");
        if(property!=null&&!"".equals(property)){
            File file = new File(property);
            if(file.isDirectory()){
                if(!file.exists()){
                    file.mkdirs();
                }
                return  file.getAbsolutePath();
            }
        }
        String file = "";
        URL resource = Resource.class.getResource("/");
        if(resource!=null){
            file=resource.getFile();
        }else {
            file=Platform.isLinux()?"/usr/face/":"C:/face/";
        }
        return file;
    }


    private static void initRoot(String depPath) throws UnsupportedEncodingException {
        String file = "/";
        if(depPath!=null&&!"".equals(depPath)){
            File depFile = new File(depPath);
            if(depFile.isDirectory()){
                if(!depFile.exists()){
                    depFile.mkdirs();
                }
                file=depFile.getAbsolutePath();
            }else{
                file=getPathFromClass();
            }
        }else{
            file=getPathFromClass();
        }
        root = new File(file).getAbsolutePath();
        root= URLDecoder.decode(root);
        String libPath=Platform.isLinux()?SO_PATH_X64:(Platform.isWindows()?DLL_PATH_X64:null);
        modelRoot=new File(root+MODE_PATH).getAbsolutePath();
        dllRoot=new File(root+libPath).getAbsolutePath();
        log.debug("root path:{}",root);
        log.debug("model path:{}",modelRoot);
        log.debug("dll path:{}",dllRoot);
    }
    private static final String[] modelList={
            "age_predictor.csta",
            "eye_state.csta",
            "face_detector.csta",
            "face_landmarker_mask_pts5.csta",
            "face_landmarker_pts5.csta",
            "face_landmarker_pts68.csta",
            "face_recognizer.csta",
            "face_recognizer_light.csta",
            "face_recognizer_mask.csta",
            "fas_first.csta",
            "fas_second.csta",
            "gender_predictor.csta",
            "mask_detector.csta",
            "pose_estimation.csta",
            "quality_lbn.csta"
    };
    private static final String[] windllList={
            "SeetaAuthorize.dll",
            "tennis.dll",
            "tennis_haswell.dll",
            "tennis_pentium.dll",
            "tennis_sandy_bridge.dll",
            "SeetaAgePredictor600.dll",
            "SeetaEyeStateDetector200.dll",
            "SeetaFaceAntiSpoofingX600.dll",
            "SeetaFaceDetector600.dll",
            "SeetaFaceLandmarker600.dll",
            "SeetaFaceRecognizer610.dll",
            "SeetaFaceTracking600.dll",
            "SeetaGenderPredictor600.dll",
            "SeetaMaskDetector200.dll",
            "SeetaPoseEstimation600.dll",
            "SeetaQualityAssessor300.dll",
            "Seetaface6_JNA.dll"
    };
    private static final String[] linuxSoList={
            "libSeetaAuthorize.so",
            "libtennis.so",
            "libtennis_haswell.so",
            "libtennis_pentium.so",
            "libtennis_sandy_bridge.so",
            "libSeetaAgePredictor600.so",
            "libSeetaEyeStateDetector200.so",
            "libSeetaFaceAntiSpoofingX600.so",
            "libSeetaFaceDetector600.so",
            "libSeetaFaceLandmarker600.so",
            "libSeetaFaceRecognizer610.so",
            "libSeetaFaceTracking600.so",
            "libSeetaGenderPredictor600.so",
            "libSeetaMaskDetector200.so",
            "libSeetaPoseEstimation600.so",
            "libSeetaQualityAssessor300.so",
            "libSeetaface6_JNA.so"
    };

    private static void checkFileList(){
        log.info("开始检查人脸依赖资源...");
        File parent = new File(root+MODE_PATH+"/");
        if(!parent.exists()){
            parent.mkdirs();
        }
        for (String name: modelList){
            String path =root+ MODE_PATH+"/"+name;
            File file = new File(path);
            if(!file.exists()){
                log.info("人脸依赖资源:"+path+"不存在,开始创建");
                try{
                    ModelResource.writeModel(MODE_PATH+"/"+name,path);
                    log.info("人脸依赖资源:"+path+"创建成功");
                }catch (Exception e){
                    log.error("人脸依赖资源:"+path+"创建失败！可能影响人脸相关功能正常使用！！",e);
                }
            }else{
            }

        }
        String libPath=Platform.isLinux()?SO_PATH_X64:(Platform.isWindows()?DLL_PATH_X64:null);
        File parent2 = new File(root+libPath+"/");
        if(!parent2.exists()){
            parent2.mkdirs();
        }
        String[] libList = Platform.isWindows()?windllList:((Platform.isLinux()&&Platform.is64Bit())?linuxSoList:null);
        List<String> libs = new ArrayList<>();
        for (String name: libList){
            String path = root+libPath+"/"+name;
            File file = new File(path);
            if(!file.exists()){
                log.info("人脸依赖资源:"+path+"不存在,开始创建");
                try{
                    writeResource(libPath+"/"+name);
                    log.info("人脸依赖资源:"+path+"创建成功");
                }catch (Exception e){
                    log.error("人脸依赖资源:"+path+"创建失败！可能影响人脸相关功能正常使用！！",e);
                }
            }else{
            }
            libs.add(path);
        }
        loadLibs(libs);
        log.info("检查人脸依赖资源完毕！");
    }

    private static void loadLibs(List<String> libs) {
        libs.forEach(path -> {
            try {
                System.load(path);
                log.info("已加载依赖:{}",path);
            } catch (Error e) {
                log.error("加载库失败",e);
            } catch (Throwable t) {
                log.error("加载库失败",t);
            }
        });

    }

    private static void writeResource(String path) throws IOException {
        InputStream in = Resource.class.getResourceAsStream(path);
        FileOutputStream fos = new FileOutputStream(root + path);
        byte[] buffer = new byte[1024*100];
        int len =0;
        while((len=in.read(buffer))>0){
            fos.write(buffer,0,len);
            fos.flush();
        }
        fos.close();
        in.close();
    }
    protected static boolean isInit(){
        return isInit;
    }

    public synchronized static void init(String depPath){
        if(isInit||initFirst){
            return;
        }
        initFirst=true;
        try{
            initRoot(depPath);
            checkFileList();
            if(Platform.isWindows()){
                System.setProperty("jna.encoding","GBK");
            }
            isInit=true;
            log.info("加载人脸依赖资源完毕!");
        }catch (Exception e){
            log.error("人脸资源初始化异常,将会影响人脸相关功能使用！！",e);
        }

    }


}



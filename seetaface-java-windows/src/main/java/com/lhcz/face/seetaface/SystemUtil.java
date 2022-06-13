package com.lhcz.face.seetaface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-05-07
 **/
public class SystemUtil {
    public static final Logger log = LoggerFactory.getLogger(SystemUtil.class);
    public static void exportPathForLinux(String key,String val){
        String command = String.format("export %s=%s",key,val);
        File file = new File("./tmp");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(command.getBytes());
            fos.flush();
            fos.close();
            if(execForLinux("source " + file.getAbsolutePath()+"")){
                log.info("设置环境变量:{}={}",key,System.getenv(key));
                file.delete();
            }
        } catch (Exception e) {
            log.error("设置环境变量:{}错误",key,e);
        }


    }

    public static boolean execForLinux(String command) throws InterruptedException, IOException {
        log.info(command);
        Process exec = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", command});
        int i = exec.waitFor();
        if(i==0){
            return true;
        }else {
            byte[] bytes = IOUtil.readAll(exec.getErrorStream());
            throw new RuntimeException(new String(bytes));
        }
    }
}

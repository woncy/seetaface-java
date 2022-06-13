package com.lhcz.face.seetaface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-05-17
 **/
public class ModelResource {

    public static void writeModel(String model,String writePath) throws IOException {
        InputStream in = ModelResource.class.getResourceAsStream(model);
        FileOutputStream fos = new FileOutputStream(writePath);
        byte[] buffer = new byte[1024*100];
        int len =0;
        while((len=in.read(buffer))>0){
            fos.write(buffer,0,len);
            fos.flush();
        }
        fos.close();
        in.close();
    }
}

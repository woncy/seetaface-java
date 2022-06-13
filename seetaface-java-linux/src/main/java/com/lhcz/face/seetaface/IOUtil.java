package com.lhcz.face.seetaface;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
    public static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*1024];
        int len = 0;
        try {
            while ((len=in.read(buffer)) > 0) {
                bos.write(buffer,0,len);
                bos.flush();
            }
            byte[] bytes = bos.toByteArray();
            return bytes;
        }finally {
            IOUtil.close(bos);
            IOUtil.close(in);
        }
    }

    public static void close(InputStream in) {
        try {
            if(in!=null){
                in.close();
            }
        } catch (IOException e) {

        }
    }

    public static void close(OutputStream out) {
        try {
            if(out!=null){
                out.close();
            }
        } catch (IOException e) {

        }
    }
}

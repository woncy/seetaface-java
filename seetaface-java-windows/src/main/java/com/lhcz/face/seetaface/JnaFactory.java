package com.lhcz.face.seetaface;

import com.sun.jna.Native;

class JnaFactory {
    private static Jna instance=null;
    protected synchronized static Jna getInstance() throws FaceException {
        if(!Resource.isInit()){
            Resource.init(null);
        }
        if(instance==null){
            try {
                instance = (Jna) Native.loadLibrary("Seetaface6_JNA", Jna.class);
            }catch (Throwable e){
                throw new FaceException("初始化Jna句柄失败",e);
            }
        }
        return instance;
    }
}

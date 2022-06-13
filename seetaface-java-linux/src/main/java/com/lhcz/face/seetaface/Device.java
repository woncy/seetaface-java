package com.lhcz.face.seetaface;


public enum Device {
    AUTO(0),CPU(1),/** 目前版本不支持GPU */@Deprecated() GPU(2);

    private Device(int type){
        this.type=type;
    }
    private int type;
    public int getType() {
        return type;
    }

}

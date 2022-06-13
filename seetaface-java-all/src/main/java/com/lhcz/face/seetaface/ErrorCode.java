package com.lhcz.face.seetaface;

enum ErrorCode {
    ImageInitError(-1,"图片加载错误"),  //图片初始化错误
    FaceDetectorHandleError(-2,"人脸检测句柄错误"), //人脸检测句柄错误
    FaceDetectError(-3,"人脸检测失败"), //人脸检测失败
    FaceMaxFaceNumNotInRangeError(-4,"人脸最大检测数量超出范围"), //人脸最大检测数量超出范围
    ModelSettingInitError(-5,"设置cpu类型错误"), //设置cpu类型错误
    HandleInitError(-6,"句柄初始化错误"), //句柄初始化错误；
    FaceComperasionError(-7,"人脸比对错误"),//人脸比对错误
    FaceExtractError(-8,"提取特征错误");//提取特征错误

    private int code;
    private String msg;

    private ErrorCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public static ErrorCode getByCode(int code){
        for (ErrorCode value : ErrorCode.values()) {
            if(value.getCode()==code){
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

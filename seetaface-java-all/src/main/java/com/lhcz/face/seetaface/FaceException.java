package com.lhcz.face.seetaface;

/**
 * 人脸相关功能异常
 */
public class FaceException extends Exception {


    public FaceException(ErrorCode errorCode) {
        super(errorCode.getMsg()+",code="+errorCode.getCode());
    }

    public FaceException(String message) {
        super(message);
    }

    public FaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FaceException(Throwable cause) {
        super(cause);
    }
}

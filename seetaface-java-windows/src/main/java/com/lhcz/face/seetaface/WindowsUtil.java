package com.lhcz.face.seetaface;


import com.sun.jna.WString;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-05-08
 **/
public class WindowsUtil {
    public static WString toWChar(String str){
        return new WString(str);
    }
}

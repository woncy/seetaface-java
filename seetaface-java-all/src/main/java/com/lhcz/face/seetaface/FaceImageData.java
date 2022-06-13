package com.lhcz.face.seetaface;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-06-10
 **/
public class FaceImageData {
    int width;
    int height;
    int channels;
    byte[] data;

    public static FaceImageData fromBufferedImage(BufferedImage img){
        FaceImageData data=new FaceImageData();
        if(img.getType()!=BufferedImage.TYPE_3BYTE_BGR) {
            BufferedImage i = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            i.setData(img.getData());
            img = i;
        }
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer())
                .getData();
        data.data=pixels;
        data.width=img.getWidth();
        data.height=img.getHeight();
        data.channels=3;
        return data;
    }



    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


}

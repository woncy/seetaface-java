package com.lhcz.face.seetaface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 描述:
 *
 * @author wangxxxi@163.com
 * @date 2021-04-02
 **/
public class ImageUtil {

    public static BufferedImage toImage(InputStream inputStream) throws IOException {
        if(inputStream!=null){
            try {
                return ImageIO.read(inputStream);
            }finally {
                IOUtil.close(inputStream);
            }
        }else{
            return null;
        }
    }


    public static void drawRect(BufferedImage image, List<FaceRect> rects){
        if(rects==null||rects.size()<0||image==null){
        }else{
            Graphics2D graphics = image.createGraphics();
            for (FaceRect rect : rects) {
                drawRect(graphics,rect);
            }
        }
    }


    private static void drawRect(Graphics2D g, FaceRect rect){
        if(g==null||rect==null){
            return;
        }else{
            Color color = g.getColor();
            g.setColor(Color.RED);
            Stroke stroke = g.getStroke();
            g.setStroke(new BasicStroke(2.5f));
            g.drawRect((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
            g.setStroke(stroke);
            g.setColor(color);
        }
    }

    private BufferedImage toImage(byte[] data) throws IOException {
        return toImage(new ByteArrayInputStream(data));
    }
}

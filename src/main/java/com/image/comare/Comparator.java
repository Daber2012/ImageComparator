package com.image.comare;


import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.image.comare.objects.ChangesPoly;
import com.image.comare.objects.Point;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Comparator {

    public BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2) {
        int width1 = img1.getWidth();
        int width2 = img2.getWidth();
        int height1 = img1.getHeight();
        int height2 = img2.getHeight();
        if ((width1 != width2) || (height1 != height2)) {
            System.err.println("Different sizes");
            return null;
        }
        BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
        List<Point> changePoints = new ArrayList<>();
        int diff;
        int result;
        for (int j = 0; j < height1; j++) {
            for (int i = 0; i < width1; i++) {
                int rgb1 = img1.getRGB(i, j);
                int rgb2 = img2.getRGB(i, j);
                int red1 = (rgb1 >> 16) & 0xff;
                int green1 = (rgb1 >> 8) & 0xff;
                int blue1 = (rgb1) & 0xff;
                int red2 = (rgb2 >> 16) & 0xff;
                int green2 = (rgb2 >> 8) & 0xff;
                int blue2 = (rgb2) & 0xff;
                diff = Math.abs(red1 - red2);
                diff += Math.abs(green1 - green2);
                diff += Math.abs(blue1 - blue2);
                diff /= 3;
                result = (diff << 16) | (diff << 8) | diff;
                if (result != 0) {
                    changePoints.add(new Point(i, j));
                }
                outImg.setRGB(i, j, rgb2);
            }
        }
        drawChanges(changePoints, outImg);
        return outImg;
    }
   
    public void drawChanges(List<Point> changePoints, BufferedImage resImg) {
        Graphics2D g2 = resImg.createGraphics();
        for (ChangesPoly change : SortUtil.findChangesPoly(changePoints)) {
            g2.setColor(Color.RED);
            g2.drawRect(change.getX1(), change.getY1(),
                    change.getX2() - change.getX1(), change.getY2() - change.getY1());
        }
    }
   
    public byte[] compare(MultipartFile f1, MultipartFile f2) {
        byte[] result;
        BufferedImage img1;
        BufferedImage img2;
        try {
            ByteArrayInputStream mlpFileStream = new ByteArrayInputStream(f1.getBytes());
            img1 = ImageIO.read(mlpFileStream);
            mlpFileStream = new ByteArrayInputStream(f2.getBytes());
            img2 = ImageIO.read(mlpFileStream);
            mlpFileStream.close();
            BufferedImage resImg = getDifferenceImage(img1, img2);
            ByteArrayOutputStream mlpByteStream = new ByteArrayOutputStream();
            ImageIO.write( resImg, "png", mlpByteStream);
            mlpByteStream.flush();
            result = mlpByteStream.toByteArray();
            mlpByteStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}




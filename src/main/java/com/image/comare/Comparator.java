package com.image.comare;

import javax.imageio.ImageIO;

import org.assertj.core.util.Lists;
import org.springframework.web.multipart.MultipartFile;
import com.image.comare.objects.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

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
        
        int centerX = width1 % 2;
        int centerY = (int)height1/2;
        System.out.println("x" + width1 + " center " + centerX);
        System.out.println("y" + height1 + " center " + centerY);
        
        CopyOnWriteArrayList<Point> changePoints = new CopyOnWriteArrayList<>();
        
        //comparePoly(changePoints, img1, img2, 0,0,width1,height1);
        
        Thread th1 = new Thread(() -> comparePoly(changePoints, img1, img2, 0, 0, centerX, centerY));
        Thread th2 = new Thread(() -> comparePoly(changePoints, img1, img2, centerX, 0, width1-1, centerY));
        Thread th3 = new Thread(() -> comparePoly(changePoints, img1, img2, 0, centerY, centerX, height1-1));
        Thread th4 = new Thread(() -> comparePoly(changePoints, img1, img2, centerX, centerY, width1-1, height1-1));
        th1.start();th2.start();th3.start();th4.start();
        th1.run();th2.run();th3.run();th4.run();
        BufferedImage outImg = null;
        while (true) {
        	if (!th1.isAlive() && !th2.isAlive() && !th3.isAlive() && !th3.isAlive()) {
		        outImg = ImageUtil.copyImage(img2);
		        ImageUtil.drawChanges(new ArrayList<Point>(changePoints), outImg);
		        break;
        	}
        }

        
        return outImg;
    }
    
    public void comparePoly(CopyOnWriteArrayList<Point> changePoints, BufferedImage img1, BufferedImage img2, 
                              int x1, int y1, int x2, int y2) {
    	int diff;
        int result;
        for (int j = x1; j <= y2; j++) {
            for (int i = y1; i <= x2; i++) {
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
            }
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




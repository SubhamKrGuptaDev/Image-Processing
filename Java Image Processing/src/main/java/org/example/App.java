package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static final String SOURCE_FILE = "src/main/sources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "src/main/out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage outPutImage =
                new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();
//        recolorSingleThreaded(originalImage, outPutImage);

        int threadCount = 10;

        recolorMultiThreaded(originalImage, outPutImage, threadCount);
        System.out.println("System Take Time to Execute : " + (System.currentTimeMillis() - startTime));

        File outputFile = new File(DESTINATION_FILE);
        File parentDir = outputFile.getParentFile();

        if(!parentDir.exists()) {
            parentDir.mkdir();
        }

        ImageIO.write(outPutImage, "jpg",outputFile);
        System.out.println("Process Completed.");

    }

    public static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int threadCount) {
        List<Thread> listOfThread = new ArrayList<>();

        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / threadCount;

        for(int i=0; i<threadCount; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int xOrigin = 0;
                int yOrigin = height * threadMultiplier;

                recolorImage(originalImage, resultImage, xOrigin, yOrigin, width, height);
            });

            listOfThread.add(thread);
        }

        for(Thread thread : listOfThread) {
            thread.start();
        }

        for(Thread thread : listOfThread) {
            try {
                thread.join();
            } catch (Exception ex) {
                System.out.println("Thread Join Exception. Message : "+ex.getMessage());
            }
        }

    }

    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                    int width, int height) {

        for(int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for(int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }

    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster()
                .setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }


    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }


}

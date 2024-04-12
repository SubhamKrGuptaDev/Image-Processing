package com.image.process.service;

import com.image.process.exception.FileException;
import com.image.process.exception.ThreadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.image.process.utill.CommonUtil.DESTINATION_DIRECTORY;
import static com.image.process.utill.CommonUtil.UPLOAD_DIRECTORY;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {


    @Override
    public String imageColorChangeGrayToPurple(String fileName) {

        try {

            BufferedImage originalImage = ImageIO.read(new File(UPLOAD_DIRECTORY + "/" + fileName));
            BufferedImage outPutImage =
                    new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            long startTime = System.currentTimeMillis();
//            recolorSingleThreaded(originalImage, outPutImage);

            int threadCount = 10;

            recolorMultiThreaded(originalImage, outPutImage, threadCount);
            log.info("System Take Time to Process Image : {}", (System.currentTimeMillis() - startTime));

            File outputFile = new File(DESTINATION_DIRECTORY + "/" + fileName);
            File parentDir = outputFile.getParentFile();

            if (!parentDir.exists()) {
                parentDir.mkdir();
            }

            ImageIO.write(outPutImage, "jpg", outputFile);
            log.info("Process Completed.");

            return outputFile.getAbsolutePath();
        } catch (Exception ex) {
            log.error("Exception Message : {}", ex.getMessage());
            throw new FileException("File Exception Message : " + ex.getMessage());
        }

    }

    private void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int threadCount) {
        List<Thread> listOfThread = new ArrayList<>();

        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / threadCount;

        for (int i = 0; i < threadCount; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int xOrigin = 0;
                int yOrigin = height * threadMultiplier;

                recolorImage(originalImage, resultImage, xOrigin, yOrigin, width, height);
            });

            listOfThread.add(thread);
        }

        for (Thread thread : listOfThread) {
            thread.start();
        }

        for (Thread thread : listOfThread) {
            try {
                thread.join();
            } catch (Exception ex) {
                log.error("Thread Join Exception. Message : {}", ex.getMessage());
                throw new ThreadException("Thread Join Exception | Message : " + ex.getMessage());
            }
        }

    }

    private void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    private void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                              int width, int height) {

        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }

    }

    private void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if (isShadeOfGray(red, green, blue)) {
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

    private void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster()
                .setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }


    private boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    private int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    private int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    private int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    private int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

}

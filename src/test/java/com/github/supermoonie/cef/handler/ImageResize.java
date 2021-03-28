package com.github.supermoonie.cef.handler;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author supermoonie
 * @date 2021-03-28
 */
public class ImageResize {

    @Test
    public void test_resize() throws IOException {
        BufferedImage originalImage = ImageIO.read(Objects.requireNonNull(ImageResize.class.getClassLoader().getResourceAsStream("input.png")));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .forceSize(200, 200)
                .outputFormat("PNG")
                .outputQuality(1)
                .toOutputStream(outputStream);
        byte[] data = outputStream.toByteArray();
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
//        ImageIO.read(inputStream);
        FileUtils.writeByteArrayToFile(new File("output.png"), data);
    }
}

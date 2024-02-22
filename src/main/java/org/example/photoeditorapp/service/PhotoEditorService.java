package org.example.photoeditorapp.service;

import org.example.photoeditorapp.model.ImageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
@Service
public class PhotoEditorService {
   private static final Logger logger = LoggerFactory.getLogger(PhotoEditorService.class);

    private final ResourceLoader resourceLoader;

    public PhotoEditorService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void saveImage(MultipartFile file) throws IOException {
        // Save the uploaded image to a directory
        String uploadDir = "src/main/resources/static/images/";
        File uploadPath = new File(uploadDir);

        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        File imageFile = new File(uploadPath.getAbsolutePath(), file.getOriginalFilename());
        file.transferTo(imageFile);
    }

    public Resource getCroppedImageResource(ImageModel imageModel) throws IOException {

        Resource resource = resourceLoader.getResource("classpath:static/images/" + imageModel.getFileName().getOriginalFilename());
        BufferedImage originalImage = ImageIO.read(resource.getInputStream());

        // Crop the image
        BufferedImage croppedImage = originalImage.getSubimage(imageModel.getX(), imageModel.getY(),
                imageModel.getWidth(), imageModel.getHeight());

        // Convert the cropped image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(croppedImage, "png", baos);

        // Convert byte array to InputStreamResource
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));

        logger.info("Image cropped successfully: {}", imageModel.getFileName());

        return inputStreamResource;
    }

    public Resource rotateImage(ImageModel imageModel) throws IOException {
        // Load the image
        Resource resource = resourceLoader.getResource("classpath:static/images/" + imageModel.getFileName().getOriginalFilename());
        BufferedImage originalImage = ImageIO.read(resource.getInputStream());

        // Rotate the image
        BufferedImage rotatedImage = rotate(originalImage, imageModel.getRotationAngle());

        // Convert the rotated image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(rotatedImage, "png", baos);

        // Convert byte array to InputStreamResource
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        logger.info("Image rotated successfully: {}", imageModel.getFileName());
        return inputStreamResource;
    }

    // Method to rotate the image
    private BufferedImage rotate(BufferedImage image, double angle) {
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = rotated.createGraphics();
        g.translate((newWidth - width) / 2, (newHeight - height) / 2);
        g.rotate(radians, width / 2, height / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return rotated;
    }
}

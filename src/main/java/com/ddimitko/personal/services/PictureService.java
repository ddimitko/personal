package com.ddimitko.personal.services;

import com.ddimitko.personal.models.Picture;
import com.ddimitko.personal.models.Post;
import com.ddimitko.personal.models.User;
import com.ddimitko.personal.repositories.PictureRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public byte[] compressImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String formatName = getFileExtension(file.getOriginalFilename());

        if (formatName == null || (!formatName.equalsIgnoreCase("jpg") &&
                !formatName.equalsIgnoreCase("jpeg") &&
                !formatName.equalsIgnoreCase("png"))) {
            throw new IllegalArgumentException("Unsupported image format");
        }

        // Compress the image (e.g., 0.5f for 50% quality reduction)
        Thumbnails.of(originalImage)
                .size(800, 600)
                .outputFormat(formatName)
                .outputQuality(0.75)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        String[] parts = filename.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1] : null;
    }

    public void uploadProfilePicture(User user, MultipartFile file) throws Exception {

        byte[] compressedImage = compressImage(file);
        Picture newProfilePicture = new Picture();
        newProfilePicture.setImageData(compressedImage);

        // Check if the user already has a profile photo
        if (user.getProfilePicture() != null) {
            Picture oldProfilePicture = user.getProfilePicture();
            // Remove the old profile photo (orphanRemoval will handle the actual deletion)
            user.setProfilePicture(null);
            pictureRepository.delete(oldProfilePicture);
        }


        user.setProfilePicture(newProfilePicture);
    }

    public void uploadPostPicture(Post post, List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {
            byte[] compressedImage = compressImage(file);

            Picture postPicture = new Picture();
            postPicture.setImageData(compressedImage);

            if(post.getPictureList().isEmpty()) {
                List<Picture> pictures = new ArrayList<>();
                pictures.add(postPicture);
                post.setPictureList(pictures);
            }else{
                post.getPictureList().add(postPicture);
            }
        }
    }

}

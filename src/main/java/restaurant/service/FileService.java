package restaurant.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${upload.path}")
    private String uploadPath;

    public String saveAvatar(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;

        Path root = Paths.get(uploadPath);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path targetPath = root.resolve(fileName);

        Thumbnails.of(file.getInputStream())
                .size(300, 300)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toFile(targetPath.toFile());

        return "/" + uploadPath + "/" + fileName;
    }

    private String createUploadDirectory() throws IOException {
        Path root = Paths.get(uploadPath);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        return UUID.randomUUID().toString() + ".jpg";
    }

    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String fileName = createUploadDirectory();
        Path targetPath = Paths.get(uploadPath).resolve(fileName);

        Thumbnails.of(file.getInputStream())
                .scale(1.0)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toFile(targetPath.toFile());

        return "/" + uploadPath + "/" + fileName;
    }

    public void deleteOldFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.contains("default-avatar")) {
            return;
        }

        try {
            String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path pathToDelete = Paths.get(relativePath);

            Files.deleteIfExists(pathToDelete);
            System.out.println(">> Đã xóa ảnh cũ thành công: " + relativePath);
        } catch (IOException e) {
            System.err.println(">> Không thể xóa ảnh: " + e.getMessage());
        }
    }
}
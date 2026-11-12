package com.example.lms_mini.service.impl;

import com.example.lms_mini.exception.StorageException;
import com.example.lms_mini.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation; // Đường dẫn tới folder 'uploads/'

    // Tự động inject giá trị từ application.yml
    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);

        // Tạo folder 'uploads/' nếu nó chưa tồn tại
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("resource.cannot_create_dir");
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("resource.empty.cannot_save");
        }

        // 1. Lấy tên file gốc (ví dụ: "my_avatar.png")
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // 2. Tạo tên file DUY NHẤT
        // (Rất quan trọng, để tránh 2 người cùng upload file "avatar.png")
        // Kết quả: "123e4567-e89b-12d3-a456-426614174000.png"
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + fileExtension;

        try {
            // 3. Resolve đường dẫn tuyệt đối (ví dụ: /home/project/uploads/123e4567.png)
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            // 4. Copy file vào thư mục đích
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // 5. Trả về TÊN FILE DUY NHẤT (để lưu vào CSDL)
            return uniqueFilename;

        } catch (IOException e) {
            throw new StorageException("");
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Không thể đọc file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Lỗi URL khi đọc file: " + filename);
        }
    }
}

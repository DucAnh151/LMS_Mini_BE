package com.example.lms_mini.controller;

import com.example.lms_mini.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files") // Base URL cho file
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // 1. Load file từ folder uploads (dùng service đã viết hôm qua)
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // 2. Xác định Content-Type (image/jpeg, image/png...)
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Ignore
        }

        if (contentType == null) {
            contentType = "application/octet-stream"; // Mặc định nếu không nhận ra
        }

        // 3. Trả về file
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // Dòng dưới này để trình duyệt hiển thị ảnh luôn (inline) thay vì tải về
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

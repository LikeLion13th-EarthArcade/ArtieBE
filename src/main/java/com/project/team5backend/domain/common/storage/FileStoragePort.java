package com.project.team5backend.domain.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String upload(MultipartFile file, String dirName);
    void delete(String fileKey);
    void moveToTrash(String fileKey);
}
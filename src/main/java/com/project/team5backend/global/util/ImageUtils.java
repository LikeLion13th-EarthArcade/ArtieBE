package com.project.team5backend.global.util;

import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageUtils {
    private static final int MAX_IMAGE_COUNT = 5;

    public static void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND_IN_DTO);
        }
        if (images.size() > MAX_IMAGE_COUNT) {
            throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);
        }
    }
}
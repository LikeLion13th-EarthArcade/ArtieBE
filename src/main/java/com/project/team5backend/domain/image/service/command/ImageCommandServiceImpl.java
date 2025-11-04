package com.project.team5backend.domain.image.service.command;

import com.project.team5backend.domain.common.storage.FileStoragePort;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageCommandServiceImpl implements ImageCommandService {

    private final FileStoragePort fileStoragePort;

    @Override
    public void deleteImages(List<String> fileKeys) {
        try {
            fileKeys.forEach(fileStoragePort::moveToTrash);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }
}
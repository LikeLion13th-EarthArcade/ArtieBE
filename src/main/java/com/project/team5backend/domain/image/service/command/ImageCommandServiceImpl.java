package com.project.team5backend.domain.image.service.command;

import com.project.team5backend.domain.common.storage.FileStoragePort;
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
        fileKeys.forEach(fileStoragePort::moveToTrash);
    }
}
package com.project.team5backend.domain.image;

import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpaceImageReader {

    private final SpaceImageRepository spaceImageRepository;
    private final FileUrlResolverPort fileUrlResolverPort;

    public List<String> readSpaceImageUrls(Long spaceId) {
        return spaceImageRepository.findImageUrlsBySpaceId(spaceId).stream()
                .map(fileUrlResolverPort::toFileUrl)
                .toList();
    }
}

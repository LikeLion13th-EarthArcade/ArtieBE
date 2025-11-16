package com.project.team5backend.domain.image;

import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExhibitionImageReader {

    private final ExhibitionImageRepository exhibitionImageRepository;
    private final FileUrlResolverPort fileUrlResolverPort;

    public List<String> getExhibitionImageUrls(Long exhibitionId) {
        return exhibitionImageRepository.findImageUrlsByExhibitionId(exhibitionId).stream()
                .map(fileUrlResolverPort::toFileUrl)
                .toList();
    }
}

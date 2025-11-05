package com.project.team5backend.domain.common.storage;

public interface FileUrlResolverPort {
    String toFileUrl(String fileKeyOrUrl); // Key → URL 변환
    String toFileKey(String imageUrl);      // URL → Key 변환
}

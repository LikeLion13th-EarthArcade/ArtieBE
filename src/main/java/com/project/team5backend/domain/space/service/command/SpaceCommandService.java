package com.project.team5backend.domain.space.service.command;


import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SpaceCommandService {
    SpaceResDTO.SpaceCreateResDTO createSpace(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO,
                                              Long userId,
                                              MultipartFile businessLicenseFile,
                                              MultipartFile buildingRegisterFile,
                                              List<MultipartFile> images);

    void deleteSpace(Long spaceId, Long userId);

    SpaceResDTO.SpaceLikeResDTO toggleLike(Long spaceId, Long userId);
}
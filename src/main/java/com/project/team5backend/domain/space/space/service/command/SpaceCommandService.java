package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.space.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SpaceCommandService {
    SpaceResDTO.CreateSpaceResDTO createSpace(SpaceReqDTO.CreateSpaceReqDTO createSpaceReqDTO, long userId, List<MultipartFile> urls);

    void deleteSpace(long spaceId, long userId);
//    boolean toggleLike(Long spaceId, Long userId);
//    void approveSpace(Long spaceId);
}
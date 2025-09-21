package com.project.team5backend.domain.space.service.command;


import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SpaceCommandService {
    SpaceResDTO.SpaceCreateResDTO createSpace(SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO, long userId, List<MultipartFile> urls);

    void deleteSpace(long spaceId, long userId);

    SpaceResDTO.SpaceLikeResDTO toggleLike(long spaceId, long userId);
}
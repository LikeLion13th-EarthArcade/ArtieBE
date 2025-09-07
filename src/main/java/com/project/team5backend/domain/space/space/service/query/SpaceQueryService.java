package com.project.team5backend.domain.space.space.service.query;


import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;

public interface SpaceQueryService {
    SpaceResDTO.DetailSpaceResDTO getSpaceDetail(long spaceId);
}
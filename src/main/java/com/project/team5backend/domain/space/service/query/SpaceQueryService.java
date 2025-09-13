package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;

import java.time.LocalDate;
import java.util.List;

public interface SpaceQueryService {
    SpaceResDTO.SpaceDetailResDTO getSpaceDetail(long spaceId);

    SpaceResDTO.SearchSpacePageResDTO searchSpace(LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size, SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, int page);
}
package com.project.team5backend.domain.space.space.service.query;

import com.project.team5backend.domain.facility.entity.enums.FacilityType;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.space.entity.enums.SpaceType;

import java.time.LocalDate;
import java.util.List;

public interface SpaceQueryService {
    SpaceResDTO.DetailSpaceResDTO getSpaceDetail(long spaceId);

    SpaceResDTO.SearchSpacePageResDTO searchSpace(LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size, SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, int page);
}
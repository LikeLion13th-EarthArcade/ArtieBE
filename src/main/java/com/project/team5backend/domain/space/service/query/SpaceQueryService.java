package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SpaceQueryService {
    SpaceResDTO.SpaceDetailResDTO getSpaceDetail(long spaceId);

    SpaceResDTO.SpaceSearchPageResDTO searchSpace(LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size, SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, int page);

    Page<SpaceResDTO.SpaceLikeSummaryResDTO> getInterestedSpaces(long userId, Pageable pageable);

    Page<SpaceResDTO.SpaceDetailResDTO> getMySpace(long userId, StatusGroup statusGroup, Pageable pageable);

    SpaceResDTO.MySpaceDetailResDTO getMySpaceDetail(long userId, long spaceId);
}
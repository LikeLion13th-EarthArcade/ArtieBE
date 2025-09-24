package com.project.team5backend.domain.space.repository;


import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.entity.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SpaceRepositoryCustom {
    Page<Space> findSpacesWithFilters(LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size, SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, Pageable pageable);

    Page<Space> findAdminSpacesByStatus(StatusGroup status, Pageable pageable);

    Page<Space> findByUserWithFilters(User user, StatusGroup statusGroup, Pageable pageable);

}

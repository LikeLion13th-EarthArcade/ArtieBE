package com.project.team5backend.domain.admin.space.service.query;

import com.project.team5backend.domain.admin.space.converter.AdminSpaceConverter;
import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.global.entity.enums.StatusGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminSpaceQueryImpl implements AdminSpaceQueryService {

    private final SpaceRepository spaceRepository;

    private static final int PAGE_SIZE = 10;

    @Override
    public Page<AdminSpaceResDTO.adminSpaceDetailResDTO> getAdminSpaceList(StatusGroup status, int page){
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        Page<Space> spacePage = spaceRepository.findAdminSpacesByStatus(status, pageable);

        return spacePage.map(AdminSpaceConverter::toDetailAdminSpaceResDTO);
    }
}

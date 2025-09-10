package com.project.team5backend.domain.admin.space.service.command;

import com.project.team5backend.domain.admin.space.converter.AdminSpaceConverter;
import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminSpaceCommandServiceImpl implements AdminSpaceCommandService {

    private final SpaceRepository spaceRepository;

    @Override
    public AdminSpaceResDTO.SpaceStatusUpdateResDTO approveSpace(long spaceId){
        Space space = spaceRepository.findByIdAndIsDeletedFalse(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        space.approveSpace();
        return AdminSpaceConverter.toSpaceStatusUpdateResDTO(space, "해당 공간이 승인되었습니다.");
    }

    @Override
    public AdminSpaceResDTO.SpaceStatusUpdateResDTO rejectSpace(long spaceId){
        Space space = spaceRepository.findByIdAndIsDeletedFalse(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        space.rejectSpace();
        return AdminSpaceConverter.toSpaceStatusUpdateResDTO(space, "해당 공간이 거절되었습니다.");
    }
}

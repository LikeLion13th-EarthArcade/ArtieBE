package com.project.team5backend.domain.admin.space.service.command;

import com.project.team5backend.domain.admin.space.converter.AdminSpaceConverter;
import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.space.SpaceReader;
import com.project.team5backend.domain.space.entity.Space;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminSpaceCommandServiceImpl implements AdminSpaceCommandService {

    private final SpaceReader spaceReader;

    @Override
    public AdminSpaceResDTO.SpaceStatusUpdateResDTO approveSpace(Long spaceId){
        Space space = spaceReader.readSpace(spaceId);

        space.approveSpace();
        return AdminSpaceConverter.toSpaceStatusUpdateResDTO(space, "해당 공간이 승인되었습니다.");
    }

    @Override
    public AdminSpaceResDTO.SpaceStatusUpdateResDTO rejectSpace(Long spaceId){
        Space space = spaceReader.readSpace(spaceId);

        space.rejectSpace();
        return AdminSpaceConverter.toSpaceStatusUpdateResDTO(space, "해당 공간이 거절되었습니다.");
    }
}

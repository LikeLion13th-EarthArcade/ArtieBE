package com.project.team5backend.domain.space;

import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpaceReader {

    private final SpaceRepository spaceRepository;

    public Space readSpace(Long spaceId){
        return spaceRepository.findByIdAndIsDeletedFalse(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));
    };

    public Space readApprovedSpace(Long spaceId){
        return spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));
    }
}

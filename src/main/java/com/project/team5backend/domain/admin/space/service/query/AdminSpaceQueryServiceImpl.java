package com.project.team5backend.domain.admin.space.service.query;

import com.project.team5backend.domain.admin.space.converter.AdminSpaceConverter;
import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.common.enums.StatusGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminSpaceQueryServiceImpl implements AdminSpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final FileUrlResolverPort fileUrlResolverPort;

    private static final int PAGE_SIZE = 10;

    @Override
    public Page<AdminSpaceResDTO.SpaceSummaryResDTO> getSpaceList(StatusGroup status, int page){
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        Page<Space> spacePage = spaceRepository.findAdminSpacesByStatus(status, pageable);

        return spacePage.map(AdminSpaceConverter::toSpaceSummaryResDTO);
    }

    @Override
    public AdminSpaceResDTO.SpaceDetailResDTO getDetailSpace(Long spaceId){
        Space space = getSpace(spaceId);

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId).stream()
                .map(fileUrlResolverPort::toFileUrl)
                .toList();

        SpaceVerification spaceVerification = space.getSpaceVerification();
        String businessLicenseFile = fileUrlResolverPort.toFileUrl(spaceVerification.getBusinessLicenseKey());
        String buildingRegisterFile = fileUrlResolverPort.toFileUrl(spaceVerification.getBuildingRegisterKey());

        return AdminSpaceConverter.toSpaceDetailResDTO(space, spaceVerification, imageUrls, businessLicenseFile, buildingRegisterFile);
    }

    private Space getSpace(Long spaceId) {
        return spaceRepository.findByIdAndIsDeletedFalse(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));
    }
}

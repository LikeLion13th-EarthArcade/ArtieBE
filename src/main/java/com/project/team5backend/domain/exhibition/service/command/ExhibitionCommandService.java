package com.project.team5backend.domain.exhibition.service.command;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExhibitionCommandService {

    ExhibitionResDTO.ExhibitionCreateResDTO createExhibition(ExhibitionReqDTO.ExhibitionCreateReqDTO exhibitionCreateReqDTO, Long userId, List<MultipartFile> images);

    ExhibitionResDTO.ExhibitionLikeResDTO toggleLike(Long exhibitionId, Long userId);

    void deleteExhibition(Long exhibitionId, Long userId);
}

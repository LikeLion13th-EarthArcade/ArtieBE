package com.project.team5backend.domain.exhibition.service.command;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExhibitionCommandService {

    void createExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO, Long userId, List<MultipartFile> images);

    ExhibitionResDTO.LikeExhibitionResDTO likeExhibition(Long exhibitionId, String email);

    void deleteExhibition(Long exhibitionId, String email);
}

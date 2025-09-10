package com.project.team5backend.domain.admin.exhibition.converter;

import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;

import java.util.List;

public class AdminExhibitionConverter {
    public static AdminExhibitionResDTO.ExhibitionSummaryResDTO toExhibitionSummaryResDTO(Exhibition exhibition){
        return AdminExhibitionResDTO.ExhibitionSummaryResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .createdAt(exhibition.getCreatedAt())
                .status(exhibition.getStatus())
                .build();
    }

    public static AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO toExhibitionStatusUpdateResDTO(Exhibition exhibition, String message){
        return AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO.builder()
                .exhibitionId(exhibition.getId())
                .status(exhibition.getStatus())
                .message(message)
                .build();
    }

    public static AdminExhibitionResDTO.ExhibitionDetailResDTO toExhibitionDetailResDTO(Exhibition exhibition, List<String> imageUrls){
        return AdminExhibitionResDTO.ExhibitionDetailResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .imageUrls(imageUrls)
                .description(exhibition.getDescription())
                .openTime(exhibition.getOpenTime())
                .closeTime(exhibition.getCloseTime())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .detailAddress(exhibition.getAddress() != null ? exhibition.getAddress().getDetail() : null)
                .address(exhibition.getAddress() != null ? exhibition.getAddress().getRoadAddress() : null)
                .exhibitionCategory(exhibition.getExhibitionCategory())
                .exhibitionType(exhibition.getExhibitionType())
                .description(exhibition.getDescription())
                .price(exhibition.getPrice())
                .websiteUrl(exhibition.getWebsiteUrl())
                .build();
    }
}

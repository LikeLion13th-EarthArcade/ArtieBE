package com.project.team5backend.domain.exhibition.converter;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.util.PageResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionConverter {

    public static Exhibition toEntity(ExhibitionReqDTO.ExhibitionCreateReqDTO createReqDTO, User user, String imageUrls, Address address) {
        return Exhibition.builder()
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .openTime(createReqDTO.openTime())
                .closeTime(createReqDTO.closeTime())
                .price(createReqDTO.price())
                .websiteUrl(createReqDTO.websiteUrl())
                .status(Status.PENDING)
                .exhibitionCategory(createReqDTO.exhibitionCategory())
                .exhibitionType(createReqDTO.exhibitionType())
                .exhibitionMood(createReqDTO.exhibitionMood())
                .isDeleted(false)
                .ratingAvg(0.0)
                .likeCount(0)
                .reviewCount(0)
                .reviewSum(0)
                .thumbnail(imageUrls)
                .address(address)
                .user(user)
                .build();
    }

    public static ExhibitionResDTO.ExhibitionCreateResDTO toExhibitionCreateResDTO(Long exhibitionId, LocalDateTime createdAt) {
        return ExhibitionResDTO.ExhibitionCreateResDTO.builder()
                .exhibitionId(exhibitionId)
                .createdAt(createdAt)
                .build();
    }

    public static ExhibitionFacility toCreateExhibitionFacility(Exhibition exhibition, Facility facility){
        return ExhibitionFacility.builder()
                .exhibition(exhibition)
                .facility(facility)
                .build();
    }

    public static ExhibitionResDTO.ExhibitionDetailResDTO toExhibitionDetailResDTO(Exhibition exhibition, List<String> imageUrls) {
        return ExhibitionResDTO.ExhibitionDetailResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .openTime(exhibition.getOpenTime())
                .closeTime(exhibition.getCloseTime())
                .imageUrls(imageUrls)
                .websiteUrl(exhibition.getWebsiteUrl())
                .address(
                        exhibition.getAddress() != null
                                ? String.format("%s %s",
                                exhibition.getAddress().getRoadAddress(),
                                exhibition.getAddress().getDetail() != null ? exhibition.getAddress().getDetail() : "")
                                : null
                )
                .latitude(exhibition.getAddress().getLatitude())
                .longitude(exhibition.getAddress().getLongitude())
                .exhibitionCategory(exhibition.getExhibitionCategory())
                .exhibitionType(exhibition.getExhibitionType())
                .exhibitionMood(exhibition.getExhibitionMood())
                .price(exhibition.getPrice())
                .facilities(
                        exhibition.getExhibitionFacilities().stream()
                                .map(ef -> ef.getFacility().getName()) // Facility 엔티티의 name 사용
                                .toList()
                )
                .build();
    }

    public static ExhibitionResDTO.DetailPendingExhibitionResDTO toDetailPendingExhibitionResDTO(Exhibition exhibition, List<String> imageFileKeys) {
        return ExhibitionResDTO.DetailPendingExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .openTime(exhibition.getOpenTime())
                .closeTime(exhibition.getCloseTime())
                .imageFileKeys(imageFileKeys)
                .websiteUrl(exhibition.getWebsiteUrl())
                .address(
                        exhibition.getAddress() != null ? exhibition.getAddress().toString() : null
                )
                .latitude(exhibition.getAddress().getLatitude())
                .longitude(exhibition.getAddress().getLongitude())
                .exhibitionCategory(exhibition.getExhibitionCategory())
                .exhibitionType(exhibition.getExhibitionType())
                .exhibitionMood(exhibition.getExhibitionMood())
                .price(exhibition.getPrice())
                .facilities(
                        exhibition.getExhibitionFacilities().stream()
                                .map(ef -> ef.getFacility().getName()) // Facility 엔티티의 name 사용
                                .toList()
                )
                .build();
    }

    public static ExhibitionResDTO.ExhibitionSearchResDTO toExhibitionSearchResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.ExhibitionSearchResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .address(exhibition.getAddress().getRoadAddress() + " " + exhibition.getAddress().getDetail())
                .latitude(exhibition.getAddress().getLatitude())
                .longitude(exhibition.getAddress().getLongitude())
                .build();
    }

    public static ExhibitionResDTO.ExhibitionSearchPageResDTO toExhibitionSearchPageResDTO(PageResponse<ExhibitionResDTO.ExhibitionSearchResDTO> page, Double lat, Double lon) {
        return ExhibitionResDTO.ExhibitionSearchPageResDTO.builder()
                .page(page)
                .map(new ExhibitionResDTO.ExhibitionSearchPageResDTO.MapInfo(lat, lon))
                .build();
    }

    public static ExhibitionResDTO.ExhibitionHotNowResDTO toExhibitionHotNowResDTO(Exhibition exhibition, boolean isLiked) {
        return ExhibitionResDTO.ExhibitionHotNowResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .thumbnail(exhibition.getThumbnail())
                .category(String.valueOf(exhibition.getExhibitionCategory()))
                .mood(String.valueOf(exhibition.getExhibitionMood()))
                .location(exhibition.getAddress().getRoadAddress() + exhibition.getAddress().getDetail())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .reviewAvg(BigDecimal.valueOf(exhibition.getRatingAvg())
                        .setScale(1, RoundingMode.HALF_UP)
                        .doubleValue())
                .reviewCount(exhibition.getReviewCount())
                .isLiked(isLiked)
                .build();
    }

    public static ExhibitionResDTO.UpcomingPopularExhibitionResDTO toUpcomingPopularExhibitionResDTO(
            Long exhibitionId, String title, List<String> imageUrls
    ) {
        return ExhibitionResDTO.UpcomingPopularExhibitionResDTO.builder()
                .exhibitionId(exhibitionId)
                .title(title)
                .imagesUrls(imageUrls)
                .build();
    }

    public static ExhibitionResDTO.RegionalPopularExhibitionListResDTO toRegionalPopularExhibitionListResDTO(List<ExhibitionResDTO.RegionalPopularExhibitionResDTO> exhibitions) {
        return ExhibitionResDTO.RegionalPopularExhibitionListResDTO.builder()
                .exhibitions(exhibitions)
                .build();
    }

    public static ExhibitionResDTO.RegionalPopularExhibitionResDTO toRegionalPopularExhibitionResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.RegionalPopularExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .district(exhibition.getAddress().getDistrict())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .build();
    }

    public static ExhibitionResDTO.ArtieRecommendationResDTO toArtieRecommendationResDTO(Exhibition exhibition,boolean isLiked) {
        return ExhibitionResDTO.ArtieRecommendationResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .thumbnail(exhibition.getThumbnail())
                .category(String.valueOf(exhibition.getExhibitionCategory()))
                .mood(String.valueOf(exhibition.getExhibitionMood()))
                .location(exhibition.getAddress().getRoadAddress() + exhibition.getAddress().getDetail())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .reviewAvg(exhibition.getRatingAvg())
                .reviewCount(exhibition.getReviewCount())
                .isLiked(isLiked)
                .build();
    }

    //ai 추천 결과 관련 컨버터
    public static ExhibitionResDTO.ExhibitionCardResDTO toCard(Exhibition e, boolean isLiked) {
        double avg = BigDecimal.valueOf(e.getRatingAvg())
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return ExhibitionResDTO.ExhibitionCardResDTO.builder()
                .exhibitionId(e.getId())
                .title(e.getTitle())
                .description(e.getDescription()) // description 필드가 따로 있으면 교체
                .thumbnail(e.getThumbnail())
                .category(e.getExhibitionCategory().name())
                .mood(e.getExhibitionMood().name())
                .location(e.getAddress().getRoadAddress())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .reviewAvg(avg)
                .reviewCount(e.getReviewCount())
                .isLiked(isLiked)
                .build();
    }
}

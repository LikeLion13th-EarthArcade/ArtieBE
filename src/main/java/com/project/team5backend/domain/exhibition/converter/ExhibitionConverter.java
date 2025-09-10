package com.project.team5backend.domain.exhibition.converter;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionConverter {

    public static Exhibition toEntity(User user, ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO, Address address) {
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
                .ratingAvg(BigDecimal.ZERO)
                .likeCount(0)
                .reviewCount(0)
                .thumbnail(null)
                .address(address)
                .user(user)
                .build();
    }

    public static ExhibitionFacility toCreateExhibitionFacility(Exhibition exhibition, Facility facility){
        return ExhibitionFacility.builder()
                .exhibition(exhibition)
                .facility(facility)
                .build();
    }

    public static ExhibitionResDTO.DetailExhibitionResDTO toDetailExhibitionResDTO(Exhibition exhibition, List<String> imageFileKeys, List<ExhibitionReviewResDTO.exReviewDetailResDTO> reviews) {
        return ExhibitionResDTO.DetailExhibitionResDTO.builder()
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
                .reviews(reviews)
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

    public static ExhibitionResDTO.SearchExhibitionResDTO toSearchExhibitionResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.SearchExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .address(exhibition.getAddress().getRoadAddress() + exhibition.getAddress().getDetail())
                .latitude(exhibition.getAddress().getLatitude())
                .longitude(exhibition.getAddress().getLongitude())
                .build();
    }

    public static ExhibitionResDTO.SearchExhibitionPageResDTO toSearchExhibitionPageResDTO(
            List<ExhibitionResDTO.SearchExhibitionResDTO> items,
            Page<?> page,
            Double defaultCenterLat,
            Double defaultCenterLng) {

        // PageInfo 생성
        ExhibitionResDTO.SearchExhibitionPageResDTO.PageInfo pageInfo =
                new ExhibitionResDTO.SearchExhibitionPageResDTO.PageInfo(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isFirst(),
                        page.isLast()
                );

        // MapInfo 생성
        ExhibitionResDTO.SearchExhibitionPageResDTO.MapInfo mapInfo =
                new ExhibitionResDTO.SearchExhibitionPageResDTO.MapInfo(
                        defaultCenterLat,
                        defaultCenterLng
                );

        return new ExhibitionResDTO.SearchExhibitionPageResDTO(items, pageInfo, mapInfo);
    }

    public static ExhibitionResDTO.HotNowExhibitionResDTO toHotNowExhibitionResDTO(Exhibition exhibition, boolean isLiked) {
        return ExhibitionResDTO.HotNowExhibitionResDTO.builder()
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

    public static ExhibitionResDTO.UpcomingPopularityExhibitionResDTO toUpcomingPopularityExhibitionResDTO(
            Long exhibitionId, String title, List<String> fileKeys
    ) {
        return ExhibitionResDTO.UpcomingPopularityExhibitionResDTO.builder()
                .exhibitionId(exhibitionId)
                .title(title)
                .images(fileKeys)
                .build();
    }

    public static ExhibitionResDTO.PopularRegionExhibitionListResDTO toPopularRegionExhibitionListResDTO(List<ExhibitionResDTO.PopularRegionExhibitionResDTO> exhibitions) {
        return ExhibitionResDTO.PopularRegionExhibitionListResDTO.builder()
                .exhibitions(exhibitions)
                .build();
    }

    public static ExhibitionResDTO.PopularRegionExhibitionResDTO toPopularRegionExhibitionResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.PopularRegionExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
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
        var avg = (e.getRatingAvg()==null)
                ? java.math.BigDecimal.ZERO
                : e.getRatingAvg().setScale(1, java.math.RoundingMode.HALF_UP);

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
                .reviewCount(e.getReviewCount()==null? 0 : e.getReviewCount())
                .isLiked(isLiked)
                .build();
    }
}

package com.project.team5backend.domain.exhibition.converter;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.PageResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionConverter {

    public static Exhibition toExhibition(ExhibitionReqDTO.ExhibitionCreateReqDTO createReqDTO, User user, String imageUrls, Address address) {
        return Exhibition.builder()
                .portalExhibitionId(null)
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .operatingInfo(createReqDTO.operatingStartHour() + "/" + createReqDTO.operatingEndHour() + "/" + createReqDTO.operatingOption())
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

    public static ExhibitionFacility toCreateExhibitionFacility(Exhibition exhibition, Facility facility) {
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
                .operatingInfo(exhibition.getOperatingInfo())
                .imageUrls(imageUrls)
                .websiteUrl(exhibition.getWebsiteUrl())
                .address(formatAddress(exhibition.getAddress()))
                .latitude(exhibition.getAddress().getLatitude())
                .longitude(exhibition.getAddress().getLongitude())
                .exhibitionCategory(exhibition.getExhibitionCategory())
                .exhibitionType(exhibition.getExhibitionType())
                .exhibitionMood(exhibition.getExhibitionMood())
                .price(exhibition.getPrice())
                .facilities(extractFacility(exhibition))
                .build();
    }

    public static ExhibitionResDTO.ExhibitionSearchResDTO toExhibitionSearchResDTO(Exhibition exhibition, String thumbnail) {
        return ExhibitionResDTO.ExhibitionSearchResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(thumbnail)
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

    public static ExhibitionResDTO.ExhibitionHotNowResDTO toExhibitionHotNowResDTO(Exhibition exhibition, boolean isLiked, String thumbnail) {
        return ExhibitionResDTO.ExhibitionHotNowResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .thumbnail(thumbnail)
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

    public static ExhibitionResDTO.RegionalPopularExhibitionResDTO toRegionalPopularExhibitionResDTO(Exhibition exhibition, String thumbnail) {
        return ExhibitionResDTO.RegionalPopularExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .district(exhibition.getAddress().getDistrict())
                .title(exhibition.getTitle())
                .thumbnail(thumbnail)
                .build();
    }

    public static ExhibitionResDTO.ArtieRecommendationResDTO toArtieRecommendationResDTO(Exhibition exhibition, boolean isLiked, String thumbnail) {
        return ExhibitionResDTO.ArtieRecommendationResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .thumbnail(thumbnail)
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

    public static ExhibitionResDTO.ExhibitionSummaryResDTO toExhibitionSummaryResDTO(Exhibition exhibition){
        return ExhibitionResDTO.ExhibitionSummaryResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .createdAt(exhibition.getCreatedAt())
                .status(exhibition.getStatus())
                .build();
    }

    public static ExhibitionResDTO.MyExhibitionDetailResDTO toMyExhibitionDetailResDTO(Exhibition exhibition, List<String> imageUrls){
        return ExhibitionResDTO.MyExhibitionDetailResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .imageUrls(imageUrls)
                .description(exhibition.getDescription())
                .operatingInfo(exhibition.getOperatingInfo())
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

    //ai 추천 결과 관련 컨버터
    public static ExhibitionResDTO.ExhibitionCardResDTO toCard(Exhibition e, boolean isLiked, String thumbnail) {
        double avg = BigDecimal.valueOf(e.getRatingAvg())
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return ExhibitionResDTO.ExhibitionCardResDTO.builder()
                .exhibitionId(e.getId())
                .title(e.getTitle())
                .description(e.getDescription()) // description 필드가 따로 있으면 교체
                .thumbnail(thumbnail)
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

    public static ExhibitionResDTO.ExhibitionLikeSummaryResDTO toExhibitionLikeSummaryResDTO(Exhibition exhibition, String thumbnail, boolean isLiked) {
        return ExhibitionResDTO.ExhibitionLikeSummaryResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(thumbnail)
                .exhibitionCategory(exhibition.getExhibitionCategory())
                .exhibitionType(exhibition.getExhibitionType())
                .exhibitionMood(exhibition.getExhibitionMood())
                .isLiked(isLiked)
                .build();
    }

    // 크롤링 전용 dto
    public static Exhibition toExhibitionCrawl(ExhibitionResDTO.ExhibitionCrawlResDto exhibitionCrawlResDto, Address address, ExhibitionResDTO.ExhibitionEnumResDTO exhibitionEnumResDTO, User user) {
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        return Exhibition.builder()
                .portalExhibitionId(Long.valueOf(exhibitionCrawlResDto.portalExhibitionId()))
                .title(exhibitionCrawlResDto.title())
                .description(null)
                .startDate(LocalDate.parse(exhibitionCrawlResDto.startDate(), formatter))
                .endDate(LocalDate.parse(exhibitionCrawlResDto.endDate(), formatter))
                .operatingInfo("정보 없음")
                .price(null)
                .websiteUrl(null)
                .status(Status.PENDING)
                .exhibitionCategory(exhibitionEnumResDTO.exhibitionCategory())
                .exhibitionType(exhibitionEnumResDTO.exhibitionType())
                .exhibitionMood(exhibitionEnumResDTO.exhibitionMood())
                .isDeleted(false)
                .ratingAvg(0.0)
                .likeCount(0)
                .reviewCount(0)
                .reviewSum(0)
                .thumbnail(exhibitionCrawlResDto.thumbnail())
                .address(address)
                .user(user)
                .build();
    }

    private static List<String> extractFacility(Exhibition exhibition) {
        if (exhibition.getExhibitionFacilities() == null) {
            return List.of(); // null-safe
        }
        return exhibition.getExhibitionFacilities().stream()
                .map(ef -> ef.getFacility().getName())
                .toList();
    }

    private static String formatAddress(Address address) {
        if (address == null) return null;
        return String.format("%s %s",
                Objects.toString(address.getRoadAddress(), ""),
                Objects.toString(address.getDetail(), "")
        ).trim();
    }
}

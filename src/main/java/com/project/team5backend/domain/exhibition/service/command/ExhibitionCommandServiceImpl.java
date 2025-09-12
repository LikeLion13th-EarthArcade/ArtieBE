package com.project.team5backend.domain.exhibition.service.command;

import com.project.team5backend.domain.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.converter.ExhibitionLikeConverter;
import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.repository.FacilityRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.global.apiPayload.code.GeneralErrorCode;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.ImageUtils;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionCommandServiceImpl implements ExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final FacilityRepository facilityRepository;
    private final ImageCommandService imageCommandService;
    private final AddressService addressService;
    private final InteractLogService interactLogService;
    private final S3Uploader s3Uploader;

    @Override
    public ExhibitionResDTO.ExhibitionCreateResDTO createExhibition(ExhibitionReqDTO.ExhibitionCreateReqDTO exhibitionCreateReqDTO, Long userId, List<MultipartFile> images) {
        ImageUtils.validateImages(images); // 이미지 검증 (개수, null 여부)

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.NOT_FOUND_404));
        // 주소 변환
        AddressResDTO.AddressCreateResDTO addressResDTO = addressService.resolve(exhibitionCreateReqDTO.address());
        Address address = AddressConverter.toAddress(addressResDTO);

        // 업로드 및 image 획득
        List<String> imageUrls = images.stream()
                .map(file -> s3Uploader.upload(file, "exhibitions"))
                .toList();

        // 전시 엔티티 먼저 저장
        Exhibition exhibition = ExhibitionConverter.toEntity(exhibitionCreateReqDTO, user, imageUrls.get(0),address);
        exhibitionRepository.save(exhibition);

        // 시설 매핑 (문자열 → Facility 엔티티 조회 → ExhibitionFacility 생성)
        List<Facility> facilities = facilityRepository.findByNameIn(exhibitionCreateReqDTO.facilities());
        facilities.forEach(facility -> {
            ExhibitionFacility ef = ExhibitionConverter.toCreateExhibitionFacility(exhibition, facility);
            exhibition.getExhibitionFacilities().add(ef);
        });

        // Space 이미지 엔티티 저장
        for (String url : imageUrls) {
            exhibitionImageRepository.save(ImageConverter.toExhibitionImage(exhibition, url));
        }

        return ExhibitionConverter.toExhibitionCreateResDTO(exhibition.getId(), exhibition.getCreatedAt());
    }

    @Override
    public ExhibitionResDTO.LikeExhibitionResDTO likeExhibition(Long exhibitionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(()-> new CustomException(GeneralErrorCode.NOT_FOUND_404));

        if (exhibitionLikeRepository.existsByUserIdAndExhibitionId(user.getId(), exhibition.getId())) {
            //좋아요 취소
            exhibitionLikeRepository.deleteByUserIdAndExhibitionId(user.getId(), exhibitionId);
            exhibition.decreaseLikeCount();
            return ExhibitionLikeConverter.toLikeExhibitionResDTO(exhibitionId, "관심목록에서 삭제되었습니다.");
        }else {
            //좋아요 등록
            exhibitionLikeRepository.save(ExhibitionLikeConverter.toEntity(user, exhibition));
            exhibition.increaseLikeCount();

            interactLogService.logLike(user.getId(), exhibitionId);
            return ExhibitionLikeConverter.toLikeExhibitionResDTO(exhibitionId, "관심목록에 추가되었습니다.");
        }
    }

    @Override
    public void deleteExhibition(Long exhibitionId, String email) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprove(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        if (exhibition.isDeleted()) return;

        exhibition.delete();

        // 전시이미지 소프트 삭제
        List<ExhibitionImage> images = exhibitionImageRepository.findByExhibitionId(exhibitionId);
        images.forEach(ExhibitionImage::deleteImage);
        List<String> keys = images.stream().map(ExhibitionImage::getImageUrl).toList();
        // 3) 좋아요 하드 삭제 (벌크)
        exhibitionLikeRepository.deleteByExhibitionId(exhibitionId);

        // 4) 리뷰 소프트 삭제 (벌크)
        exhibitionReviewRepository.softDeleteByExhibitionId(exhibitionId);

        // 집계 초기화
        exhibition.resetCount();

        // s3 보존 휴지통 prefix로 이동시키기
        try{
            imageCommandService.moveToTrashPrefix(keys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }
}

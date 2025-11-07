package com.project.team5backend.domain.review.space.service.command;

import com.project.team5backend.domain.common.storage.FileStoragePort;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.SpaceReviewImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.review.space.converter.SpaceReviewConverter;
import com.project.team5backend.domain.review.space.dto.request.SpaceReviewReqDTO;
import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.review.space.exception.SpaceReviewErrorCode;
import com.project.team5backend.domain.review.space.exception.SpaceReviewException;
import com.project.team5backend.domain.review.space.repository.SpaceReviewRepository;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.domain.common.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpaceReviewCommandServiceImpl implements SpaceReviewCommandService {

    private final SpaceReviewRepository spaceReviewRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;;
    private final FileStoragePort fileStoragePort;
    private final SpaceReviewImageRepository spaceReviewImageRepository;
    private final ImageCommandService imageCommandService;

    @Override
    public SpaceReviewResDTO.SpaceReviewCreateResDTO createSpaceReview(Long spaceId, Long userId, SpaceReviewReqDTO.SpaceReviewCreateReqDTO spaceReviewCreateReqDTO, List<MultipartFile> images) {
        Space space = getActiveSpace(spaceId);
        User user = getActiveUser(userId);

        SpaceReview spaceReview = SpaceReviewConverter.toSpaceReview(spaceReviewCreateReqDTO, space, user);
        spaceReviewRepository.save(spaceReview);

        saveReviewImages(images, spaceReview);
        spaceRepository.applyReviewCreated(spaceId, spaceReview.getRate()); // 리뷰 생성에 따른 공간 별점 평균 업데이트
        return SpaceReviewConverter.toSpaceReviewCreateResDTO(spaceReview.getId());
    }

    @Override
    public void deleteSpaceReview(Long spaceReviewId, Long userId) {
        SpaceReview spaceReview = getActiveSpaceReview(spaceReviewId, userId);
        performsSoftDelete(spaceReview);
    }

    private void performsSoftDelete(SpaceReview spaceReview) {
        spaceReview.softDelete();
        spaceReview.getSpaceReviewImages().forEach(SpaceReviewImage::deleteImage);

        List<String> fileKeys = spaceReview.getSpaceReviewImages().stream()
                .map(SpaceReviewImage::getFileKey)
                .toList();

        spaceRepository.applySpaceReviewDeleted(spaceReview.getSpace().getId(), spaceReview.getRate());  // 리뷰 평균/카운트 갱신
        moveImagesToTrash(fileKeys);
    }

    private SpaceReview getActiveSpaceReview(Long spaceReviewId, Long userId) {
        return spaceReviewRepository.findByIdAndUserIdAndIsDeletedFalse(spaceReviewId, userId)
                .orElseThrow(() -> new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_NOT_FOUND));
    }

    private void saveReviewImages(List<MultipartFile> images, SpaceReview spaceReview) {
        List<SpaceReviewImage> reviewImages = Optional.ofNullable(images)
                .orElseGet(List::of)
                .stream()
                .map(file -> {
                    String url = fileStoragePort.upload(file, "spaceReviews");
                    return ImageConverter.toSpaceReviewImage(spaceReview, url);
                })
                .toList();

        if (!reviewImages.isEmpty()) {
            spaceReviewImageRepository.saveAll(reviewImages);
        }
    }

    private void moveImagesToTrash(List<String> fileKeys) {
        try {
            imageCommandService.deleteImages(fileKeys);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }

    private User getActiveUser(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Space getActiveSpace(Long spaceId) {
        return spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));
    }
}


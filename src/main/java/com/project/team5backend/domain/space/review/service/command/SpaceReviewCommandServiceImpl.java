package com.project.team5backend.domain.space.review.service.command;

import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.repository.SpaceReviewImageRepository;
import com.project.team5backend.domain.image.service.command.ImageCommandService;
import com.project.team5backend.domain.space.review.converter.SpaceReviewConverter;
import com.project.team5backend.domain.space.review.dto.request.SpaceReviewReqDTO;
import com.project.team5backend.domain.space.review.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.space.review.entity.SpaceReview;
import com.project.team5backend.domain.space.review.exception.SpaceReviewErrorCode;
import com.project.team5backend.domain.space.review.exception.SpaceReviewException;
import com.project.team5backend.domain.space.review.repository.SpaceReviewRepository;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SpaceReviewCommandServiceImpl implements SpaceReviewCommandService {

    private final SpaceReviewRepository spaceReviewRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final SpaceReviewImageRepository reviewImageRepository;
    private final S3Uploader s3Uploader;
    private final SpaceReviewImageRepository spaceReviewImageRepository;
    private final ImageCommandService imageCommandService;

    @Override
    public SpaceReviewResDTO.CreateSpaceReviewResDTO createSpaceReview(long spaceId, long userId, SpaceReviewReqDTO.CreateSpaceReviewReqDTO request, List<MultipartFile> images) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (images == null || images.isEmpty()) images = List.of();

        SpaceReview spaceReview = SpaceReviewConverter.toSpaceReview(request, space, user);
        spaceReviewRepository.save(spaceReview);

        List<SpaceReviewImage> spaceReviewImages = new ArrayList<>();
        for (MultipartFile file : images) {
            String url = s3Uploader.upload(file, "spaceReview"); // S3 URL
            SpaceReviewImage spaceReviewImage = ImageConverter.toSpaceReviewImage(spaceReview, url);
            spaceReviewImages.add(spaceReviewImage);
        }
        reviewImageRepository.saveAll(spaceReviewImages);

        // 리뷰 생성에 따른 공간 별점 평균 업데이트
        double rating = spaceReview.getRating();
        spaceRepository.applyReviewCreated(spaceId, rating);

        return SpaceReviewConverter.toCreateSpaceReviewResDTO(spaceReview.getId());
    }

    @Override
    public void deleteSpaceReview(long spaceReviewId, long userId) {
        SpaceReview spaceReview = spaceReviewRepository.findByIdAndIsDeletedFalse(spaceReviewId)
                .orElseThrow(() -> new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_NOT_FOUND));

        if (!spaceReview.getUser().getId().equals(userId)) {
            throw new SpaceReviewException(SpaceReviewErrorCode.SPACE_REVIEW_FORBIDDEN);
        }
        spaceReview.softDelete();

        List<SpaceReviewImage> images = spaceReviewImageRepository.findBySpaceReviewId(spaceReviewId);
        images.forEach(SpaceReviewImage::deleteImage);
        List<String> imageUrls = images.stream().map(SpaceReviewImage::getImageUrl).toList();

        // 리뷰 평균/카운트 갱신
        double rating = spaceReview.getRating();
        spaceRepository.applySpaceReviewDeleted(spaceReview.getSpace().getId(), rating);

        // s3 보존 휴지통 prefix로 이동시키기
        try{
            imageCommandService.moveToTrashPrefix(imageUrls);
        } catch (ImageException e) {
            throw new ImageException(ImageErrorCode.S3_MOVE_TRASH_FAIL);
        }
    }
}


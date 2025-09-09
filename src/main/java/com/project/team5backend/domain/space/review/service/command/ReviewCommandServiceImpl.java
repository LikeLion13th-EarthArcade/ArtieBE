package com.project.team5backend.domain.space.review.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {

//    private final SpaceReviewRepository spaceReviewRepository;
//    private final SpaceRepository spaceRepository;
//    private final UserRepository userRepository;
//    private final ReviewConverter reviewConverter;
//    private final SpaceReviewImageRepository reviewImageRepository;
//    private final S3Uploader s3Uploader;
//
//    @Override
//    public void createReview(Long spaceId, Long userId, ReviewRequest.CreateRe request, List<MultipartFile> images) {
//
//        Space space = spaceRepository.findById(spaceId)
//                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        if (images == null || images.isEmpty()) throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
//        if (images.size() > 5) throw new ImageException(ImageErrorCode.IMAGE_TOO_MANY_REQUESTS);
//
//        // S3 업로드 → URL 반환
//        List<String> fileUrls = new ArrayList<>();
//        for (MultipartFile file : images) {
//            String url = s3Uploader.upload(file, "spaceReview"); // S3 URL
//            fileUrls.add(url);
//        }
//
//        String mainImageUrl = fileUrls.get(0); // 첫 번째 이미지 대표 이미지
//        SpaceReview spaceReview = reviewConverter.toReview(request, space, user, mainImageUrl);
//        SpaceReview savedSpaceReview = spaceReviewRepository.save(spaceReview);
//
//        // 엔티티에 이미지 URL 저장
//        for (String url : fileUrls) {
//            reviewImageRepository.save(ImageConverter.toEntityReviewImage(savedSpaceReview, url));
//        }
//    }
//
//    @Override
//    public void deleteReview(Long reviewId, Long userId) {
//        SpaceReview spaceReview = spaceReviewRepository.findById(reviewId)
//                .orElseThrow(() -> new IllegalArgumentException("SpaceReview not found"));
//
//        if (!spaceReview.getUser().getId().equals(userId)) {
//            throw new IllegalArgumentException("User is not the author of this spaceReview");
//        }
//
//        spaceReviewRepository.delete(spaceReview);
//    }
}


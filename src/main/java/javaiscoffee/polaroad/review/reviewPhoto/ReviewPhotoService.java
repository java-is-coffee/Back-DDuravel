package javaiscoffee.polaroad.review.reviewPhoto;

import javaiscoffee.polaroad.review.JpaReviewRepository;
import javaiscoffee.polaroad.review.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class ReviewPhotoService {
    private final JpaReviewRepository reviewRepository;
    private final JpaReviewPhotoRepository reviewPhotoRepository;

    /**
     * 댓글 생성시 사용하는 사진 저장 메서드
     */
    public ReviewPhoto saveReviewPhoto(String image, Review review) {
        ReviewPhoto newReviewPhoto = new ReviewPhoto();
        newReviewPhoto.setImage(image);     // 링크 저장
        newReviewPhoto.setReview(review); // 댓글 id 저장
        reviewPhotoRepository.save(newReviewPhoto);

        return newReviewPhoto;
    }

    /**
     * 댓글 수정시 사용하는 사진 수정 메서드
     */
    public void editReviewPhoto(List<ReviewPhotoInfoDto> reviewPhotoInfoDtoList, Review review) {
        //기존 사진 url 리스트
        List<ReviewPhoto> oldReviewPhotoList = reviewRepository.findByReviewId(review.getReviewId()).getReviewPhoto();
        log.info("기존 사진 리스트 = {}", oldReviewPhotoList);

        List<String> reviewPhotoUrls = new ArrayList<>();
        for (ReviewPhotoInfoDto reviewPhotoInfoDto : reviewPhotoInfoDtoList) {
            String reviewPhotoUrl = reviewPhotoInfoDto.getReviewPhotoUrl();
            reviewPhotoUrls.add(reviewPhotoUrl);
        }

        //새로 수정된 사진 세트
        Set<String> updatedReviewPhotoSet = new HashSet<>(reviewPhotoUrls);
        oldReviewPhotoList.forEach(reviewPhotoUrl -> {
            //삭제되어야 할 사진 찾기
            if (!updatedReviewPhotoSet.contains(reviewPhotoUrl.getImage())) {   // 수정된 리스트에 기존 사진 url이 없으면
                reviewPhotoRepository.delete(reviewPhotoUrl.getReviewPhotoId()); // 기존 사진 url 삭제
            }
            //그대로 있는 사진 업데이트 리스트에서 지우기
            else {
                reviewPhotoUrls.remove(reviewPhotoUrl.getImage());
            }
        });

        for (ReviewPhotoInfoDto id : reviewPhotoInfoDtoList) {
            Long reviewPhotoId = id.getReviewPhotoId();
            log.info("수정된 댓글 사진의 reviewPhotoId = {}",reviewPhotoId);

            for (String reviewPhotoUrl : reviewPhotoUrls) {
                // reviewPhotoId가 null이면 새로 저장
                if (reviewPhotoId == null) {
                    ReviewPhoto newReviewPhoto = new ReviewPhoto();
                    Review reId = reviewRepository.findByReviewId(review.getReviewId());
                    newReviewPhoto.setImage(reviewPhotoUrl);
                    newReviewPhoto.setReview(reId);
                    reviewPhotoRepository.save(newReviewPhoto);
                }
            }
        }

    }
}

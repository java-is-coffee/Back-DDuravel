package javaiscoffee.polaroad.review.reviewPhoto;

import javaiscoffee.polaroad.post.hashtag.Hashtag;
import javaiscoffee.polaroad.post.hashtag.PostHashtag;
import javaiscoffee.polaroad.post.hashtag.PostHashtagId;
import javaiscoffee.polaroad.review.JpaReviewRepository;
import javaiscoffee.polaroad.review.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
        newReviewPhoto.setReviewId(review); // 댓글 id 저장
        reviewPhotoRepository.save(newReviewPhoto);

        return newReviewPhoto;
    }

    /**
     * 댓글 수정시 사용하는 사진 수정 메서드
     */
    public void editReviewPhoto(Long reviewPhotoId ,List<String> reviewPhotoUrlList, Review review) {
        //기존 사진 url 리스트
        List<ReviewPhoto> oldReviewPhotoList = reviewRepository.findByReviewId(review.getReviewId()).getReviewPhoto();
        log.info("기존 사진 리스트 = {}", oldReviewPhotoList);
        //새로 수정된 사진 세트
        Set<String> updatedReviewPhotoSet = new HashSet<>(reviewPhotoUrlList);

        oldReviewPhotoList.forEach(reviewPhotoUrl -> {
            //삭제되어야 할 사진 찾기
            if (!updatedReviewPhotoSet.contains(reviewPhotoUrl.getImage())) {   // 수정된 리스트에 기존 사진 url이 없으면
                reviewPhotoRepository.delete(reviewPhotoUrl.getReviewPhotoId()); // 기존 사진 url 삭제
            }
            //그대로 있는 사진 업데이트 리스트에서 지우기
            else {
                reviewPhotoUrlList.remove(reviewPhotoUrl);
            }
        });

        log.info("수정된 사진 리스트 생성, 저장 시작");
        reviewPhotoUrlList.forEach(reviewPhotoUrl ->{
            log.info("쿼리문 실행 후 사진 id = {}",reviewPhotoId);
            // 새로 추가된 사진은 사진 id가 null, 새로 생성 후 저장
            if (reviewPhotoId == null) {
                ReviewPhoto newReviewPhoto = new ReviewPhoto();
                Review reId = reviewRepository.findByReviewId(review.getReviewId());
                newReviewPhoto.setImage(reviewPhotoUrl);
                newReviewPhoto.setReviewId(reId);
                reviewPhotoRepository.save(newReviewPhoto);
            }
        });
    }
}

package javaiscoffee.polaroad.review;

import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository  {

    public Review save(Review review);

    public Review findByReviewId(Long reviewId);

    public Review update(Review updatedReview);

    public void delete(Long reviewId);

    void updateReviewGoodNumber(int changeNumber, Long reviewId);

    int getReviewGoodNumber(Long reviewId);

    ReviewInfoCachingDto getReviewCachingDto(Long reviewId, ReviewStatus status);
}

package javaiscoffee.polaroad.review;

import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;

import java.util.List;

public interface ReviewRepository {

    public Review save(Review review);

    public Review findByReviewId(Long reviewId);

    public Review update(Review updatedReview);

    public void delete(Long reviewId);

}

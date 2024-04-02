package javaiscoffee.polaroad.review.reviewGood;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewGoodRepository extends JpaRepository<ReviewGood, ReviewGoodId> {
    @Query("SELECT CASE WHEN COUNT(rg) > 0 THEN true ELSE false END FROM ReviewGood rg WHERE rg.review.reviewId = :reviewId AND rg.member.memberId = :memberId")
    boolean existsByReviewIdAndMemberId(@Param("reviewId") Long reviewId,@Param("memberId") Long memberId);
}

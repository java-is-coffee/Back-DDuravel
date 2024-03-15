package javaiscoffee.polaroad.review;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class JpaReviewRepository implements ReviewRepository{

    private final EntityManager em;

    public JpaReviewRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Review save(Review review) {
        em.persist(review);
        // 댓글 수 +1 하는 쿼리 생성
        Post post = review.getPostId(); // 댓글이 속한 포스트 가져옴
        Query query = em.createQuery("UPDATE Post p SET p.reviewNumber = p.reviewNumber + 1 WHERE p.postId = :postId");
        query.setParameter("postId", post.getPostId());
        query.executeUpdate(); // DB 댓글 수 업데이트
        return review;
    }

    @Override
    public Review findByReviewId(Long reviewId) {
        Review review = em.find(Review.class, reviewId);
        log.info("JpaReviewRepository 댓글 조회시 댓글 id = {}", review);
        return review;
    }

    @Override
    public Review update(Review updatedReview) {
        em.merge(updatedReview);
        return updatedReview;
    }

    @Override
    public void delete(Long reviewId) {
        Review review = em.find(Review.class, reviewId);
        review.setStatus(ReviewStatus.DELETED);
        // 댓글 수 -1
        Post post = review.getPostId();
        Query query = em.createQuery("UPDATE Post p SET p.reviewNumber = p.reviewNumber - 1 WHERE p.postId = :postId");
        query.setParameter("postId", post.getPostId());
        query.executeUpdate(); // DB 댓글 수 업데이트
    }

    // PostId로 review들 조회
    @Override
    public List<Review> findReviewByPostId(Post postId, ReviewStatus status) {
        return em.createQuery("SELECT r FROM Review r WHERE r.postId = :postId AND r.status = :status ORDER BY r.createdTime ASC", Review.class) //post와 status가 set된 값과 일치하는 review 엔티티 선택하고 오름차순 정렬
                .setParameter("postId", postId)
                .setParameter("status", status)
                .getResultList();
    }

    // MemberId로 review들 조회
    @Override
    public List<Review> findReviewByMemberId(Member memberId, ReviewStatus status) {
        return em.createQuery("SELECT r FROM Review r WHERE r.memberId = :memberId AND r.status = :status ORDER BY r.createdTime ASC", Review.class)
                .setParameter("member", memberId)
                .setParameter("status", status)
                .getResultList();
    }
}

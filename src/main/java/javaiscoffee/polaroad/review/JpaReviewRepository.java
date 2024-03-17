package javaiscoffee.polaroad.review;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    // 포스트의 댓글들 페이징
    public Page<Review> findReviewPagedByPostId(Post postId, Pageable pageable, ReviewStatus status) {
        // 페이징된 결과를 가져오는 쿼리
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.postId = :postId AND r.status = :status ORDER BY r.createdTime DESC", Review.class)
                .setParameter("postId", postId)
                .setParameter("status", status);

        // 페이징 쿼리 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 전체 댓글 수를 가져오는 카운트 쿼리
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.postId = :postId AND r.status = :status", Long.class)
                .setParameter("postId", postId)
                .setParameter("status", status);
        // 전체 댓글 수 조회
        Long totalReviews = countQuery.getSingleResult();
        List<Review> reviewList = query.getResultList();
        // 페이징된 결과와 전체 댓글 수를 사용하여 Page 객체 생성
        return new PageImpl<>(reviewList, pageable, totalReviews);
    }

    // MemberId로 review들 조회
    @Override
    public List<Review> findReviewByMemberId(Member memberId, ReviewStatus status) {
        return em.createQuery("SELECT r FROM Review r WHERE r.memberId = :memberId AND r.status = :status ORDER BY r.createdTime ASC", Review.class)
                .setParameter("member", memberId)
                .setParameter("status", status)
                .getResultList();
    }

    // 맴버가 작성한 모든 댓글들 페이징
    public Page<Review> findReviewPagedByMemberId(Member memberId, Pageable pageable, ReviewStatus status) {
        // 페이징된 결과를 가져오는 쿼리
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.memberId = :memberId AND r.status = :status ORDER BY r.createdTime DESC", Review.class)
                .setParameter("memberId", memberId)
                .setParameter("status", status);

        // 페이징 쿼리 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 전체 댓글 수를 가져오는 카운트 쿼리
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.memberId = :memberId AND r.status = :status", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("status", status);

        // 전체 댓글 수 조회
        Long totalReviews = countQuery.getSingleResult();

        List<Review> reviewList = query.getResultList();
        // 페이징된 결과와 전체 댓글 수를 사용하여 Page 객체 생성
        return new PageImpl<>(reviewList, pageable, totalReviews);
    }
}

package javaiscoffee.polaroad.review;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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

    /**
     * 해당 포스트의 모든 댓글들 조회
     */
    @Override
    public List<Review> findReviewByPostId(Post postId, ReviewStatus status) {
        return em.createQuery("SELECT r FROM Review r WHERE r.postId = :postId AND r.status = :status ORDER BY r.createdTime ASC", Review.class) //post와 status가 set된 값과 일치하는 review 엔티티 선택하고 오름차순 정렬
                .setParameter("postId", postId)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * 포스트의 댓글들 페이징
     */
    public Slice<Review> findReviewSlicedByPostId(Post postId, Pageable pageable, ReviewStatus status) {
        // 총 댓글 수를 조회하는 쿼리
        TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.postId = :postId AND r.status = :status", Long.class)
                .setParameter("postId", postId)
                .setParameter("status", status);
        Long totalReviews = countQuery.getSingleResult();

        // 마지막 페이지에 댓글이 있는지 여부 확인 => 총 댓글 수를 기반으로 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalReviews / pageable.getPageSize());

        // 페이징된 결과를 가져오는 쿼리
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.postId = :postId AND r.status = :status ORDER BY r.createdTime DESC", Review.class)
                .setParameter("postId", postId)
                .setParameter("status", status);

        // 페이징 쿼리 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Review> reviewList = query.getResultList();
        // 다음 페이지의 존재 여부 결정. 현재 페이지가 마지막 페이지인지 확인
        boolean hasNextPage = pageable.getPageNumber() < totalPages - 1;
        // Slice 객체 생성 후 반환
        return new SliceImpl<>(reviewList, pageable, hasNextPage);
    }

    /**
     * 맴버가 작성한 모든 댓글들 조회
     */
    @Override
    public List<Review> findReviewByMemberId(Member memberId, ReviewStatus status) {
        return em.createQuery("SELECT r FROM Review r WHERE r.memberId = :memberId AND r.status = :status ORDER BY r.createdTime ASC", Review.class)
                .setParameter("memberId", memberId)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * 맴버가 작성한 모든 댓글들 페이징
     */
    public Slice<Review> findReviewSlicedByMemberId(Member memberId, Pageable pageable, ReviewStatus status) {
        // 페이징된 결과를 가져오는 쿼리
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.memberId = :memberId AND r.status = :status ORDER BY r.createdTime DESC", Review.class)
                .setParameter("memberId", memberId)
                .setParameter("status", status);

        // 페이징 쿼리 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Review> reviewList = query.getResultList();
        boolean hasNextPage = reviewList.size() == pageable.getPageSize();
        return new SliceImpl<>(reviewList, pageable, hasNextPage);
    }
}

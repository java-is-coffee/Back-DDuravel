package javaiscoffee.polaroad.review.reviewPhoto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class JpaReviewPhotoRepository implements ReviewPhotoRepository{
    private final EntityManager em;

    public JpaReviewPhotoRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public ReviewPhoto save(ReviewPhoto reviewPhoto) {
        em.persist(reviewPhoto);
        return reviewPhoto;
    }

    @Override
    public Optional<ReviewPhoto> findReviewPhotoIdByReviewPhotoUrl(String reviewPhotoUrl) {
        log.info("사진 url로 사진 id 찾는 쿼리문 시작, reviewPhotoUrl = {}", reviewPhotoUrl);
        try {
            ReviewPhoto photoId = em.createQuery("SELECT rp FROM ReviewPhoto rp WHERE rp.image = :reviewPhotoUrl", ReviewPhoto.class)
                    .setParameter("reviewPhotoUrl", reviewPhotoUrl)
                    .getSingleResult();
            return Optional.of(photoId);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long reviewPhotoId) {
        ReviewPhoto reviewPhoto = em.find(ReviewPhoto.class, reviewPhotoId);
        em.remove(reviewPhoto);
        em.flush();
    }

}

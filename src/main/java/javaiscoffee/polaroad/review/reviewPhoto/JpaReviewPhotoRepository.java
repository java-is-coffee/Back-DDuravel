package javaiscoffee.polaroad.review.reviewPhoto;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
    public void delete(Long reviewPhotoId) {
        ReviewPhoto reviewPhoto = em.find(ReviewPhoto.class, reviewPhotoId);
        em.remove(reviewPhoto);
        em.flush();
    }

}

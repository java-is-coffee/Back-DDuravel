package javaiscoffee.polaroad.review.reviewPhoto;

import java.util.Optional;

public interface ReviewPhotoRepository {

    public ReviewPhoto save(ReviewPhoto reviewPhoto);

    public Optional<ReviewPhoto> findReviewPhotoIdByReviewPhotoUrl(String reviewPhotoUrl);

    public void delete(Long reviewPhotoId);
}

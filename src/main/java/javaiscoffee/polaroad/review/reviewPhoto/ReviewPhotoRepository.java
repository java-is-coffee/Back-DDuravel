package javaiscoffee.polaroad.review.reviewPhoto;

public interface ReviewPhotoRepository {

    public ReviewPhoto save(ReviewPhoto reviewPhoto);

    public ReviewPhoto findByReviewPhotoId(Long reviewPhotoId);

    public ReviewPhoto update(ReviewPhoto updatedReviewPhoto);

}

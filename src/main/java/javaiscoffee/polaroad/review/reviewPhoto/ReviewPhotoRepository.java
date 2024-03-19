package javaiscoffee.polaroad.review.reviewPhoto;


public interface ReviewPhotoRepository {

    public ReviewPhoto save(ReviewPhoto reviewPhoto);

    public void delete(Long reviewPhotoId);
}

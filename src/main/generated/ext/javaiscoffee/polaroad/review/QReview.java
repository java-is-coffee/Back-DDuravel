package ext.javaiscoffee.polaroad.review;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.review.Review;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = -1419713462L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final ext.javaiscoffee.polaroad.member.QMember memberId;

    public final ext.javaiscoffee.polaroad.post.QPost postId;

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final ListPath<javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto, ext.javaiscoffee.polaroad.review.reviewPhoto.QReviewPhoto> reviewPhoto = this.<javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto, ext.javaiscoffee.polaroad.review.reviewPhoto.QReviewPhoto>createList("reviewPhoto", javaiscoffee.polaroad.review.reviewPhoto.ReviewPhoto.class, ext.javaiscoffee.polaroad.review.reviewPhoto.QReviewPhoto.class, PathInits.DIRECT2);

    public final EnumPath<javaiscoffee.polaroad.review.ReviewStatus> status = createEnum("status", javaiscoffee.polaroad.review.ReviewStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedTime = createDateTime("updatedTime", java.time.LocalDateTime.class);

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberId = inits.isInitialized("memberId") ? new ext.javaiscoffee.polaroad.member.QMember(forProperty("memberId")) : null;
        this.postId = inits.isInitialized("postId") ? new ext.javaiscoffee.polaroad.post.QPost(forProperty("postId"), inits.get("postId")) : null;
    }

}


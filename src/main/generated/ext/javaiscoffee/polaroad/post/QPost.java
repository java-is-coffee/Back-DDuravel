package ext.javaiscoffee.polaroad.post;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.post.Post;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 52425930L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final ListPath<javaiscoffee.polaroad.post.card.Card, ext.javaiscoffee.polaroad.post.card.QCard> cards = this.<javaiscoffee.polaroad.post.card.Card, ext.javaiscoffee.polaroad.post.card.QCard>createList("cards", javaiscoffee.polaroad.post.card.Card.class, ext.javaiscoffee.polaroad.post.card.QCard.class, PathInits.DIRECT2);

    public final EnumPath<javaiscoffee.polaroad.post.PostConcept> concept = createEnum("concept", javaiscoffee.polaroad.post.PostConcept.class);

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> goodNumber = createNumber("goodNumber", Integer.class);

    public final ext.javaiscoffee.polaroad.member.QMember member;

    public final ListPath<javaiscoffee.polaroad.post.hashtag.PostHashtag, ext.javaiscoffee.polaroad.post.hashtag.QPostHashtag> postHashtags = this.<javaiscoffee.polaroad.post.hashtag.PostHashtag, ext.javaiscoffee.polaroad.post.hashtag.QPostHashtag>createList("postHashtags", javaiscoffee.polaroad.post.hashtag.PostHashtag.class, ext.javaiscoffee.polaroad.post.hashtag.QPostHashtag.class, PathInits.DIRECT2);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<javaiscoffee.polaroad.post.PostRegion> region = createEnum("region", javaiscoffee.polaroad.post.PostRegion.class);

    public final NumberPath<Integer> reviewNumber = createNumber("reviewNumber", Integer.class);

    public final ListPath<javaiscoffee.polaroad.review.Review, ext.javaiscoffee.polaroad.review.QReview> reviews = this.<javaiscoffee.polaroad.review.Review, ext.javaiscoffee.polaroad.review.QReview>createList("reviews", javaiscoffee.polaroad.review.Review.class, ext.javaiscoffee.polaroad.review.QReview.class, PathInits.DIRECT2);

    public final StringPath routePoint = createString("routePoint");

    public final EnumPath<javaiscoffee.polaroad.post.PostStatus> status = createEnum("status", javaiscoffee.polaroad.post.PostStatus.class);

    public final NumberPath<Integer> thumbnailIndex = createNumber("thumbnailIndex", Integer.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedTime = createDateTime("updatedTime", java.time.LocalDateTime.class);

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new ext.javaiscoffee.polaroad.member.QMember(forProperty("member")) : null;
    }

}


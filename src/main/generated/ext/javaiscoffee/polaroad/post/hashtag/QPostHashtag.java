package ext.javaiscoffee.polaroad.post.hashtag;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.post.hashtag.PostHashtag;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostHashtag is a Querydsl query type for PostHashtag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostHashtag extends EntityPathBase<PostHashtag> {

    private static final long serialVersionUID = -961129856L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostHashtag postHashtag = new QPostHashtag("postHashtag");

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final QHashtag hashtag;

    public final ext.javaiscoffee.polaroad.post.QPost post;

    public final QPostHashtagId postHashtagId;

    public QPostHashtag(String variable) {
        this(PostHashtag.class, forVariable(variable), INITS);
    }

    public QPostHashtag(Path<? extends PostHashtag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostHashtag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostHashtag(PathMetadata metadata, PathInits inits) {
        this(PostHashtag.class, metadata, inits);
    }

    public QPostHashtag(Class<? extends PostHashtag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hashtag = inits.isInitialized("hashtag") ? new QHashtag(forProperty("hashtag")) : null;
        this.post = inits.isInitialized("post") ? new ext.javaiscoffee.polaroad.post.QPost(forProperty("post"), inits.get("post")) : null;
        this.postHashtagId = inits.isInitialized("postHashtagId") ? new QPostHashtagId(forProperty("postHashtagId")) : null;
    }

}


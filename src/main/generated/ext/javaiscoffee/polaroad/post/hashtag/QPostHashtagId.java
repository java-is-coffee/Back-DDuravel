package ext.javaiscoffee.polaroad.post.hashtag;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.post.hashtag.PostHashtagId;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostHashtagId is a Querydsl query type for PostHashtagId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPostHashtagId extends BeanPath<PostHashtagId> {

    private static final long serialVersionUID = -227820613L;

    public static final QPostHashtagId postHashtagId = new QPostHashtagId("postHashtagId");

    public final NumberPath<Long> hashtagId = createNumber("hashtagId", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public QPostHashtagId(String variable) {
        super(PostHashtagId.class, forVariable(variable));
    }

    public QPostHashtagId(Path<? extends PostHashtagId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostHashtagId(PathMetadata metadata) {
        super(PostHashtagId.class, metadata);
    }

}


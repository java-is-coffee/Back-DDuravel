package ext.javaiscoffee.polaroad.post.hashtag;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.post.hashtag.Hashtag;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHashtag is a Querydsl query type for Hashtag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHashtag extends EntityPathBase<Hashtag> {

    private static final long serialVersionUID = 598286656L;

    public static final QHashtag hashtag = new QHashtag("hashtag");

    public final NumberPath<Long> hashtagId = createNumber("hashtagId", Long.class);

    public final StringPath name = createString("name");

    public QHashtag(String variable) {
        super(Hashtag.class, forVariable(variable));
    }

    public QHashtag(Path<? extends Hashtag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHashtag(PathMetadata metadata) {
        super(Hashtag.class, metadata);
    }

}


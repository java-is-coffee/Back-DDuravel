package ext.javaiscoffee.polaroad.post.card;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.post.card.Card;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCard is a Querydsl query type for Card
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCard extends EntityPathBase<Card> {

    private static final long serialVersionUID = -727472764L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCard card = new QCard("card");

    public final NumberPath<Long> cardId = createNumber("cardId", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final StringPath image = createString("image");

    public final NumberPath<Integer> index = createNumber("index", Integer.class);

    public final StringPath latitude = createString("latitude");

    public final StringPath location = createString("location");

    public final StringPath longtitude = createString("longtitude");

    public final ext.javaiscoffee.polaroad.post.QPost post;

    public final EnumPath<javaiscoffee.polaroad.post.card.CardStatus> status = createEnum("status", javaiscoffee.polaroad.post.card.CardStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedTime = createDateTime("updatedTime", java.time.LocalDateTime.class);

    public QCard(String variable) {
        this(Card.class, forVariable(variable), INITS);
    }

    public QCard(Path<? extends Card> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCard(PathMetadata metadata, PathInits inits) {
        this(Card.class, metadata, inits);
    }

    public QCard(Class<? extends Card> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new ext.javaiscoffee.polaroad.post.QPost(forProperty("post"), inits.get("post")) : null;
    }

}


package javaiscoffee.polaroad.post.card;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.post.QPost;
import ext.javaiscoffee.polaroad.post.card.QCard;
import ext.javaiscoffee.polaroad.post.hashtag.QPostHashtag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import javaiscoffee.polaroad.post.PostConcept;

import java.util.List;

public class QueryCardRepositoryImpl implements QueryCardRepository{
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    public static final QCard card = QCard.card;
    public static final QPost post = QPost.post;
    public QueryCardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }
    @Override
    public List<MapCardListDto> getMapCardListByKeyword(String searchKeyword, PostConcept concept, double swLatitude, double neLatitude, double swLongitude, double neLongitude, int pageSize) {
        // 기본 쿼리 구성
        String sql = "SELECT c.post_id, c.card_id, c.image, c.content, c.location, c.latitude, c.longitude " +
                "FROM Cards c " +
                "JOIN Posts p ON c.post_id = p.id ";

        // 검색어가 있을 경우 멤버와 조인
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            sql += "JOIN Member m ON c.member_id = m.id ";
        }

        // 위치 기반 검색 조건 추가
        sql += "WHERE c.latitude BETWEEN :swLatitude AND :neLatitude " +
                "AND c.longitude BETWEEN :swLongitude AND :neLongitude";

        // 개념이 있을 경우 조건 추가
        if (concept != null) {
            sql += " AND p.concept = :concept";
        }

        // 검색어가 있는 경우 풀 텍스트 검색 조건 추가
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            sql += " AND MATCH(p.title, c.content, m.nickname) AGAINST(:keyword IN BOOLEAN MODE)";
        }

        // 결과 정렬 및 페이지 제한
        sql += " ORDER BY p.good_number DESC, c.card_id DESC LIMIT :pageSize";

        Query query = em.createNativeQuery(sql, MapCardListDto.class)
                .setParameter("swLatitude", swLatitude)
                .setParameter("neLatitude", neLatitude)
                .setParameter("swLongitude", swLongitude)
                .setParameter("neLongitude", neLongitude)
                .setParameter("pageSize", pageSize);

        if (concept != null) {
            query.setParameter("concept", concept);
        }
        if (searchKeyword != null) {
            query.setParameter("keyword", searchKeyword);
        }

        return query.getResultList();
    }

    @Override
    public List<MapCardListDto> getMapCardListByHashtag(Long hashtagId, PostConcept concept, double swLatitude, double neLatitude, double swLongitude, double neLongitude, int pageSize) {
        QPostHashtag postHashtag = QPostHashtag.postHashtag;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(card.latitude.between(swLatitude, neLatitude));
        builder.and(card.longitude.between(swLongitude,neLongitude));
        if(concept != null) {
            builder.and(card.post.concept.eq(concept));
        }
        if(hashtagId != null) {
            builder.and(postHashtag.hashtag.hashtagId.eq(hashtagId));
        }

        return queryFactory.select(Projections.constructor(
                MapCardListDto.class,
                card.post.postId,
                card.cardId,
                card.image,
                card.content,
                card.location,
                card.latitude,
                card.longitude
        ))
                .from(card)
                .leftJoin(card.post, post)
                .leftJoin(card.post.postHashtags, postHashtag)
                .where(builder)
                .orderBy(card.post.goodNumber.desc(), card.cardId.desc())
                .limit(pageSize)
                .fetch();
    }
}

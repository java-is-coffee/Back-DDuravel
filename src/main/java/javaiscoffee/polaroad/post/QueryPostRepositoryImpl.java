package javaiscoffee.polaroad.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ListExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.member.QFollow;
import ext.javaiscoffee.polaroad.member.QMember;
import ext.javaiscoffee.polaroad.post.QPost;
import ext.javaiscoffee.polaroad.post.card.QCard;
import ext.javaiscoffee.polaroad.post.good.QPostGood;
import ext.javaiscoffee.polaroad.post.hashtag.QHashtag;
import ext.javaiscoffee.polaroad.post.hashtag.QPostHashtag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.post.card.CardListRepositoryDto;
import javaiscoffee.polaroad.post.card.CardStatus;
import javaiscoffee.polaroad.post.hashtag.PostHashtagInfoDto;

import java.util.*;
import java.util.stream.Collectors;

public class QueryPostRepositoryImpl implements QueryPostRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    public QueryPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }
    public static final QPost post = QPost.post; //QueryDSL Q Class
    public static final QCard card = QCard.card;
    public static final QMember member = QMember.member;

    @Override
    public List<Post> findPostByEmail(String email) {
        QPost post = QPost.post;
        return queryFactory.selectFrom(post)
                .where(post.member.email.eq(email))
                .orderBy(post.createdTime.desc())
                .fetch();
    }

    /**
     * 검색어로 포스트 목록 조회
     * todo : 검색어 기반 조회 쿼리 최적화 방법 찾기
     */
    @Override
    public PostListResponseDto searchPostByKeyword(int page, int pageSize, String searchKeyword, PostListSort sortBy, PostConcept concept, PostRegion region, PostStatus status) {
        BooleanBuilder builder = new BooleanBuilder();
        //지역, 컨셉, 상태 조건 추가
        addBuilderCondition(concept, region, status, builder, post);
        //검색어 조건 추가
        if(searchKeyword != null && !searchKeyword.isEmpty()) {
            BooleanBuilder searchBuilder = new BooleanBuilder(); // 새로운 BooleanBuilder로 검색 조건 생성
            searchBuilder.or(post.title.containsIgnoreCase(searchKeyword)); // 포스트 제목에 검색어가 포함되는 경우
            searchBuilder.or(card.content.containsIgnoreCase(searchKeyword)); // 카드 컨텐츠에 검색어가 포함되는 경우
            searchBuilder.or(post.member.nickname.containsIgnoreCase(searchKeyword)); // 멤버 닉네임에 검색어가 포함되는 경우
            builder.and(searchBuilder); // 검색어 조건 추가
        };

        // 1. 포스트 목록 조회 pageSize보다 1개 더 조회해서 hasNext 판별
        JPAQuery<PostListRepositoryDto> query = queryFactory
                .select(getPostListRepositoryDtoConstructor(post, member))
                .from(post)
                .leftJoin(post.cards, card)
                .leftJoin(post.member, member)
                .where(builder)
                .groupBy(post.postId)
                .offset(getOffset(page, pageSize))
                .limit(pageSize + 1);
        //최신순 정렬
        if (sortBy.equals(PostListSort.RECENT)) {
            query.orderBy(post.postId.desc());
        }
        //인기순 정렬
        else {
            query.orderBy(post.goodNumber.desc(),post.postId.desc());
        }

        List<PostListRepositoryDto> posts = query.fetch();
        // hasNext 판별하고 true면 1개 추가 조회한 컨텐트 삭제
        boolean hasNext = hasNextPage(posts, pageSize);

        // 2. 포스트들의 카드 정보 조회
        List<Long> postIds = getPostIds(posts);
        // 카드들을 맵으로 변경
        Map<Long, List<CardListRepositoryDto>> cardsMap = getPostCardsMap(card, postIds);

        // 3. DTO에 카드 정보 추가
        setCardInfoToPostDto(posts, cardsMap);

        // 포스트를 DTO로 변환하고 카드 이미지 처리
        return getPostListResponseDto(posts, hasNext);
    }

    @Override
    public PostListResponseDto searchPostByKeywordIndexMatch(int page, int pageSize, String searchKeyword, PostListSort sortBy, PostConcept concept, PostRegion region, PostStatus status) {
        // 기본 쿼리 설정
        String sql = "SELECT p.title, p.post_id, m.nickname, p.thumbnail_index, p.good_number, p.concept, p.region, p.updated_time " +
                "FROM posts p JOIN member m ON p.member_id = m.member_id JOIN cards c ON p.post_id = c.post_id " +
                "WHERE p.status = :status ";

        // 조건 추가
        if(region != null) {
            sql += "AND p.region = :region ";
        }
        if(concept != null) {
            sql += "AND p.concept = :concept ";
        }
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            sql += "AND (MATCH(p.title) AGAINST(:keyword IN BOOLEAN MODE) OR MATCH(c.content) AGAINST(:keyword IN BOOLEAN MODE) OR MATCH(m.nickname) AGAINST(:keyword IN BOOLEAN MODE)) ";
        }

        // 정렬 설정
        if(sortBy.equals(PostListSort.RECENT)) {
            sql += "ORDER BY p.post_id DESC ";
        } else {
            sql += "ORDER BY p.good_number DESC, p.post_id DESC ";
        }

        //페이징처리
        sql += "LIMIT :pageSize OFFSET :page";

        Query query = em.createNativeQuery(sql,"PostListRepositoryDtoMapping");
        query.setParameter("status", status.toString());
        query.setParameter("pageSize", pageSize + 1);
        query.setParameter("page", getOffset(page, pageSize));

        if (concept != null) {
            query.setParameter("concept", concept);
        }
        if (region != null) {
            query.setParameter("region", region);
        }
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            query.setParameter("keyword", searchKeyword);
        }

        List<PostListRepositoryDto> posts = query.getResultList();
        // hasNext 판별하고 true면 1개 추가 조회한 컨텐트 삭제
        boolean hasNext = hasNextPage(posts, pageSize);

        // 2. 포스트들의 카드 정보 조회
        List<Long> postIds = getPostIds(posts);
        // 카드들을 맵으로 변경
        Map<Long, List<CardListRepositoryDto>> cardsMap = getPostCardsMap(card, postIds);

        // 3. DTO에 카드 정보 추가
        setCardInfoToPostDto(posts, cardsMap);

        // 포스트를 DTO로 변환하고 카드 이미지 처리
        return getPostListResponseDto(posts, hasNext);
    }

    /**
     * 해쉬 태그로 포스트 목록 조회
     */
    @Override
    public PostListResponseDto searchPostByHashtag(int page, int pageSize, Long hashtagId, PostListSort sortBy, PostConcept concept, PostRegion region, PostStatus status) {
        QPostHashtag postHashtag = QPostHashtag.postHashtag;

        BooleanBuilder builder = new BooleanBuilder();
        //지역, 컨셉, 상태 조건 추가
        addBuilderCondition(concept, region, status, builder, post);
        // 해쉬태그 검색 조건 추가
        if(hashtagId != null) {
            builder.and(postHashtag.hashtag.hashtagId.eq(hashtagId));
        }

        JPAQuery<PostListRepositoryDto> query = queryFactory
                .select(getPostListRepositoryDtoConstructor(post, member))
                .from(post)
                .leftJoin(post.cards, card)
                .leftJoin(post.member, member)
                .leftJoin(post.postHashtags, postHashtag)
                .where(builder)
                .groupBy(post.postId)
                .offset(getOffset(page, pageSize))
                .limit(pageSize + 1);

        //최신순 정렬
        if (sortBy.equals(PostListSort.RECENT)) {
            query.orderBy(post.postId.desc());
        }
        //인기순 정렬
        else {
            query.orderBy(post.goodNumber.desc(),post.postId.desc());
        }

        List<PostListRepositoryDto> posts = query.fetch();
        // hasNext 판별하고 true면 1개 추가 조회한 컨텐트 삭제
        boolean hasNext = hasNextPage(posts, pageSize);

        // 2. 포스트들의 카드 정보 조회
        List<Long> postIds = getPostIds(posts);

        Map<Long, List<CardListRepositoryDto>> cardsMap = getPostCardsMap(card, postIds);

        // 3. DTO에 카드 정보 추가
        setCardInfoToPostDto(posts, cardsMap);

        // 포스트를 DTO로 변환하고 카드 이미지 처리
        return getPostListResponseDto(posts, hasNext);
    }


    //팔로잉하고 있는 멤버 포스트 목록 조회
    @Override
    public PostListResponseDto getFollowingMembersPostByMember(Long memberId, PostConcept concept, int page, int pageSize, PostStatus status) {
        QFollow follow = QFollow.follow;

        List<Long> followingMemberIds = queryFactory
                .select(follow.followedMember.memberId)
                .from(follow)
                .where(follow.followingMember.memberId.eq(memberId))
                .fetch();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(post.member.memberId.in(followingMemberIds));
        booleanBuilder.and(post.status.eq(status));
        if(concept != null) {
            booleanBuilder.and(post.concept.eq(concept));
        }

        // 팔로잉하는 멤버의 포스트를 조회
        List<PostListRepositoryDto> posts = queryFactory
                .select(getPostListRepositoryDtoConstructor(post, post.member))
                .from(post)
                .where(booleanBuilder)
                .offset(getOffset(page, pageSize))
                .limit(pageSize + 1)
                .orderBy(post.postId.desc())
                .fetch();
        // hasNext 판별하고 true면 1개 추가 조회한 컨텐트 삭제
        boolean hasNext = hasNextPage(posts, pageSize);

        // 2. 포스트들의 카드 정보 조회
        List<Long> postIds = getPostIds(posts);

        Map<Long, List<CardListRepositoryDto>> cardsMap = getPostCardsMap(card, postIds);

        // 3. DTO에 카드 정보 추가
        setCardInfoToPostDto(posts, cardsMap);

        // 포스트를 DTO로 변환하고 카드 이미지 처리
        return getPostListResponseDto(posts, hasNext);
    }

    @Override
    public PostInfoDto getPostInfoById(Long postId, Long memberId) {
        QPostGood postGood = QPostGood.postGood;
        QPostHashtag postHashtag = QPostHashtag.postHashtag;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanExpression isMemberGood = JPAExpressions
                .selectOne()
                .from(postGood)
                .where(postGood.post.postId.eq(postId)
                        .and(postGood.member.memberId.eq(memberId)))
                .exists();

        PostInfoDto postInfoDto = queryFactory
                .select(Projections.constructor(PostInfoDto.class,
                        post.title,
                        isMemberGood,
                        Projections.constructor(PostMemberInfoDto.class, post.member.memberId, post.member.name, post.member.nickname, post.member.profileImage),
                        post.routePoint,
                        post.goodNumber,
                        post.thumbnailIndex,
                        post.concept,
                        post.region,
                        post.updatedTime))
                .from(post)
                .leftJoin(post.member, member)
                .where(post.postId.eq(postId))
                .fetchOne();

        List<CardInfoDto> postCards = queryFactory.select(Projections.constructor(
                        CardInfoDto.class,
                        card.cardId,
                        card.cardIndex,
                        card.latitude,
                        card.longitude,
                        card.location,
                        card.image,
                        card.content))
                .from(card)
                .where(card.post.postId.eq(postId).and(card.status.eq(CardStatus.ACTIVE)))
                .fetch();

        List<PostHashtagInfoDto> postHashtags = queryFactory
                .select(Projections.constructor(
                        PostHashtagInfoDto.class,
                        postHashtag.postHashtagId.hashtagId,
                        postHashtag.hashtag.name))
                .from(postHashtag)
                .leftJoin(postHashtag.hashtag, hashtag)
                .where(postHashtag.post.postId.eq(postId))
                .fetch();

        postInfoDto.setCards(postCards);
        postInfoDto.setPostHashtags(postHashtags);
        return postInfoDto;
    }

    @Override
    public PostInfoCachingDto getPostCachingDtoById(Long postId) {
        QPostHashtag postHashtag = QPostHashtag.postHashtag;
        QHashtag hashtag = QHashtag.hashtag;

        PostInfoCachingDto postInfoDto = queryFactory
                .select(Projections.constructor(PostInfoCachingDto.class,
                        post.title,
                        post.member.memberId,
                        post.routePoint,
                        post.goodNumber,
                        post.thumbnailIndex,
                        post.concept,
                        post.region,
                        post.updatedTime))
                .from(post)
                .leftJoin(post.member, member)
                .where(post.postId.eq(postId))
                .fetchOne();
        List<CardInfoDto> postCards = queryFactory.select(Projections.constructor(
                        CardInfoDto.class,
                        card.cardId,
                        card.cardIndex,
                        card.latitude,
                        card.longitude,
                        card.location,
                        card.image,
                        card.content))
                .from(card)
                .where(card.post.postId.eq(postId).and(card.status.eq(CardStatus.ACTIVE)))
                .fetch();

        List<PostHashtagInfoDto> postHashtags = queryFactory
                .select(Projections.constructor(
                        PostHashtagInfoDto.class,
                        postHashtag.postHashtagId.hashtagId,
                        postHashtag.hashtag.name))
                .from(postHashtag)
                .leftJoin(postHashtag.hashtag, hashtag)
                .where(postHashtag.post.postId.eq(postId))
                .fetch();

        postInfoDto.setCards(postCards);
        postInfoDto.setPostHashtags(postHashtags);
        return postInfoDto;
    }

    private static void addBuilderCondition(PostConcept concept, PostRegion region, PostStatus status, BooleanBuilder builder, QPost post) {
        //여행컨셉 조건 추가
        //인기 게시글 검색 아닐 때 조건 추가
        if(concept != null && !concept.equals(PostConcept.HOT)) {
            builder.and(post.concept.eq(concept));
        }
        //인기 게시글 검색 조건 추가
        else if(concept != null) {
            builder.and(post.goodNumber.goe(10));
        }
        //여행지역 조건 추가
        if(region != null) {
            builder.and(post.region.eq(region));
        }
        //게시글 상태 조건 추가
        builder.and(post.status.eq(status));
    }

    private static ConstructorExpression<PostListRepositoryDto> getPostListRepositoryDtoConstructor(QPost post, QMember member) {
        return Projections.constructor(
                PostListRepositoryDto.class,
                post.title,
                post.postId,
                member.nickname,
                post.thumbnailIndex,
                post.goodNumber,
                post.concept,
                post.region,
                post.updatedTime
        );
    }
    private static void setCardInfoToPostDto(List<PostListRepositoryDto> posts, Map<Long, List<CardListRepositoryDto>> cardsMap) {
        posts.forEach(p -> {
            List<CardListRepositoryDto> cardsForPost = cardsMap.getOrDefault(p.getPostId(), Collections.emptyList());
            p.setCards(cardsForPost);
        });
    }
    //조회에 해당하는 포스트 ID 리스트 반환
    private static List<Long> getPostIds(List<PostListRepositoryDto> posts) {
        return posts.stream()
                .map(PostListRepositoryDto::getPostId)
                .collect(Collectors.toList());
    }
    // 포스트의 카드 리스트를 카드 맵으로 바꾸기
    private Map<Long, List<CardListRepositoryDto>> getPostCardsMap(QCard card, List<Long> postIds) {
        return queryFactory
                .select(Projections.constructor(CardListRepositoryDto.class,
                        card.post.postId,
                        card.cardIndex,
                        card.image))
                .from(card)
                .where(card.post.postId.in(postIds),
                        card.status.eq(CardStatus.ACTIVE))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(CardListRepositoryDto::getPostId));
    }

    //포스트 리스트를 DTO로 변환하고 카드 이미지에서 썸네일을 제일 앞으로 설정
    private PostListResponseDto getPostListResponseDto(List<PostListRepositoryDto> posts, boolean hasNext) {
        return new PostListResponseDto(posts.stream().map(p -> {
            List<String> images = p.getCards().stream()
                    .sorted(Comparator.comparingInt(CardListRepositoryDto::getCardIndex))
                    .map(CardListRepositoryDto::getImage)
                    .distinct()
                    .limit(3)
                    .collect(Collectors.toList());

            // 썸네일 이미지가 없으면 맨 앞에 추가
            String thumbnailImage = p.getCards().get(p.getThumbnailIndex()).getImage();
            if (!images.contains(thumbnailImage)) {
                images.add(0, thumbnailImage); // 맨 앞에 썸네일 이미지 추가
                if (images.size() > 3) {
                    images = images.subList(0, 3); // 최대 3개 이미지 유지
                }
            }
            //썸네일 이미지가 있으면 맨 앞으로 옮기기
            else {
                images.remove(thumbnailImage);
                images.add(0, thumbnailImage);
            }

            return new PostListDto(
                    p.getTitle(),
                    p.getPostId(),
                    p.getNickname(),
                    p.getGoodNumber(),
                    p.getConcept(),
                    p.getRegion(),
                    images,
                    p.getUpdatedTime()
            );
        }).collect(Collectors.toList()), hasNext);
    }
    // 1개 추가 조회한 포스트가 존재하면 true 처리 및 추가 조회한 포스트 삭제
    private boolean hasNextPage(List<PostListRepositoryDto> list, int pageSize) {
        if (list.size() > pageSize) {
            list.remove(pageSize);
            return true;
        }
        return false;
    }

    private int getOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }
}

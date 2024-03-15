package javaiscoffee.polaroad.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.member.QMember;
import ext.javaiscoffee.polaroad.post.QPost;
import ext.javaiscoffee.polaroad.post.card.QCard;
import ext.javaiscoffee.polaroad.post.hashtag.QHashtag;
import ext.javaiscoffee.polaroad.post.hashtag.QPostHashtag;
import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.response.ResponseMessages;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QueryPostRepositoryImpl implements QueryPostRepository{

    private final JPAQueryFactory queryFactory;
    public QueryPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findPostByEmail(String email) {
        QPost post = QPost.post; // Querydsl QClass
        return queryFactory.selectFrom(post)
                .where(post.member.email.eq(email))
                .orderBy(post.createdTime.desc())
                .fetch();
    }

    /**
     * 검색어로 포스트 목록 조회
     */
    @Override
    public List<PostListDto> searchPostByKeyword(int paging, int pagingNumber, String searchKeyword, PostListSort sortBy, PostConcept concept, PostRegion region) {
        QPost post = QPost.post;
        QCard card = QCard.card;
        QMember member = QMember.member; // 멤버와 관련된 쿼리를 위한 QClass

        BooleanBuilder builder = new BooleanBuilder();
        //검색어 조건 추가
        if(searchKeyword != null) {
            BooleanBuilder searchBuilder = new BooleanBuilder(); // 새로운 BooleanBuilder로 검색 조건 생성
            searchBuilder.or(post.title.containsIgnoreCase(searchKeyword)); // 포스트 제목에 검색어가 포함되는 경우
            searchBuilder.or(card.content.containsIgnoreCase(searchKeyword)); // 카드 컨텐츠에 검색어가 포함되는 경우
            searchBuilder.or(post.member.nickname.containsIgnoreCase(searchKeyword)); // 멤버 닉네임에 검색어가 포함되는 경우
            builder.and(searchBuilder); // 검색어 조건 추가
        }
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
        if(concept != null) {
            builder.and(post.region.eq(region));
        }

        List<Post> posts;
        //최신순 정렬
        if (sortBy.equals(PostListSort.RECENT)) {
            posts = queryFactory.selectFrom(post)
                    .leftJoin(post.cards, card)
                    .leftJoin(post.member, member)
                    .where(builder)
                    .groupBy(post.postId)
                    .orderBy(post.createdTime.desc())
                    .offset(paging * pagingNumber)
                    .limit(pagingNumber)
                    .fetch();
        }
        //인기순 정렬
        else {
            posts = queryFactory.selectFrom(post)
                    .leftJoin(post.cards, card)
                    .leftJoin(post.member, member)
                    .where(builder)
                    .groupBy(post.postId)
                    .orderBy(post.goodNumber.desc())
                    .offset(paging * pagingNumber)
                    .limit(pagingNumber)
                    .fetch();
        }

        // 포스트를 DTO로 변환하고 카드 이미지 처리
        return posts.stream().map(p -> {
            List<String> images = p.getCards().stream()
                    .sorted(Comparator.comparingInt(Card::getCardIndex))
                    .map(Card::getImage)
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
                    p.getMember().getNickname(),
                    p.getGoodNumber(),
                    p.getConcept(),
                    p.getRegion(),
                    images
            );
        }).collect(Collectors.toList());
    }

    /**
     * 해쉬 태그로 포스트 목록 조회
     */
    @Override
    public List<PostListDto> searchPostByHashtag(int paging, int pagingNumber, Long hashtagId, PostListSort sortBy, PostConcept concept, PostRegion region) {
        QPost post = QPost.post;
        QCard card = QCard.card;
        QMember member = QMember.member; // 멤버와 관련된 쿼리를 위한 QClass
        QPostHashtag postHashtag = QPostHashtag.postHashtag;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();
        // 해쉬태그 검색 조건 추가
        if(hashtagId != null) {
            builder.and(postHashtag.hashtag.hashtagId.eq(hashtagId));
        }
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
        if(concept != null) {
            builder.and(post.region.eq(region));
        }

        List<Post> posts;
        //최신순 정렬
        if (sortBy.equals(PostListSort.RECENT)) {
            posts = queryFactory.selectFrom(post)
                    .leftJoin(post.cards, card)
                    .leftJoin(post.member, member)
                    .leftJoin(post.postHashtags, postHashtag)
                    .leftJoin(postHashtag.hashtag, hashtag)
                    .where(builder)
                    .groupBy(post.postId)
                    .orderBy(post.createdTime.desc())
                    .offset(paging * pagingNumber)
                    .limit(pagingNumber)
                    .fetch();
        }
        //인기순 정렬
        else {
            posts = queryFactory.selectFrom(post)
                    .leftJoin(post.cards, card)
                    .leftJoin(post.member, member)
                    .leftJoin(post.postHashtags, postHashtag)
                    .leftJoin(postHashtag.hashtag, hashtag)
                    .where(builder)
                    .groupBy(post.postId)
                    .orderBy(post.goodNumber.desc())
                    .offset(paging * pagingNumber)
                    .limit(pagingNumber)
                    .fetch();
        }

        // 포스트를 DTO로 변환하고 카드 이미지 처리
        return posts.stream().map(p -> {
            List<String> images = p.getCards().stream()
                    .sorted(Comparator.comparingInt(Card::getCardIndex))
                    .map(Card::getImage)
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
                    p.getMember().getNickname(),
                    p.getGoodNumber(),
                    p.getConcept(),
                    p.getRegion(),
                    images
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Post getPostInfoById(Long postId) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QCard card = QCard.card;
        QPostHashtag postHashtag = QPostHashtag.postHashtag;
        QHashtag hashtag = QHashtag.hashtag;

        Post findPost = queryFactory
                .selectFrom(post)
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.cards, card).fetchJoin()
                .leftJoin(post.postHashtags, postHashtag).fetchJoin()
                .leftJoin(postHashtag.hashtag, hashtag).fetchJoin()
                .where(post.postId.eq(postId))
                .fetchOne();
        if(findPost==null) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        return findPost;
    }
}

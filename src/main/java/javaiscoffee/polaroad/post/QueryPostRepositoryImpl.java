package javaiscoffee.polaroad.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.post.QPost;
import ext.javaiscoffee.polaroad.post.card.QCard;
import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.post.card.Card;

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

    @Override
    public List<PostListDto> searchPost(int paging, int pagingNumber, String searchKeyword, PostListSort sortBy, PostConcept concept, PostRegion region) {
        QPost post = QPost.post;
        QCard card = QCard.card;
        BooleanBuilder builder = new BooleanBuilder();
        //검색어 조건 추가
        if(searchKeyword != null) {
            builder.and(post.title.containsIgnoreCase(searchKeyword));
        }
        //여행컨셉 조건 추가
        //인기 게시글 검색 아닐 때 조건 추가
        if(concept != null && !concept.equals(PostConcept.HOT)) {
            builder.and(post.concept.eq(concept));
        }
        //인기 게시글 검색 조건 추가
        else if(concept != null) {
            builder.and(post.goodNumber.goe(30));
        }
        //여행지역 조건 추가
        if(concept != null) {
            builder.and(post.region.eq(region));
        }

        List<Post> posts;
        //최신순 정렬
        if(sortBy.equals(PostListSort.RECENT)) {
            posts = queryFactory.selectFrom(post)
                    .leftJoin(post.cards, card)
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
                    .sorted(Comparator.comparingInt(Card::getIndex))
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
}

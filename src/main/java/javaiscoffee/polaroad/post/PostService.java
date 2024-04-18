package javaiscoffee.polaroad.post;

import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberSimpleInfoDto;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.card.*;
import javaiscoffee.polaroad.post.good.PostGood;
import javaiscoffee.polaroad.post.good.PostGoodBatchUpdator;
import javaiscoffee.polaroad.post.good.PostGoodId;
import javaiscoffee.polaroad.post.good.PostGoodRepository;
import javaiscoffee.polaroad.post.hashtag.PostHashtagInfoDto;
import javaiscoffee.polaroad.post.hashtag.HashtagService;
import javaiscoffee.polaroad.post.hashtag.PostHashtag;
import javaiscoffee.polaroad.redis.RedisService;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * todo 동시에 많은 요청이 들어왔을 때 어떻게 처리할 것인지 대처법 생각해보기
 */

@Slf4j
@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HashtagService hashtagService;
    private final CardService cardService;
    private final PostGoodRepository postGoodRepository;
    private final RedisService redisService;
    private final PostGoodBatchUpdator postGoodBatchUpdator;
    private final EntityManager entityManager;

    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository, HashtagService hashtagService, CardService cardService, PostGoodRepository postGoodRepository, RedisService redisService, PostGoodBatchUpdator postGoodBatchUpdator, EntityManager entityManager) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.hashtagService = hashtagService;
        this.cardService = cardService;
        this.postGoodRepository = postGoodRepository;
        this.redisService = redisService;
        this.postGoodBatchUpdator = postGoodBatchUpdator;
        this.entityManager = entityManager;
    }

    /**
     * 포스트 저장
     */
    @Transactional
    public ResponseEntity<Post> savePost(PostSaveDto postSaveDto, Long memberId) {
        //썸네일 번호가 잘못되었을 경우 에러
        if(postSaveDto.getThumbnailIndex() < 0 || postSaveDto.getThumbnailIndex() >= postSaveDto.getCards().size()) throw new BadRequestException("썸네일 인덱스가 잘못되었습니다.");
        //게시글 해쉬코드가 10개 넘어가면 에러
        if(postSaveDto.getHashtags().size() > 10) throw new BadRequestException("해쉬태그 개수는 최대 10개입니다.");
        if(postSaveDto.getCards().size() > 10) throw new BadRequestException("카드 개수는 최대 10개입니다.");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        Post post = new Post();
        BeanUtils.copyProperties(postSaveDto, post);
        post.setMember(member);
        Post savedPost = postRepository.save(post);
        post.setUpdatedTime(savedPost.getUpdatedTime());

        //해쉬태그 저장
        post.setPostHashtags(hashtagService.savePostHashtags(postSaveDto.getHashtags(), savedPost));
        
        //카드 저장
        int cardIndex = 0;
        List<Card> cardsToSave = new ArrayList<>();
        for(CardSaveDto cardInfo : postSaveDto.getCards()) {
            Card newCard = new Card();
            newCard.setCardIndex(cardIndex++);
            newCard.setLocation(cardInfo.getLocation());
            newCard.setLatitude(cardInfo.getLatitude());
            newCard.setLongitude(cardInfo.getLongitude());
            newCard.setImage(cardInfo.getImage());
            newCard.setContent(cardInfo.getContent());
            newCard.setPost(savedPost);
            newCard.setMember(member);
            cardsToSave.add(newCard);
        }
        post.setCards(cardService.saveAllCards(cardsToSave));
        //멤버 포스트 개수 1개 증가
        member.setPostNumber(member.getPostNumber() + 1);

        log.info("저장된 post = {}",post);

        redisService.saveCachingPostInfo(toPostInfoCachingDto(post), savedPost.getPostId());
        return ResponseEntity.ok(post);
    }
    /**
     * 포스트 저장 테스트
     */
    @Transactional
    public void savePostTest(PostSaveDto postSaveDto, Long memberId) {
        //썸네일 번호가 잘못되었을 경우 에러
        if(postSaveDto.getThumbnailIndex() < 0 || postSaveDto.getThumbnailIndex() >= postSaveDto.getCards().size()) throw new BadRequestException("썸네일 인덱스가 잘못되었습니다.");
        //게시글 해쉬코드가 10개 넘어가면 에러
        if(postSaveDto.getHashtags().size() > 10) throw new BadRequestException("해쉬태그 개수는 최대 10개입니다.");
        if(postSaveDto.getCards().size() > 10) throw new BadRequestException("카드 개수는 최대 10개입니다.");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        Post post = new Post();
        BeanUtils.copyProperties(postSaveDto, post);
        post.setMember(member);
        Post savedPost = postRepository.save(post);

        //해쉬태그 저장
        hashtagService.savePostHashtags(postSaveDto.getHashtags(), savedPost);

        //카드 저장
        int cardIndex = 0;
        List<Card> cardsToSave = new ArrayList<>();
        for(CardSaveDto cardInfo : postSaveDto.getCards()) {
            Card newCard = new Card();
            newCard.setCardIndex(cardIndex++);
            newCard.setLocation(cardInfo.getLocation());
            newCard.setLatitude(cardInfo.getLatitude());
            newCard.setLongitude(cardInfo.getLongitude());
            newCard.setImage(cardInfo.getImage());
            newCard.setContent(cardInfo.getContent());
            newCard.setPost(savedPost);
            newCard.setMember(member);
            cardsToSave.add(newCard);
        }
        cardService.saveAllCards(cardsToSave);
        //멤버 포스트 개수 1개 증가
        member.setPostNumber(member.getPostNumber() + 1);

//        redisService.saveCachingPostInfo(toPostInfoCachingDto(savedPost), savedPost.getPostId());
    }
    /**
     * 포스트 수정
     */
    @Transactional
    public ResponseEntity<Post> editPost(PostSaveDto postSaveDto,Long memberId, Long postId) {
        //썸네일 번호가 잘못되었을 경우 에러
        if(postSaveDto.getThumbnailIndex() < 0 || postSaveDto.getThumbnailIndex() >= postSaveDto.getCards().size()) throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        //카드, 해쉬태그 개수가 잘못된 경우
        if(postSaveDto.getCards().size() > 10 || postSaveDto.getHashtags().size() > 10) throw new BadRequestException("카드 또는 해쉬태그 개수가 많습니다.");
        Post oldPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.POST_NOT_FOUND.getMessage()));
        //포스트가 삭제되었으면
        if(oldPost.getStatus() == PostStatus.DELETED) throw new NotFoundException(ResponseMessages.POST_NOT_FOUND.getMessage());
        //인기게시글이면 수정 불가
        if(oldPost.getGoodNumber() >= 10) throw new BadRequestException("인기 포스트는 수정이 불가능합니다.");
        //생성자가 아니면 수정 불가
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.MEMBER_NOT_FOUND.getMessage()));
        if(!Objects.equals(oldPost.getMember().getMemberId(), memberId)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.MEMBER_NOT_FOUND.getMessage());
        //포스트 정보 업데이트
        oldPost.setTitle(postSaveDto.getTitle());
        oldPost.setRoutePoint(postSaveDto.getRoutePoint());
        oldPost.setThumbnailIndex(postSaveDto.getThumbnailIndex());
        oldPost.setConcept(postSaveDto.getConcept());
        oldPost.setRegion(postSaveDto.getRegion());
        oldPost.setUpdatedTime(LocalDateTime.now());

        //해쉬태그 업데이트
        oldPost.setPostHashtags(hashtagService.editPostHashtags(postSaveDto.getHashtags(),oldPost));

        //카드 업데이트
        List<CardSaveDto> updateCards = postSaveDto.getCards();
        int index = 0;
        for(CardSaveDto updateCard : updateCards) {
            updateCard.setCardIndex(index++);
        }
        oldPost.setCards(cardService.editCards(postSaveDto.getCards(), oldPost, member));

        log.info("수정된 post = {}",oldPost);
        redisService.updateCachingPost(toPostInfoCachingDto(oldPost), postId);
        return ResponseEntity.ok(oldPost);
    }
    /**
     * 포스트 삭제
     */
    @Transactional
    public ResponseEntity<String> deletePost(Long postId, Long memberId) {
        PostSimpleInfoDto postInfo = postRepository.getPostSimpleInfo(postId).orElseThrow(() -> new NotFoundException("포스트를 찾을 수 없습니다."));
        //포스트가 삭제되었으면 에러
        if(postInfo.getStatus() == PostStatus.DELETED) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 멤버가 삭제되었으면 에러
        MemberSimpleInfoDto memberInfo = memberRepository.getMemberSimpleInfo(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!memberInfo.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //생성자가 아니면 수정 불가
        if(!Objects.equals(postInfo.getMemberId(), memberId)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        postRepository.updatePostStatus(postId,PostStatus.DELETED);
        //멤버 포스트 개수 1개 감소
        memberRepository.addMemberPostNumber(memberId, -1);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    /**
     * 탐색페이지나 검색페이지에서 게시글을 목록으로 조회
     */
    public ResponseEntity<PostListResponseDto> getPostList (int page, int pageSize,PostSearchType searchType, String searchKeyword, PostListSort sortBy, PostConcept concept, PostRegion region, PostStatus status) {
        //해쉬태그 검색일 경우
        //검색어가 없으면 키워드 검색으로 넘김
        if(searchType.equals(PostSearchType.HASHTAG) && searchKeyword != null) {
            Long hashtagId = hashtagService.getHashtagIdByName(searchKeyword);
            if(hashtagId == null) return ResponseEntity.ok(new PostListResponseDto(new ArrayList<>(),false));
            return ResponseEntity.ok(postRepository.searchPostByHashtag(page, pageSize, hashtagId, sortBy, concept, region, status));
        }
        //키워드 검색일 경우
        PostListResponseDto posts = postRepository.searchPostByKeyword(page, pageSize, searchKeyword, sortBy, concept, region, status);
        return ResponseEntity.ok(posts);
    }

    /**
     * 탐색페이지나 검색페이지에서 게시글을 목록으로 조회할 때 인덱스 매칭 사용
     */
    public ResponseEntity<PostListResponseDto> getPostListByIndexMatch (int page, int pageSize,PostSearchType searchType, String searchKeyword, PostListSort sortBy, PostConcept concept, PostRegion region, PostStatus status) {
        //해쉬태그 검색일 경우
        //검색어가 없으면 키워드 검색으로 넘김
        if(searchType.equals(PostSearchType.HASHTAG) && searchKeyword != null) {
            Long hashtagId = hashtagService.getHashtagIdByName(searchKeyword);
            if(hashtagId == null) return ResponseEntity.ok(new PostListResponseDto(new ArrayList<>(),false));
            return ResponseEntity.ok(postRepository.searchPostByHashtag(page, pageSize, hashtagId, sortBy, concept, region, status));
        }
        //키워드 검색일 경우
        PostListResponseDto posts = postRepository.searchPostByKeywordIndexMatch(page, pageSize, searchKeyword, sortBy, concept, region, status);
        return ResponseEntity.ok(posts);
    }

    /**
     * 팔로잉하고 있는 멤버의 게시글을 목록으로 조회
     */
    public ResponseEntity<PostListResponseDto> getFollowingMemberPosts (Long memberId, PostConcept concept,int page, int pageSize, PostStatus status) {
        MemberSimpleInfoDto memberInfo = memberRepository.getMemberSimpleInfo(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!memberInfo.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        return ResponseEntity.ok(postRepository.getFollowingMembersPostByMember(memberInfo.getMemberId(), concept, page, pageSize, status));
    }

    /**
     * 포스트 내용 조회
     */
    public ResponseEntity<PostInfoDto> getPostInfoById(Long postId, Long memberId) {
        PostSimpleInfoDto simplePostInfo = postRepository.getPostSimpleInfo(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.POST_NOT_FOUND.getMessage()));
        MemberSimpleInfoDto simpleMemberInfo = memberRepository.getMemberSimpleInfo(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //포스트나 멤버가 조회 가능한 상태가 아니면 에러
        if(!simplePostInfo.getStatus().equals(PostStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.POST_NOT_FOUND.getMessage());
        if(!simpleMemberInfo.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.MEMBER_NOT_FOUND.getMessage());

        //캐싱된 정보가 있으면 캐싱 정보 전달
        PostInfoCachingDto cachingPostInfo = redisService.getCachingPostInfo(postId);
        if(cachingPostInfo != null) {
            log.info("캐싱된 포스트 정보 전달 = {}",postId);
            PostGoodId postGoodId = new PostGoodId(memberId, postId);
            boolean isMemberGood = postGoodRepository.existsById(postGoodId);
            PostMemberInfoDto postMemberInfo = memberRepository.getPostMemberInfoByMemberId(memberId);

            redisService.addPostView(postId, memberId);
            return ResponseEntity.ok(new PostInfoDto(
                    cachingPostInfo.getTitle(),
                    isMemberGood,
                    postMemberInfo,
                    cachingPostInfo.getRoutePoint(),
                    cachingPostInfo.getGoodNumber(),
                    cachingPostInfo.getThumbnailIndex(),
                    cachingPostInfo.getConcept(),
                    cachingPostInfo.getRegion(),
                    cachingPostInfo.getUpdatedTime(),
                    cachingPostInfo.getCards(),
                    cachingPostInfo.getPostHashtags()));


        }

        PostInfoDto postInfoDto = postRepository.getPostInfoById(postId, memberId);
        redisService.addPostView(postId, memberId);
        return ResponseEntity.ok(postInfoDto);
    }

    /**
     *  본인 포스트 리스트 조회
     */
    public PostListResponseDto getMyPostList (Long memberId,int page, int pageSize, PostStatus status) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Slice<Post> slicePosts = postRepository.findPostsByMemberMemberIdAndStatusOrderByCreatedTimeDesc(memberId, status, pageable);

        return toPostListResponseDto(slicePosts.getContent(), slicePosts.hasNext());
    }

    /**
     * 조회수 랭킹으로 조회
     */
    public ResponseEntity<PostListResponseDto> getPostRankingList(int page, int pageSize, PostRankingRange range) {
        //랭킹 순위 리스트 조회
        List<String> list = redisService.getViewRankingList(page, pageSize, range);
        List<Long> rankingList = list.stream().map(Long::valueOf).toList();
        //랭킹 최대 페이지 구하기
        int maxPage = redisService.getViewRankingMaxPageSize(pageSize, range);
        //포스트 목록 구해서 정렬하기
        List<PostListRepositoryDto> postListDtos = getRankingPostListRepositoryDtoList(rankingList);
        //반환값으로 매핑
        return ResponseEntity.ok(getPostListResponseDto(postListDtos, page < maxPage));
    }


    /**
     * 포스트 추천 토글
     */
    @Transactional
    public void postGoodToggle(Long memberId, Long postId) {
        PostGoodId postGoodId = new PostGoodId(memberId, postId);

        MemberSimpleInfoDto memberInfo = memberRepository.getMemberSimpleInfo(memberId).orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));
        PostSimpleInfoDto postInfo = postRepository.getPostSimpleInfo(postId).orElseThrow(() -> new NotFoundException("포스트를 찾을 수 없습니다."));
        // Member와 Post의 참조(프록시)만 로드
        Member memberRef = entityManager.getReference(Member.class, memberId);
        Post postRef = entityManager.getReference(Post.class, postId);

        if(!memberInfo.getStatus().equals(MemberStatus.ACTIVE) || !postInfo.getStatus().equals(PostStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        if(postInfo.getMemberId().equals(memberId)) throw new BadRequestException(ResponseMessages.GOOD_FAILED.getMessage());

        boolean postGoodExists = postGoodRepository.existsById(postGoodId);
        //추천 삭제
        if(postGoodExists) {
            postGoodRepository.deleteById(postGoodId);
            postGoodBatchUpdator.decreasePostGoodCount(postId);
        }
        //추천 생성
        else {
            postGoodRepository.save(new PostGood(postGoodId,memberRef,postRef));
            postGoodBatchUpdator.increasePostGoodCount(postId);
        }
    }

    //랭킹 리스트에 해당하는 포스트 정보 리스트로 조회 후 PostListRepositoryDto로 매핑
    private List<PostListRepositoryDto> getRankingPostListRepositoryDtoList(List<Long> rankingList) {
        List<PostListRepositoryDto> postListDtos = new ArrayList<>();
        List<Object[]> queryResults = postRepository.getPostsWithCardsByPostId(rankingList);
        //이미 앞에서 나온 포스트 정보인지 확인
        Map<Long, PostListRepositoryDto> postDtoMap = new HashMap<>();

        for (Object[] result : queryResults) {
            Long postId = (Long) result[1];
            PostListRepositoryDto postDto = postDtoMap.get(postId);
            //새로운 포스트
            if (postDto == null) {
                postDto = new PostListRepositoryDto(
                        (String) result[0], // title
                        postId, // postId
                        (String) result[2], // nickname
                        (Integer) result[3], // thumbnailIndex
                        (Integer) result[4], // goodNumber
                        PostConcept.valueOf((String) result[5]), // concept
                        PostRegion.valueOf((String) result[6]), // region
                        new ArrayList<>(), // cards
                        ((Timestamp) result[7]).toLocalDateTime() // updatedTime
                );
                postDtoMap.put(postId, postDto);
                postListDtos.add(postDto);
            }
            //카드 정보 주입
            if (result[8] != null) { // cardIndex
                CardListRepositoryDto cardDto = new CardListRepositoryDto(
                        postId,
                        (Integer) result[8], // cardIndex
                        (String) result[9] // image
                );
                postDto.getCards().add(cardDto);
            }
        }
        return postListDtos;
    }

    /**
     * post 객체를 캐싱 객체로 매핑
     */
    private PostInfoCachingDto toPostInfoCachingDto(Post post) {
        return new PostInfoCachingDto(
                post.getTitle(),
                post.getMember().getMemberId(),
                post.getRoutePoint(),
                post.getGoodNumber(),
                post.getThumbnailIndex(),
                post.getConcept(),
                post.getRegion(),
                post.getUpdatedTime(),
                post.getCards().stream()
                        .map(card -> new CardInfoDto(
                                card.getCardId(),
                                card.getCardIndex(),
                                card.getLatitude(),
                                card.getLongitude(),
                                card.getLocation(),
                                card.getImage(),
                                card.getContent()))
                        .toList(),
                post.getPostHashtags().stream()
                        .map(postHashtag -> new PostHashtagInfoDto(
                                postHashtag.getHashtag().getHashtagId(),
                                postHashtag.getHashtag().getName()))
                        .toList()
        );
    }

    /**
     * 포스트랑 멤버를 포스트 내용 조회 ResponseDto로 변환
     */
    private PostInfoDto toPostInfoDto(Post post, Long memberId) {
        List<CardInfoDto> cardDtos = post.getCards().stream()
                .filter(card -> card.getStatus().equals(CardStatus.ACTIVE))
                .sorted(Comparator.comparingInt(Card::getCardIndex))
                .map(this::toCardInfoDto)
                .collect(toList());

        List<PostHashtagInfoDto> hashtagDtos = post.getPostHashtags().stream()
                .map(ph -> new PostHashtagInfoDto(ph.getHashtag().getHashtagId(), ph.getHashtag().getName()))
                .collect(toList());

        PostMemberInfoDto memberDto = toPostMemberInfoDto(post.getMember());

        //멤버가 추천했는지 확인
        Optional<PostGood> isMemberGood = Optional.empty();
        if(memberId != null) {
            isMemberGood = postGoodRepository.findById(new PostGoodId(memberId, post.getPostId()));
        }

        return new PostInfoDto(
                post.getTitle(),
                isMemberGood.isPresent(),
                memberDto,
                post.getRoutePoint(),
                post.getGoodNumber(),
                post.getThumbnailIndex(),
                post.getConcept(),
                post.getRegion(),
                post.getUpdatedTime(),
                cardDtos,
                hashtagDtos
        );
    }

    /**
     * 카드 리스트를 카드 내용 조회 ResponseDto로 변환
     */
    private List<CardInfoDto> toCardInfoDtoList(List<Card> cards, Long postId) {
        //결과값 리스트 생성
        List<CardInfoDto> cardInfoDtos = new ArrayList<>();
        //카드 인덱스 값으로 오름차순 정렬
        List<Card> sortedCards = cards.stream()
                .sorted(Comparator.comparingInt(Card::getCardIndex))
                .toList();
        //응답Dto로 변환
        for (Card card : sortedCards) {
            CardInfoDto cardInfoDto = new CardInfoDto();
            BeanUtils.copyProperties(card, cardInfoDto);
            cardInfoDtos.add(cardInfoDto);
        }
        return cardInfoDtos;
    }
    private CardInfoDto toCardInfoDto(Card card) {
        return new CardInfoDto(card.getCardId(), card.getCardIndex(), card.getLatitude(), card.getLongitude(), card.getLocation(), card.getImage(), card.getContent());
    }

    /**
     * 해쉬태그 리스트를 해쉬 태그 조회 ResponseDto로 변환
     */
    private List<PostHashtagInfoDto> toHashtagInfoDto (List<PostHashtag> postHashtags) {
        List<PostHashtagInfoDto> postHashtagInfoDtos = new ArrayList<>();
        for(PostHashtag hashtag : postHashtags) {
            PostHashtagInfoDto postHashtagInfoDto = new PostHashtagInfoDto();
            postHashtagInfoDto.setHashtagId(hashtag.getHashtag().getHashtagId());
            postHashtagInfoDto.setTagName(hashtag.getHashtag().getName());
            postHashtagInfoDtos.add(postHashtagInfoDto);
        }
        return postHashtagInfoDtos;
    }

    /**
     * 멤버 정보를 게시글 생성 멤버 정보 조회 ResponseDto로 변환
     */
    private PostMemberInfoDto toPostMemberInfoDto (Member member) {
        PostMemberInfoDto postMemberInfoDto = new PostMemberInfoDto();
        BeanUtils.copyProperties(member,postMemberInfoDto);
        return postMemberInfoDto;
    }

    //포스트 리스트를 DTO로 변환하고 카드 이미지에서 썸네일을 제일 앞으로 설정
    private static PostListResponseDto toPostListResponseDto(List<Post> posts, boolean hasNext) {
        return new PostListResponseDto(posts.stream().map(p -> {
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
                    p.getPostId(),
                    p.getMember().getNickname(),
                    p.getGoodNumber(),
                    p.getConcept(),
                    p.getRegion(),
                    images,
                    p.getUpdatedTime()
            );
        }).collect(Collectors.toList()), hasNext);
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
}

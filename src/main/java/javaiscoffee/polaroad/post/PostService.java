package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.card.*;
import javaiscoffee.polaroad.post.good.PostGood;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository,HashtagService hashtagService, CardService cardService, PostGoodRepository postGoodRepository, RedisService redisService) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.hashtagService = hashtagService;
        this.cardService = cardService;
        this.postGoodRepository = postGoodRepository;
        this.redisService = redisService;
    }

    /**
     * 포스트 저장
     */
    @Transactional
    public ResponseEntity<Post> savePost(PostSaveDto postSaveDto, Long memberId) {
        //썸네일 번호가 잘못되었을 경우 에러
        if(postSaveDto.getThumbnailIndex() < 0 || postSaveDto.getThumbnailIndex() >= postSaveDto.getCards().size()) throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        //게시글 해쉬코드가 10개 넘어가면 에러
        if(postSaveDto.getHashtags().size() > 10) throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Post post = new Post();
        BeanUtils.copyProperties(postSaveDto, post);
        post.setMember(member);
        Post savedPost = postRepository.save(post);
        //해쉬태그 저장
        postSaveDto.getHashtags().forEach(tagName -> {
            hashtagService.savePostHashtag(tagName, savedPost);
        });
        //카드 저장
        int cardIndex = 0;
        for(CardSaveDto cardInfo : postSaveDto.getCards()) {
            Card newCard = new Card();
            BeanUtils.copyProperties(cardInfo,newCard);
            newCard.setCardIndex(cardIndex++);
            newCard.setPost(savedPost);
            newCard.setMember(member);
            cardService.saveCard(newCard);
        }
        //멤버 포스트 개수 1개 증가
        member.setPostNumber(member.getPostNumber() + 1);

        log.info("저장된 post = {}",post);
        return ResponseEntity.ok(savedPost);
    }
    /**
     * 포스트 수정
     */
    @Transactional
    public ResponseEntity<Post> editPost(PostSaveDto postSaveDto,Long memberId, Long postId) {
        Post oldPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //포스트가 삭제되었으면
        if(oldPost.getStatus() == PostStatus.DELETED) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //생성자가 아니면 수정 불가
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!Objects.equals(oldPost.getMember().getMemberId(), memberId)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //포스트 정보 업데이트
        oldPost.setTitle(postSaveDto.getTitle());
        oldPost.setRoutePoint(postSaveDto.getRoutePoint());
        //썸네일 번호가 잘못되었을 경우 에러
        if(postSaveDto.getThumbnailIndex() < 0 || postSaveDto.getThumbnailIndex() >= postSaveDto.getCards().size()) throw new BadRequestException(ResponseMessages.BAD_REQUEST.getMessage());
        oldPost.setThumbnailIndex(postSaveDto.getThumbnailIndex());
        oldPost.setConcept(postSaveDto.getConcept());
        oldPost.setRegion(postSaveDto.getRegion());
        oldPost.setUpdatedTime(LocalDateTime.now());

        //해쉬태그 업데이트
        hashtagService.editPostHashtags(postSaveDto.getHashtags(),oldPost);

        //카드 업데이트
        List<CardSaveDto> updateCards = postSaveDto.getCards();
        int index = 0;
        for(CardSaveDto updateCard : updateCards) {
            updateCard.setCardIndex(index++);
        }
        cardService.editCards(postSaveDto.getCards(), oldPost, member);

        return ResponseEntity.ok(oldPost);
    }
    /**
     * 포스트 삭제
     */
    @Transactional
    public ResponseEntity<String> deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //포스트가 삭제되었으면 에러
        if(post.getStatus() == PostStatus.DELETED) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 멤버가 삭제되었으면 에러
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //생성자가 아니면 수정 불가
        if(!Objects.equals(post.getMember().getMemberId(), memberId)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        post.setStatus(PostStatus.DELETED);
        post.setUpdatedTime(LocalDateTime.now());
        //멤버 포스트 개수 1개 감소
        member.setPostNumber(member.getPostNumber() - 1);
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
            if(hashtagId == null) return ResponseEntity.ok(new PostListResponseDto(new ArrayList<>(),0));
            return ResponseEntity.ok(postRepository.searchPostByHashtag(page, pageSize, hashtagId, sortBy, concept, region, status));
        }
        //키워드 검색일 경우
        PostListResponseDto posts = postRepository.searchPostByKeyword(page, pageSize, searchKeyword, sortBy, concept, region, status);
        return ResponseEntity.ok(posts);
    }

    /**
     * 팔로잉하고 있는 멤버의 게시글을 목록으로 조회
     */
    public ResponseEntity<PostListResponseDto> getFollowingMemberPosts (Long memberId,int page, int pageSize, PostStatus status) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        return ResponseEntity.ok(postRepository.getFollowingMembersPostByMember(member, page, pageSize, status));
    }

    /**
     * 포스트 내용 조회
     */
    public ResponseEntity<PostInfoDto> getPostInfoById(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
//        Member member = memberRepository.findById(post.getMember().getMemberId()).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //포스트가 삭제되었으면 에러
        if(post.getStatus().equals(PostStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        redisService.addPostView(postId, memberId);
        return ResponseEntity.ok(toPostInfoDto(post,memberId));
    }

    /**
     * 조회수 랭킹으로 조회
     */
    public ResponseEntity<PostListResponseDto> getPostRankingList(int page, int pageSize, PostRankingDto range) {
        //랭킹 순위 리스트 조회
        List<String> list = redisService.getViewRankingList(page, pageSize, range);
        List<Long> rankingList = list.stream().map(Long::getLong).toList();
        //랭킹 최대 페이지 구하기
        int maxPage = redisService.getViewRankingMaxPageSize(pageSize, range);
        //포스트 목록 구해서 정렬하기
        List<Post> unorderedPosts = postRepository.getPostsByPostIdIsIn(rankingList);
        Map<Long, Post> postMap = unorderedPosts.stream().collect(Collectors.toMap(Post::getPostId, Function.identity()));
        List<Post> orderedPosts = rankingList.stream().map(postMap::get)
                .toList();
        //반환값으로 매핑
        return ResponseEntity.ok(toPostListResponseDto(orderedPosts, maxPage));
    }

    /**
     * 포스트 추천 토글
     */
    @Transactional
    public void postGoodToggle(Long memberId, Long postId) {
        PostGoodId postGoodId = new PostGoodId(memberId, postId);

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(member.getStatus().equals(MemberStatus.DELETED) || post.getStatus().equals(PostStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        if(post.getMember().getMemberId().equals(memberId)) throw new BadRequestException(ResponseMessages.GOOD_FAILED.getMessage());

        Optional<PostGood> memberGood = postGoodRepository.findById(postGoodId);
        //추천 삭제
        if(memberGood.isPresent()) {
            postGoodRepository.delete(memberGood.get());
            post.setGoodNumber(post.getGoodNumber() - 1);
        }
        //추천 생성
        else {
            postGoodRepository.save(new PostGood(postGoodId,member,post));
            post.setGoodNumber(post.getGoodNumber() + 1);
        }
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
        return new CardInfoDto(card.getCardId(), card.getCardIndex(), card.getLatitude(), card.getLongtitude(), card.getLocation(), card.getImage(), card.getContent());
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
    private static PostListResponseDto toPostListResponseDto(List<Post> posts, int maxPage) {
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
                    images
            );
        }).collect(Collectors.toList()), maxPage);
    }
}

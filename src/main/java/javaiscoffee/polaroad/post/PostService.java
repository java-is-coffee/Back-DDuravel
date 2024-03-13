package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardSaveDto;
import javaiscoffee.polaroad.post.card.CardService;
import javaiscoffee.polaroad.post.hashtag.HashtagService;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HashtagService hashtagService;
    private final CardService cardService;

    @Autowired
    public PostService(PostRepository postRepository, MemberRepository memberRepository,HashtagService hashtagService, CardService cardService) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.hashtagService = hashtagService;
        this.cardService = cardService;
    }

    /**
     * 포스트 저장
     */
    @Transactional
    public ResponseEntity<Post> savePost(PostSaveDto postSaveDto, Long memberId) {
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
            newCard.setIndex(cardIndex++);
            newCard.setPost(savedPost);
            cardService.saveCard(newCard);
        }

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
        if(!Objects.equals(oldPost.getMember().getMemberId(), memberId)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        //포스트 정보 업데이트
        oldPost.setTitle(postSaveDto.getTitle());
        oldPost.setRoutePoint(postSaveDto.getRoutePoint());
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
            updateCard.setIndex(index++);
        }
        cardService.editCards(postSaveDto.getCards(), oldPost);

        return ResponseEntity.ok(oldPost);
    }
    /**
     * 포스트 삭제
     */
    @Transactional
    public ResponseEntity<String> deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //포스트가 삭제되었으면
        if(post.getStatus() == PostStatus.DELETED) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //생성자가 아니면 수정 불가
        if(!Objects.equals(post.getMember().getMemberId(), memberId)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        post.setStatus(PostStatus.DELETED);
        post.setUpdatedTime(LocalDateTime.now());
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }
}

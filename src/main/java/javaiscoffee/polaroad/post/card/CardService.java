package javaiscoffee.polaroad.post.card;

import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberSimpleInfoDto;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostConcept;
import javaiscoffee.polaroad.post.PostListResponseDto;
import javaiscoffee.polaroad.post.PostSearchType;
import javaiscoffee.polaroad.post.hashtag.HashtagService;
import javaiscoffee.polaroad.response.ResponseMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final HashtagService hashtagService;
    private final MemberRepository memberRepository;

    @Autowired
    public CardService(CardRepository cardRepository,HashtagService hashtagService, MemberRepository memberRepository) {
        this.cardRepository = cardRepository;
        this.hashtagService = hashtagService;
        this.memberRepository = memberRepository;
    }

    /**
     * 포스트 생성할 때 카드 저장하는 메서드
     */
    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    /**
     * 포스트 수정할 때 카드 수정하는 메서드
     */
    public void editCards(List<CardSaveDto> updateCards, Post post, Member member) {
        List<Card> oldCards = cardRepository.findCardsByPostAndStatusOrderByCardIndexAsc(post, CardStatus.ACTIVE);

        // updateCards의 복사본 생성
        List<CardSaveDto> remainingNewCards = new ArrayList<>(updateCards);

        for (Card oldCard : oldCards) {
            Optional<CardSaveDto> matchingNewCardOpt = remainingNewCards.stream()
                    .filter(newCard -> newCard.getCardId() != null && newCard.getCardId().equals(oldCard.getCardId()))
                    .findFirst();

            if (matchingNewCardOpt.isPresent()) {
                // 기존 카드 내용 업데이트
                CardSaveDto matchingNewCard = matchingNewCardOpt.get();
                BeanUtils.copyProperties(matchingNewCard, oldCard, "cardId", "post", "status", "createdTime", "updatedTime");
                oldCard.setUpdatedTime(LocalDateTime.now());
                remainingNewCards.remove(matchingNewCard);
            } else {
                // 더 이상 사용되지 않는 카드 상태 변경
                oldCard.setStatus(CardStatus.DELETED);
            }
        }

        // 새로운 카드 엔터티 추가
        for (CardSaveDto newCardDto : remainingNewCards) {
            if (newCardDto.getCardId() == null) { // 새 카드는 ID가 없음
                Card newCard = new Card();
                BeanUtils.copyProperties(newCardDto, newCard, "cardId");
                newCard.setPost(post);
                newCard.setMember(member);
                cardRepository.save(newCard); // 새 카드 저장
            }
        }
    }

    public CardListResponseDto getCardListByMember(Long memberId,int page, int pageSize) {
        MemberSimpleInfoDto memberInfo = memberRepository.getMemberSimpleInfo(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(memberInfo.getStatus() == MemberStatus.DELETED) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdTime");
        Page<CardListDto> cardPage = cardRepository.findCardsByMemberAndStatusOrderByCreatedTimeDesc(memberInfo.getMemberId(), CardStatus.ACTIVE,pageable);

        return new CardListResponseDto(cardPage.getContent(), cardPage.getTotalPages());
    }

    public List<MapCardListDto> getMapCardList(PostSearchType searchType, String searchKeyword, PostConcept concept, double swLatitude, double neLatitude, double swLongitude, double neLongitude, int pageSize) {
        if(searchType.equals(PostSearchType.HASHTAG) && searchKeyword != null) {
            Long hashtagId = hashtagService.getHashtagIdByName(searchKeyword);
            if(hashtagId == null) return new ArrayList<>();
            return cardRepository.getMapCardListByHashtag(hashtagId, concept, swLatitude, neLatitude, swLongitude, neLongitude, pageSize);
        }
        return cardRepository.getMapCardListByKeyword(searchKeyword, concept, swLatitude, neLatitude, swLongitude, neLongitude, pageSize);
    }
}

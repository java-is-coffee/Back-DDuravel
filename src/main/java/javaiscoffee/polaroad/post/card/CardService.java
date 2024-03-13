package javaiscoffee.polaroad.post.card;

import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
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
}

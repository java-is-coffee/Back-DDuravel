package javaiscoffee.polaroad.album.albumCard;

import javaiscoffee.polaroad.album.Album;
import javaiscoffee.polaroad.album.AlbumRepository;
import javaiscoffee.polaroad.album.AlbumService;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class AlbumCardService {
    private final AlbumCardRepository albumCardRepository;
    private final AlbumRepository albumRepository;
    private final CardRepository cardRepository;

    public AlbumCardService(AlbumCardRepository albumCardRepository, AlbumRepository albumRepository, CardRepository cardRepository) {
        this.albumCardRepository = albumCardRepository;
        this.albumRepository = albumRepository;
        this.cardRepository = cardRepository;
    }

    public AlbumCard saveAlbumCard(Card card, Album album) {
        // 복합키 설정
        AlbumCardId albumCardId = new AlbumCardId();
        albumCardId.setAlbumId(album.getAlbumId());
        albumCardId.setCardId(card.getCardId());

        AlbumCard newAlbumCard = new AlbumCard();
        newAlbumCard.setId(albumCardId);
        newAlbumCard.setAlbum(album);
        newAlbumCard.setCard(card);
        return albumCardRepository.save(newAlbumCard);
    }

    public void editAlbumCard(List<Long> cardIdList, Album album) {
        // 기존 앨범의 앨범id로 앨범 조회 후 해당 앨범의 리스트 가져옴
        List<AlbumCard> oldAlbumCards = albumRepository.findById(album.getAlbumId()).get().getAlbumCards();
        log.info("기존 앨범 카드 리스트 = {}", oldAlbumCards);

        // 새로 수정된 앨범 카드 Set
        Set<AlbumCardId> updatedAlbumCardSet = cardIdList.stream()
                                .map(cardId -> new AlbumCardId(album.getAlbumId(), cardId))
                                .collect(Collectors.toSet());
        oldAlbumCards.forEach(oldAlbumCard -> {
            if (!updatedAlbumCardSet.contains(oldAlbumCard.getId().getCardId())) {    // 수정된 리스트에 기존 앨범카드id가 없으면
                albumCardRepository.delete(oldAlbumCard);     // 수정된 리스트에 없는 기존 앨범카드 삭제
            }
            // 수정된 리스트에 있는 기존 앨범카드id 지우기
            else {
                cardIdList.remove(oldAlbumCard.getId().getCardId());
            }
        });

        // 추가된 앨범 카드 저장
        for (Long cardId : cardIdList) {
            // 추가된 카드 id로 카드 객체 조회
            Card newCard = cardRepository.findById(cardId).get();

            // 생성 후 저장
            AlbumCardId albumCardId = new AlbumCardId();
            albumCardId.setAlbumId(album.getAlbumId());
            albumCardId.setCardId(newCard.getCardId());

            AlbumCard newAlbumCard = new AlbumCard();
            newAlbumCard.setId(albumCardId);
            newAlbumCard.setCard(newCard);
            newAlbumCard.setAlbum(album);
            albumCardRepository.save(newAlbumCard);
        }
    }

    public void addCard(List<Long> cardIdList, Album album) {
        for (Long cardId : cardIdList) {
            // 카드Id에 해당하는 카드 객체 조회
            Card addedCard = cardRepository.findById(cardId).get();

            // 생성 후 저장
            AlbumCardId albumCardId = new AlbumCardId();
            albumCardId.setAlbumId(album.getAlbumId());
            albumCardId.setCardId(addedCard.getCardId());

            AlbumCard newAlbumCard = new AlbumCard();
            newAlbumCard.setId(albumCardId);
            newAlbumCard.setCard(addedCard);
            newAlbumCard.setAlbum(album);
            albumCardRepository.save(newAlbumCard);
        }
    }

    //TODO
    // 앨범 카드 삭제 어떻게 할지 생각해보기
    public void deleteCard(List<Long> cardIdList, Album album) {
//        List<AlbumCard> oldAlbumCards = albumRepository.findById(album.getAlbumId()).get().getAlbumCards();

        for (Long cardId : cardIdList) {

            Card card = cardRepository.findById(cardId).get();
            // 해당 앨범카드에 있는 카드id인지 확인하고 포함하고있는 cardid이면 삭제?
//            albumCardRepository.delete();

        }
    }
}

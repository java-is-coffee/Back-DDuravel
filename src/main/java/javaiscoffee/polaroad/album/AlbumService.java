package javaiscoffee.polaroad.album;

import javaiscoffee.polaroad.album.albumCard.*;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import javaiscoffee.polaroad.post.card.CardRepository;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class AlbumService {
    private final JpaMemberRepository memberRepository;
    private final AlbumRepository albumRepository;
    private final AlbumCardService albumCardService;
    private final CardRepository cardRepository;
    private final AlbumCardRepository albumCardRepository;

    /**
     * 앨범 생성
     */
    public ResponseAlbumDto createAlbum(AlbumDto albumDto, Long memberId) {
        if (!memberId.equals(albumDto.getMemberId())) {
            throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        }
        Album newAlbum = new Album();
        BeanUtils.copyProperties(albumDto, newAlbum);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        newAlbum.setMember(member);
        newAlbum.setName(albumDto.getName());
        newAlbum.setDescription(albumDto.getDescription());
        Album savedAlbum = albumRepository.save(newAlbum);

        List<AlbumCard> albumCards = new ArrayList<>();
        albumDto.getCardIdList().forEach(albumCard -> {
            Card card = cardRepository.findCardByCardId(albumCard);
            // 작성 요청한 멤버 id와 카드의 멤버 id가 다른 경우
            if (!memberId.equals(card.getMember().getMemberId())) {
                throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
            }
            AlbumCard savedAlbumCard = albumCardService.saveAlbumCard(card, savedAlbum);
            albumCards.add(savedAlbumCard);
        });
        List<AlbumCardInfoDto> albumCardInfoDto = toAlbumCardInfoDto(albumCards);
        return toResponseAlbumDto(savedAlbum, albumCardInfoDto);
    }

    /**
     * 앨범 1개 조회
     */
    public ResponseAlbumDto getAlbum(Long memberId, Long albumId) {
        // 앨범이 없으면 NOT_FOUND
        Album findedAlbum = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElse(null);
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (member == null || member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(findedAlbum.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        List<AlbumCard> albumCard = albumCardRepository.findAllByAlbum(findedAlbum);
        List<AlbumCardInfoDto> albumCardInfoDto = toAlbumCardInfoDto(albumCard);
        return toResponseAlbumDto(findedAlbum, albumCardInfoDto);
    }

    /**
     * 앨범 수정
     */
    public ResponseAlbumDto editAlbum(EditAlbumDto editAlbumDto, Long albumId, Long memberId) {
        Album oldAlbum = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElse(null);
        // 멤버가 없는 경우, 멤버가 삭제된 경우
        if (member == null || member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(oldAlbum.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        // 수정된 내용 적용
        oldAlbum.setName(editAlbumDto.getName());
        oldAlbum.setDescription(editAlbumDto.getDescription());
        oldAlbum.setUpdatedTime(LocalDateTime.now());
        // save 메서드로 저장해주면 spring data jpa에서 알아서 변경사항을 반영함
        albumRepository.save(oldAlbum);

        // 앨범 카드 수정
        albumCardService.editAlbumCard(editAlbumDto.getCardIdList(), oldAlbum);
        List<AlbumCard> albumCards = albumCardRepository.findAllByAlbum(oldAlbum);
        List<AlbumCardInfoDto> albumCardInfoDtos = toAlbumCardInfoDto(albumCards);

        return toResponseAlbumDto(oldAlbum,albumCardInfoDtos);
    }

    /**
     * 앨범 삭제
     */
    public ResponseEntity<String> deleteAlbum(Long memberId, Long albumId) {
        // 삭제 요청한 앨범이 없는 경우
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElse(null);
        // 멤버가 없는 경우, 멤버가 삭제된 경우
        if (member == null || member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(album.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        // albumCard 삭제
        List<AlbumCard> albumCards = albumCardRepository.findAllByAlbum(album);
        for (AlbumCard albumCard : albumCards) {
            albumCardRepository.delete(albumCard);
        }

        albumRepository.deleteById(albumId);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    /**
     * 앨범에 카드 추가
     */
    public ResponseAlbumDto addCard(RequestToAddAlbumCardDto addAlbumCardDto, Long albumId, Long memberId) {
        return null;
    }


    /**
     * Album, AlbumCardInfo 리스트를 ResponseAlbumDto로 매핑
     */
    public static ResponseAlbumDto toResponseAlbumDto(Album album, List<AlbumCardInfoDto> albumCardInfoDto) {
        if (album == null) {
            return null;
        }
        ResponseAlbumDto responseAlbumDto = new ResponseAlbumDto();

        responseAlbumDto.setAlbumId(album.getAlbumId());
        responseAlbumDto.setMemberId(album.getMember().getMemberId());
        responseAlbumDto.setName(album.getName());
        responseAlbumDto.setDescription(album.getDescription());
        responseAlbumDto.setUpdatedTime(album.getUpdatedTime());
        responseAlbumDto.setAlbumCardInfoList(albumCardInfoDto);


        return responseAlbumDto;
    }

    /**
     * AlbumCard 객체 리스트를 AlbumCardInfoDto 객체 리스트로 매핑
     */
    public static List<AlbumCardInfoDto> toAlbumCardInfoDto(List<AlbumCard> albumCard) {

        List<AlbumCardInfoDto> albumCardInfoDtoList = new ArrayList<>();

        for (AlbumCard card : albumCard) {
            AlbumCardInfoDto albumCardInfoDto = new AlbumCardInfoDto();
            CardInfoDto cardInfoDto = toCardInfoDto(card.getCard());
            albumCardInfoDto.setCardInfo(cardInfoDto);
            albumCardInfoDtoList.add(albumCardInfoDto);
        }

        return albumCardInfoDtoList;
    }

    /**
     * Card 객체를 CardInfo 객체로 매핑
     */
    public static CardInfoDto toCardInfoDto(Card card) {
        CardInfoDto cardInfoDto = new CardInfoDto();

        cardInfoDto.setCardId(card.getCardId());
        cardInfoDto.setCardIndex(card.getCardIndex());
        cardInfoDto.setLatitude(card.getLatitude());
        cardInfoDto.setLongtitude(card.getLongtitude());
        cardInfoDto.setLocation(card.getLocation());
        cardInfoDto.setImage(card.getImage());
        cardInfoDto.setContent(card.getContent());

        return cardInfoDto;
    }
}

package javaiscoffee.polaroad.album;

import javaiscoffee.polaroad.album.albumCard.*;
import javaiscoffee.polaroad.exception.BadRequestException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class AlbumService {
    private final JpaMemberRepository memberRepository;
    private final AlbumRepository albumRepository;
    private final AlbumCardService albumCardService;
    private final CardRepository cardRepository;
    private final AlbumCardRepository albumCardRepository;

    @Autowired
    public AlbumService(JpaMemberRepository memberRepository, AlbumRepository albumRepository, AlbumCardService albumCardService, CardRepository cardRepository, AlbumCardRepository albumCardRepository) {
        this.memberRepository = memberRepository;
        this.albumRepository = albumRepository;
        this.albumCardService = albumCardService;
        this.cardRepository = cardRepository;
        this.albumCardRepository = albumCardRepository;
    }

    public ResponseAlbumDto createAlbum(AlbumDto albumDto, Long memberId) {
        Album newAlbum = new Album();
        BeanUtils.copyProperties(albumDto, newAlbum);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        newAlbum.setMember(member);
        Album savedAlbum = albumRepository.save(newAlbum);

        List<AlbumCard> albumCards = new ArrayList<>();
        albumDto.getCardIdList().forEach(albumCard -> {
            Card card = cardRepository.findById(albumCard).orElseThrow(() -> new BadRequestException(ResponseMessages.INPUT_ERROR.getMessage()));
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
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(findedAlbum.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        List<AlbumCard> albumCard = albumCardRepository.findAllByAlbum(findedAlbum);
        List<AlbumCardInfoDto> albumCardInfoDto = toAlbumCardInfoDto(albumCard);
        return toResponseAlbumDto(findedAlbum, albumCardInfoDto);
    }

    public ResponseAlbumDto editAlbum(AlbumDto editAlbumDto, Long albumId, Long memberId) {
        Album oldAlbum = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
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

    public ResponseEntity<String> deleteAlbum(Long memberId, Long albumId) {
        // 삭제 요청한 앨범이 없는 경우
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
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

    public ResponseAlbumDto addAlbumCard(RequestAlbumCardDto addAlbumCardDto, Long albumId, Long memberId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(album.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        // 새로 추가된 앨범 카드 저장
        List<Long> cardIdList = addAlbumCardDto.getCardId();
        albumCardService.addCard(cardIdList, album);

        List<AlbumCardInfoDto> albumCardInfoDto = toAlbumCardInfoDto(album.getAlbumCards());
        return toResponseAlbumDto(album, albumCardInfoDto);
    }

    public ResponseAlbumDto deleteAlbumCard(RequestAlbumCardDto deleteAlbumCardDto, Long albumId, Long memberId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(album.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        // 앨범에서 앨범 카드 삭제
        List<Long> cardIdList = deleteAlbumCardDto.getCardId();
        albumCardService.deleteCard(cardIdList, album);

        List<AlbumCardInfoDto> albumCardInfoDto = toAlbumCardInfoDto(album.getAlbumCards());
        return toResponseAlbumDto(album, albumCardInfoDto);
    }

    public SliceAlbumListDto<AlbumInfoDto> getPagedAlbumList(int page, Long memberId) {
        page = (page == 0) ? 0 : (page - 1);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //HACK: 요청한 멤버가 앨범 생성자가 아닌 경우는 어떻게 할 것인지

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdTime").ascending());
        Slice<Album> albumSlice = albumRepository.findAlbumSlicedByMemberId(memberId, pageable);
        List<Album> albumList = albumSlice.getContent();
        List<AlbumInfoDto> albumInfoDtoList = toAlbumInfoDtoList(albumList);
        return new SliceAlbumListDto<>(albumInfoDtoList, albumSlice.hasNext());
    }

    //NOTE: 매핑하는 메서드 사용 하기 위해서 AlbumCardService가 아닌 AlbumService 작성함
    public SliceAlbumCardInfoDto<AlbumCardInfoDto> getPagedAlbumCardList(Long memberId, Long albumId, int page) {
        page = (page == 0) ? 0 : (page - 1);
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //  멤버가 없는 경우, 멤버가 삭제된 경우
        if (!member.getStatus().equals(MemberStatus.ACTIVE)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        // 요청한 멤버가 앨범 생성자가 아닌 경우
        if (!memberId.equals(album.getMember().getMemberId())) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        Pageable pageable =  PageRequest.of(page, 10, Sort.by("createdTime").ascending());
        Slice<AlbumCard> albumCardSlice = albumRepository.findAlbumCardSlicedByAlbum(album, pageable);
        List<AlbumCard> albumCardList = albumCardSlice.getContent();
        List<AlbumCardInfoDto> albumCardInfoDtoList = toAlbumCardInfoDto(albumCardList);

        return new SliceAlbumCardInfoDto<>(albumId, albumCardInfoDtoList, albumCardSlice.hasNext());
    }


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

    public static List<AlbumInfoDto> toAlbumInfoDtoList(List<Album> albums) {
        if (albums == null) {
            return null;
        }

        List<AlbumInfoDto> albumInfoDtoList = new ArrayList<>();
        for (Album album : albums) {
            AlbumInfoDto albumInfoDto = toAlbumInfoDto(album);
            albumInfoDtoList.add(albumInfoDto);
        }

        return albumInfoDtoList;
    }

    public static AlbumInfoDto toAlbumInfoDto(Album album) {
        AlbumInfoDto albumInfoDto = new AlbumInfoDto();
        albumInfoDto.setAlbumId(album.getAlbumId());
        albumInfoDto.setMemberId(album.getMember().getMemberId());
        albumInfoDto.setName(album.getName());
        albumInfoDto.setDescription(album.getDescription());
        albumInfoDto.setUpdatedTime(LocalDateTime.now());
        return albumInfoDto;
    }

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

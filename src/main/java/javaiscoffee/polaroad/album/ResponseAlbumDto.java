package javaiscoffee.polaroad.album;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.album.albumCard.AlbumCard;
import javaiscoffee.polaroad.album.albumCard.AlbumCardInfoDto;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseAlbumDto {
    @Schema(description = "앨범 id", example = "1")
    private Long albumId;
    @Schema(description = "멤버 id", example = "1")
    private Long memberId;
    @Schema(description = "앨범 이름", example = "제주도")
    private String name;
    @Schema(description = "앨범 간단한 설명", example = "제주도 맛집 여행 앨범")
    private String description;
    @Schema(description = "앨범 카드 정보 리스트")
    private List<AlbumCardInfoDto> albumCardInfoList;
    @Schema(description = "업데이트된 시간")
    private LocalDateTime updatedTime;
}

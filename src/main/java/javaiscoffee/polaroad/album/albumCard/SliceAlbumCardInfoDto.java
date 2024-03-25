package javaiscoffee.polaroad.album.albumCard;

import io.swagger.v3.oas.annotations.media.Schema;
import javaiscoffee.polaroad.post.card.CardInfoDto;
import lombok.Data;

import java.util.List;

@Data
public class SliceAlbumCardInfoDto<T> {
    @Schema(description = "앨범 id")
    private Long albumId;
    @Schema(description = "카드 정보")
    private List<AlbumCardInfoDto> albumCards;
    @Schema(description = "## 다음 페이지가 있는지 여부", example = "true")
    private boolean hasNext;

    public SliceAlbumCardInfoDto(Long albumId, List<AlbumCardInfoDto> albumCards, boolean hasNext) {
        this.albumId = albumId;
        this.albumCards = albumCards;
        this.hasNext = hasNext;
    }
}

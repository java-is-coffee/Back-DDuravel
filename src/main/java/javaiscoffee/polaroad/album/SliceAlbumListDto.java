package javaiscoffee.polaroad.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SliceAlbumListDto<T> {
    @Schema(description = "## 앨범 목록")
    private List<AlbumInfoDto> albumList;
    @Schema(description = "## 다음 페이지가 있는지 여부", example = "true")
    private boolean hasNext;

    public SliceAlbumListDto(List<AlbumInfoDto> albumList, boolean hasNext) {
        this.albumList = albumList;
        this.hasNext = hasNext;
    }
}

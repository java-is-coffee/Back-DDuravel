package javaiscoffee.polaroad.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "앨범 목록 조회시 넘겨주는 responseDto")
public class AlbumInfoDto {
    @Schema(description = "앨범 id", example = "1")
    private Long albumId;
    @Schema(description = "멤버 id", example = "1")
    private Long memberId;
    @Schema(description = "앨범 이름", example = "제주도")
    private String name;
    @Schema(description = "앨범 간단한 설명", example = "제주도 맛집 여행 앨범")
    private String description;
    @Schema(description = "썸네일 url", example = "http://")
    private String thumbnail;
    @Schema(description = "업데이트된 시간")
    private LocalDateTime updatedTime;
}

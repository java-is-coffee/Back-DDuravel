package javaiscoffee.polaroad.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "앨범 생성 정보 받는 requestDto")
public class AlbumDto {
    @Schema(description = "멤버 id", example = "1")
    private Long memberId;
    @Schema(description = "앨범 이름", example = "제주도")
    private String name;
    @Schema(description = "앨범 간단한 설명", example = "제주도 맛집 여행 앨범")
    private String description;
    @Schema(description = "카드 Id 리스트")
    private List<Long> cardIdList;
}

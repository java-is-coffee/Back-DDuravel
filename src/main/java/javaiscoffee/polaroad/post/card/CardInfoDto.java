package javaiscoffee.polaroad.post.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "포스트 카드 정보를 넘겨주는 Dto")
public class CardInfoDto {
    @Schema(description = "카드 ID", example = "1")
    private Long cardId;
    @Schema(description = "카드 순서 번호", example = "1~20")
    private int cardIndex;
    @Schema(description = "위도", example = "134243.185152")
    private String latitude;
    @Schema(description = "경도", example = "134243.185152")
    private String longtitude;
    @Schema(description = "상세 위치", example = "인천광역시 연수구")
    private String location;
    @Schema(description = "카드 이미지 url", example = "http:")
    private String image;
    @Schema(description = "카드 글 내용", example = "미안하다 이거 보여주려고 어그로 끌었다.")
    private String content;
}

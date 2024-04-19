package javaiscoffee.polaroad.post.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

/**
 * 카드 생성시 사용되는 Dto
 */
@Data
@Schema(description = "카드 정보 넘겨주는 Dto")
@ToString
public class CardSaveDto {
    @Schema(description = "카드 Id \n ## 수정할 때는 기존 카드 id를 넣어주세요. \n ## 생성할 때는 null로 넘겨주시면 됩니다.", example = "0")
    private Long cardId;
    @Max(10)
    @Schema(description = "카드 순서 \n 없어도 됩니다.", example = "0")
    private int cardIndex;      // 자동으로 지정되는 값
    @Size(max = 255)
    @Schema(description = "세부 위치", example = "인천시 남동구")
    private String location;// 사진 세부 위치
    @Schema(description = "위도 좌표", example = "123851.134521")
    private double latitude;//위도
    @Schema(description = "경도 좌표 \n", example = "543512.874521")
    private double longitude;  //경도
    @Schema(description = "이미지 url", example = "https://krampolineImage.com/java-is-coffee")
    private String image;
    @Schema(description = "카드 본문", example = "여기가 꽃놀이 명소입니다.")
    private String content;
}

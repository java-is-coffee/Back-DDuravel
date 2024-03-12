package javaiscoffee.polaroad.post.card;

import lombok.Data;

/**
 * 카드 생성시 사용되는 Dto
 */
@Data
public class CardSaveDto {
    private Long cardId;
    private String location; // 사진 세부 위치
    private String latitude; //위도
    private String longtitude;//경도
    private String image;
    private String content;
}

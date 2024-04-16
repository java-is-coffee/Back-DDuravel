package javaiscoffee.polaroad.post.card;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapCardListDto {
    private Long postId;
    private Long cardId;
    private String image;
    private String content;
    private String location;
    private double latitude; // 위도
    private double longitude; // 경도
}

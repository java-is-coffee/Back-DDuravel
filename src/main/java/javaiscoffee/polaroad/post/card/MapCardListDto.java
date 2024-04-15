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
@SqlResultSetMapping(
        name = "MapCardListDtoMapping",
        classes = {
                @ConstructorResult(
                        targetClass = MapCardListDto.class,
                        columns = {
                                @ColumnResult(name = "post_id", type = Long.class),
                                @ColumnResult(name = "card_id", type = Long.class),
                                @ColumnResult(name = "image", type = String.class),
                                @ColumnResult(name = "content", type = String.class),
                                @ColumnResult(name = "location", type = String.class),
                                @ColumnResult(name = "latitude", type = Double.class),
                                @ColumnResult(name = "longitude", type = Double.class)
                        }
                )
        }
)
public class MapCardListDto {
    private Long postId;
    private Long cardId;
    private String image;
    private String content;
    private String location;
    private double latitude; // 위도
    private double longitude; // 경도
}

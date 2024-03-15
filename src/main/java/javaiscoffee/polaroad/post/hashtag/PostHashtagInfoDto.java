package javaiscoffee.polaroad.post.hashtag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "포스트 해쉬태그 정보", example = "1")
public class PostHashtagInfoDto {
    @Schema(description = "해쉬태그 ID", example = "1")
    private Long hashtagId;
    @Schema(description = "태그이름", example = "꽃놀이")
    private String tagName;
}

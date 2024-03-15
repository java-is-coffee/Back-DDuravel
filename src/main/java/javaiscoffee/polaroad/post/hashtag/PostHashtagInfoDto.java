package javaiscoffee.polaroad.post.hashtag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostHashtagInfoDto {
    private Long hashtagId;
    private String tagName;
}

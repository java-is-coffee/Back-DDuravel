package javaiscoffee.polaroad.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostSimpleInfoDto {
    private Long postId;
    private Long memberId;
    private PostStatus status;
}

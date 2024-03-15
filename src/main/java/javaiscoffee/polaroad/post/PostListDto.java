package javaiscoffee.polaroad.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 검색이나 탐색페이지에서 필요한 정보를 응답으로 넘겨주는 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListDto {
    private String title;
    private Long postId;
    private String nickname;
    private int goodNumber;
    private PostConcept concept;
    private PostRegion region;
    private List<String> images;
}

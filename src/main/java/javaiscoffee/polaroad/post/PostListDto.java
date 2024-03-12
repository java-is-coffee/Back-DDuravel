package javaiscoffee.polaroad.post;

import lombok.Data;

import java.util.List;

/**
 * 검색이나 탐색페이지에서 필요한 정보를 응답으로 넘겨주는 Dto
 */
@Data
public class PostListDto {
    private String title;
    private String nickname;
    private int goodNumber;
    private PostRegion region;
    private List<String> images;
}

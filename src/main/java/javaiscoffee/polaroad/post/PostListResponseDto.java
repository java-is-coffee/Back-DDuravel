package javaiscoffee.polaroad.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponseDto {
    private List<PostListDto> posts;
    private int maxPage;
}

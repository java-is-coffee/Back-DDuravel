package javaiscoffee.polaroad.post;

import java.util.List;

public interface QueryPostRepository {
    List<Post> findPostByEmail(String email);

    List<PostListDto> searchPost(int paging, int pagingNumber, String searchWords, PostListSort order, PostConcept concept, PostRegion region);
}

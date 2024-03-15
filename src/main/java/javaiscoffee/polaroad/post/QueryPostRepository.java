package javaiscoffee.polaroad.post;

import java.util.List;

public interface QueryPostRepository {
    //테스트용 메서드
    List<Post> findPostByEmail(String email);

    List<PostListDto> searchPostByKeyword(int paging, int pagingNumber, String searchWords, PostListSort order, PostConcept concept, PostRegion region);

    List<PostListDto> searchPostByHashtag(int paging, int pagingNumber, Long hashtagId, PostListSort order, PostConcept concept, PostRegion region);

    Post getPostInfoById(Long postId);
}

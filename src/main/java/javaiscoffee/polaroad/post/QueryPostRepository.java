package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.member.Member;

import java.util.List;

public interface QueryPostRepository {
    //테스트용 메서드
    List<Post> findPostByEmail(String email);

    PostListResponseDto searchPostByKeyword(int page, int pageSize, String searchWords, PostListSort order, PostConcept concept, PostRegion region, PostStatus status);

    PostListResponseDto searchPostByHashtag(int page, int pageSize, Long hashtagId, PostListSort order, PostConcept concept, PostRegion region, PostStatus status);

    PostInfoDto getPostInfoById(Long postId, Long memberId);

    PostInfoCachingDto getPostCachingDtoById(Long postId);

    PostListResponseDto getFollowingMembersPostByMember(Long memberId,int page, int pageSize, PostStatus status);
}

package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QueryPostRepository {
    @Query("select new javaiscoffee.polaroad.post.PostListRepositoryDto(p.title, p.postId, p.member.nickname, p.thumbnailIndex, p.goodNumber, p.concept, p.region, p.cards, p.updatedTime) from Post p where p.postId in :PostList")
    List<PostListRepositoryDto> getPostsByPostIdIsIn(@Param("PostList") List<Long> postList);
    Page<Post> findPostsByMemberMemberIdAndStatusOrderByCreatedTimeDesc(Long memberId, PostStatus status, Pageable pageable);

    //배치 처리로 좋아요 변동 업데이트
    @Modifying
    @Transactional
    @Query("update Post p set p.goodNumber = p.goodNumber + :changeNumber where p.postId = :postId")
    void updatePostGoodNumber(@Param("changeNumber") int changeNumber,@Param("postId") Long postId);

    // 포스트 간단 정보만 조회하는 메서드
    @Query("select new javaiscoffee.polaroad.post.PostSimpleInfoDto(p.postId, p.member.memberId, p.status) from Post p where p.postId = :postId")
    Optional<PostSimpleInfoDto> getPostSimpleInfo(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("update Post p set p.status = :status where p.postId = :postId")
    void updatePostStatus(@Param("postId") Long postId, @Param("status") PostStatus status);
}

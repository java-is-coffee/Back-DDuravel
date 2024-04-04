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

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QueryPostRepository {
    List<Post> getPostsByPostIdIsIn(List<Long> postIds);
    Page<Post> findPostsByMemberMemberIdAndStatusOrderByCreatedTimeDesc(Long memberId, PostStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Post p set p.goodNumber = p.goodNumber + :changeNumber where p.postId = :postId")
    void updatePostGoodNumber(@Param("changeNumber") int changeNumber,@Param("postId") Long postId);

    @Query("select new javaiscoffee.polaroad.post.PostSimpleInfoDto(p.postId, p.member.memberId, p.status) from Post p where p.postId = :postId")
    PostSimpleInfoDto getPostSimpleInfo(@Param("postId") Long postId);
}

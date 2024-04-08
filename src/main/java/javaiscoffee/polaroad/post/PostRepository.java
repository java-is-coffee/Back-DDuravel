package javaiscoffee.polaroad.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QueryPostRepository {

    @Query(value = "select p.title, p.post_id, m.nickname, p.thumbnail_index, p.good_number, p.concept, p.region, p.updated_time, c.card_index, c.image " +
            "from posts p inner join member m on p.member_id = m.member_id left join cards c on p.post_id = c.post_id " +
            "where p.post_id in :PostList", nativeQuery = true)
    List<Object[]> getPostsWithCardsByPostId(@Param("PostList") List<Long> postList);
    Slice<Post> findPostsByMemberMemberIdAndStatusOrderByCreatedTimeDesc(Long memberId, PostStatus status, Pageable pageable);

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

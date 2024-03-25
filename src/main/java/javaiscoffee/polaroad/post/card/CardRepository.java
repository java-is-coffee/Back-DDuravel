package javaiscoffee.polaroad.post.card;

import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    //포스트의 카드 조회
    public List<Card> findCardsByPostAndStatusOrderByCardIndexAsc(Post post, CardStatus status);

    //마이페이지에서 자기가 업로드한 카도 조회
    public Page<Card> findCardsByMemberAndStatusOrderByCreatedTimeDesc(Member member, CardStatus status, Pageable pageable);
}

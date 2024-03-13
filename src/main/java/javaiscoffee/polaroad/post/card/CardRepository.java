package javaiscoffee.polaroad.post.card;

import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    public List<Card> findCardsByPostAndStatusOrderByIndexAsc(Post post, CardStatus status);
}

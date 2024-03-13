package javaiscoffee.polaroad.post.card;

import javaiscoffee.polaroad.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    public List<Card> findCardsByPostAndStatusOrderByCardIndexAsc(Post post, CardStatus status);
}

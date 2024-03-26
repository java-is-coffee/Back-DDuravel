package javaiscoffee.polaroad.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QueryPostRepository {
    List<Post> getPostsByPostIdIsIn(List<Long> postIds);
}

package javaiscoffee.polaroad.post;

import java.util.List;

public interface QueryPostRepository {
    List<Post> findPostByEmail(String email);
}

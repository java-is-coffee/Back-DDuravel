package javaiscoffee.polaroad.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("prod")
public class ElasticPostController {
    private final ElasticPostService postService;

    @Autowired
    public ElasticPostController(ElasticPostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<ElasticPost> savePost(ElasticPost post) {
        return ResponseEntity.ok(postService.savePost(post));
    }

    @GetMapping
    public ResponseEntity<ElasticPost> findPost(@RequestParam(name = "postId") Long postId) {
        return ResponseEntity.ok(postService.findPost(postId));
    }
}

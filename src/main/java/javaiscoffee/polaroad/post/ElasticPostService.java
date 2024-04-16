package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.response.ResponseMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod2")
public class ElasticPostService {
    private final ElasticPostRepository elasticPostRepository;

    @Autowired
    public ElasticPostService(ElasticPostRepository elasticPostRepository) {
        this.elasticPostRepository = elasticPostRepository;
    }

    public ElasticPost savePost(ElasticPost post) {
        ElasticPost testPost = ElasticPost.builder()
                .title("테스트 1")
                .routePoint("테스트 좌표")
                .goodNumber(0)
                .reviewNumber(0)
                .thumbnailIndex(0)
                .concept(PostConcept.FOOD)
                .region(PostRegion.BUSAN)
                .status(PostStatus.ACTIVE)
                .build();
        return elasticPostRepository.save(testPost);
    }

    public ElasticPost findPost(Long postId) {
        return elasticPostRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.POST_NOT_FOUND.getMessage()));
    }
}

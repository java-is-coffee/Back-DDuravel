package javaiscoffee.polaroad.post;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

@Profile("prod2")
public interface ElasticPostRepository extends ElasticsearchRepository<ElasticPost, Long>, CrudRepository<ElasticPost, Long> {
    ElasticPost getElasticPostByPostId(Long postId);
}

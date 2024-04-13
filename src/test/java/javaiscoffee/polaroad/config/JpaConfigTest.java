package javaiscoffee.polaroad.config;

import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.post.ElasticPostController;
import javaiscoffee.polaroad.post.ElasticPostRepository;
import javaiscoffee.polaroad.post.ElasticPostService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;


@TestConfiguration
public class JpaConfigTest {
    private final EntityManager em;
    public JpaConfigTest(EntityManager em) {this.em = em;}

    @Bean
    public JpaMemberRepository jpaMemberRepository(EntityManager em) {
        return new JpaMemberRepository(em);
    }

    @Bean
    @Profile("test")
    public ElasticPostController elasticPostController() { return mock(ElasticPostController.class);}

    @Bean
    @Profile("test")
    public ElasticPostService elasticPostService() { return mock(ElasticPostService.class);}

    @Bean
    @Profile("test")
    public ElasticPostRepository elasticPostRepository() { return mock(ElasticPostRepository.class);}
}
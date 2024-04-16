package javaiscoffee.polaroad.config;

import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.review.JpaReviewRepository;
import javaiscoffee.polaroad.review.ReviewRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class JpaConfigTest {
    private final EntityManager em;
    public JpaConfigTest(EntityManager em) {this.em = em;}

    @Bean
    public JpaMemberRepository jpaMemberRepository(EntityManager em) {
        return new JpaMemberRepository(em);
    }

    @Bean
    public JpaReviewRepository jpaReviewRepository(EntityManager em) {
        return new JpaReviewRepository(em);
    }
}
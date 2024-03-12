package javaiscoffee.polaroad.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.post.QPost;
import jakarta.persistence.EntityManager;

import java.util.List;

public class QueryPostRepositoryImpl implements QueryPostRepository{

    private final JPAQueryFactory queryFactory;
    public QueryPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findPostByEmail(String email) {
        QPost post = QPost.post; // Querydsl QClass
        return queryFactory.selectFrom(post)
                .where(post.member.email.eq(email))
                .orderBy(post.createdTime.desc())
                .fetch();
    }
}

package ext.javaiscoffee.polaroad.member;

import static com.querydsl.core.types.PathMetadataFactory.*;
import javaiscoffee.polaroad.member.Member;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 148051466L;

    public static final QMember member = new QMember("member1");

    public final DatePath<java.time.LocalDate> createdTime = createDate("createdTime", java.time.LocalDate.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> followedNumber = createNumber("followedNumber", Integer.class);

    public final NumberPath<Integer> followingNumber = createNumber("followingNumber", Integer.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> postNumber = createNumber("postNumber", Integer.class);

    public final StringPath profileImage = createString("profileImage");

    public final EnumPath<javaiscoffee.polaroad.member.MemberRole> role = createEnum("role", javaiscoffee.polaroad.member.MemberRole.class);

    public final EnumPath<javaiscoffee.polaroad.member.SocialLogin> socialLogin = createEnum("socialLogin", javaiscoffee.polaroad.member.SocialLogin.class);

    public final EnumPath<javaiscoffee.polaroad.member.MemberStatus> status = createEnum("status", javaiscoffee.polaroad.member.MemberStatus.class);

    public final DatePath<java.time.LocalDate> updatedTime = createDate("updatedTime", java.time.LocalDate.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}


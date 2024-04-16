package javaiscoffee.polaroad.withCustomMockUser;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    String email() default "aaa@naver.com";

    String name() default "박자바";

    String nickname() default "자바커피";

    String password() default "a123123!";
}

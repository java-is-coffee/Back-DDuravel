package javaiscoffee.polaroad.withCustomMockUser;

import javaiscoffee.polaroad.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser>  {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        String email = annotation.email();
        String name = annotation.name();
        String nickname = annotation.nickname();
        String password = annotation.password();

        // 사용자의 ID와 권한 정보를 생성
        Long memberId = 1L; // 예시로 임의의 사용자 ID를 설정
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER"); // 사용자의 권한 설정

        // 사용자의 인증 정보를 생성하여 SecurityContext에 설정
        Authentication auth = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(name, email, memberId, authorities),
                password,
                authorities
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}

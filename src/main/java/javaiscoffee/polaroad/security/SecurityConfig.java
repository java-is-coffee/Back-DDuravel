package javaiscoffee.polaroad.security;

import javaiscoffee.polaroad.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .httpBasic().disable()
                //rest api이므로 basic auth 및 csrf 보안을 사용하지 않는다는 설정
                .csrf((csrf) -> csrf.disable())
                //JWT를 사용하기 때문에 세션을 사용하지 않는다는 설정
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                //로그인과 회원가입은 모든 요청을 허가
                .requestMatchers(new AntPathRequestMatcher("/api/member/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/login/reset-password")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/register/email-check")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/register/send-certification")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/member/refresh")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/oauth2/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/login/oauth2/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/email/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/oauth2callback")).permitAll()
                //swagger 인증 예외
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/index.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs")).permitAll()
                //테스트 api 인증 예외
                .requestMatchers(new AntPathRequestMatcher("/api/test")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/db")).permitAll()
                //그 외 나머지 요청은 전부 인증이 필요
                .anyRequest().authenticated();

        // /error 엔트리 포인트 진입했을 경우 에러 응답을 반환하도록 커스텀 핸들러 추가
        //JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행하겠다는 설정
        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 허용, but allowedOriginPattern is used to allow all origins

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }
}

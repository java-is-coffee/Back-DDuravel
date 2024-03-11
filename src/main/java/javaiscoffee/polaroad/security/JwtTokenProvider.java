package javaiscoffee.polaroad.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javaiscoffee.polaroad.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final MemberRepository memberRepository;

    public JwtTokenProvider(@Value("${JWT_SECRET_KEY}")String secretKey, MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
    }

    //유저 정보를 가지고 있는 AccessToken, RefreshToken을 생성하는 메서드
    public TokenDto generateToken(Authentication authentication) {
        log.info("authentication = {}",authentication);
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date().getTime());

        Object principal = authentication.getPrincipal();

        log.info("authorities = {}",authorities);
        log.info("principal = {}",principal);

        //Access Token 생성 30분
        Date accessTokenExpiresIn = new Date(now + (1000*60*30));
        Long memberId;
        String accessToken;

        if (principal instanceof CustomUserDetails) {
            memberId = ((CustomUserDetails)principal).getMemberId();
            accessToken = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("auth", authorities)
                    .claim("memberId", memberId) // memberId 정보 추가
                    .setExpiration(accessTokenExpiresIn)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } else if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            // memberId를 attributes에서 가져오기
            memberId = oAuth2User.getAttribute("memberId");
            log.info("토큰 생성 memberId = {}",memberId);
            accessToken = Jwts.builder()
                    .setSubject(oAuth2User.getAttribute("email"))
                    .claim("auth", authorities)
                    .claim("memberId", memberId) // memberId 정보 추가
                    .setExpiration(accessTokenExpiresIn)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new IllegalArgumentException("Unsupported principal type");
        }

        //Refresh Token 생성 1주일
        String refreshToken = Jwts.builder()
                .claim("memberId", memberId)
                .setExpiration(new Date(now + (1000 * 60 * 60 * 24 * 7)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

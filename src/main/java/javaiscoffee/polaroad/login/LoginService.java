package javaiscoffee.polaroad.login;

import javaiscoffee.polaroad.login.emailAuthentication.EmailCertificationRepository;
import javaiscoffee.polaroad.login.emailAuthentication.MailVerifyService;
import javaiscoffee.polaroad.response.ResponseStatus;
import javaiscoffee.polaroad.security.BaseException;
import javaiscoffee.polaroad.security.JwtTokenProvider;
import javaiscoffee.polaroad.security.TokenDto;
import javaiscoffee.polaroad.member.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
//    private final MailVerifyService mailVerifyService;
    private final EmailCertificationRepository emailCertificationRepository;

    /**
     * 1. 로그인 요청으로 들어온 memberId, password를 기반으로 Authentication 객체를 생성한다.
     * 2. authenticate() 메서드를 통해 요청된 Member에 대한 검증이 진행된다.
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성한다.
     */
    public TokenDto login(LoginDto loginDto) {
        log.info("로그인 검사 시작 loginDto={}",loginDto);
        Member member = memberRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new BaseException(ResponseStatus.NOT_FOUND.getMessage()));
        if(member.getStatus() == MemberStatus.DELETED) throw new BaseException(ResponseStatus.NOT_FOUND.getMessage());
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        log.info("authenticationToken = {}",authenticationToken);
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("Authentication successful, authentication = {}", authentication);

            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            log.info("로그인 성공, tokenDto={}", tokenDto);
            return tokenDto;
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public Member register(RegisterDto registerDto) {
        //이미 중복된 이메일이 존재
        if(memberRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            log.info("중복 회원가입 실패 처리");
            return null;
        }

        //이메일 인증한 적이 없으면 예외처리
//        if (!mailVerifyService.isVerified(registerDto.getEmail(), registerDto.getCertificationNumber())) {
//            log.info("이메일 인증을 하지 않았습니다.");
//            return null;
//        }


        //중복이 없으면 회원가입 진행
        Member newMember = new Member(registerDto.getEmail(), registerDto.getName(), registerDto.getNickname(), registerDto.getPassword(), "", 0, 0, 0, MemberRole.USER);
        newMember.hashPassword(bCryptPasswordEncoder);
        log.info("save하려는 멤버 = {}", newMember);
        memberRepository.save(newMember);
        Optional<Member> savedMember = memberRepository.findByEmail(newMember.getEmail());
        if(savedMember.isPresent()) {
            log.info("회원가입 성공 = {}", savedMember.get());
//            emailCertificationRepository.removeEmailVerificationNumber(registerDto.getEmail());
            return savedMember.get();
        }
        return null;
    }
}

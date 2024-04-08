package javaiscoffee.polaroad.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.exception.UnAuthorizedException;
import javaiscoffee.polaroad.login.emailAuthentication.EmailCertificationRequest;
import javaiscoffee.polaroad.login.emailAuthentication.MailSendService;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.security.RefreshTokenDto;
import javaiscoffee.polaroad.security.TokenDto;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "로그인 관련 API", description = "로그인에 관련된 API들 모음  - 담당자 윤지호, 박상현")
public class LoginController {
    private final LoginService loginService;
    private final MemberRepository memberRepository;
    private final MailSendService mailSendService;

    @Operation(summary = "로그인 API", description = "로그인할 때 사용하는 API \n ## 사용자 이메일 \n- 이메일 형식이어야 함 \n ## 사용자 비밀번호 \n- 8~20자 사이 \n- 소문자, 숫자, 특수문자 최소 1글자씩 있어야함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공했을 경우"),
            @ApiResponse(responseCode = "404", description = "로그인에 실패한 경우")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Validated @RequestBody RequestWrapperDto<LoginDto> requestDto, HttpServletResponse response) {
        LoginDto loginDto = requestDto.getData();
        log.info("로그인 요청 = {}",loginDto);
        TokenDto tokenDto = loginService.login(loginDto);
        //로그인 실패했을 경우 실패 Response 반환
        if(tokenDto == null) {
            throw new NotFoundException(ResponseMessages.LOGIN_FAILED.getMessage());
        }

        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "회원가입 API", description = "회원가입할 때 사용하는 API \n ## 사용자 이메일 \n- 이메일 형식이어야 함 \n ## 사용자 비밀번호 \n- 8~20자 사이 \n- 소문자, 숫자, 특수문자 최소 1글자씩 있어야함")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "이메일이 중복되거나 입력값이 형식에 맞지 않아서 회원가입 실패한 경우")
    })
    @PostMapping("/register")
    public ResponseEntity<Member> register(@Validated @RequestBody RequestWrapperDto<RegisterDto> requestDto) {
        RegisterDto registerDto = requestDto.getData();
        log.info("registerDto = {}", registerDto);
        Member registerdMember = loginService.register(registerDto);
        if(registerdMember ==null) {
            throw new BadRequestException(ResponseMessages.REGISTER_FAILED.getMessage());
        }
        return ResponseEntity.ok(registerdMember);
    }

    /**
     * access 토큰 30분짜리 재발급
     */
    @Operation(summary = "Access 토큰 재발급 API", description = "refresh 토큰으로 access 토큰 재발급하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급에 성공했을 경우"),
            @ApiResponse(responseCode = "401", description = "refresh 토큰이 잘못되었거나 + 입력값이 잘못되어서 재발급에 실패한 경우")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response, @RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getData().getRefreshToken();
        log.info("refreshToken 받음 = {}", refreshToken);
        //토큰 검증 후 30분짜리 일반 토큰 받아오기
        TokenDto tokenDto = loginService.refresh(refreshToken, response);
        if(tokenDto==null) {
            throw new UnAuthorizedException(ResponseMessages.UNAUTHORIZED.getMessage());
        }
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "회원가입할 때 이메일 중복 체크 API", description = "이메일이 중복되었는지 체크하는 API \n - 중복되었으면 true 반환 \n - 중복되지 않아서 회원가입이 가능하면 false 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "중복 체크에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "이메일 형식이 아닐 경우")
    })
    @PostMapping("/register/email-check")
    public ResponseEntity<Boolean> emailCheck(@Validated @RequestBody RequestWrapperDto<EmailCertificationRequest> requestDto) {
        EmailCertificationRequest emailCheckDto = requestDto.getData();
        log.info("이메일 체크 진입 = {}", emailCheckDto.getEmail());
        return ResponseEntity.ok(memberRepository.existsByEmail(emailCheckDto.getEmail()));
    }

    @Operation(summary = "로그인 페이지 비밀번호 재설정 API", description = "임시 비밀번호를 이메일로 보내는 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = "이메일과 이름이 다를 경우 + 서버 자체적으로 이메일 보내는데 실패하거나 + 임시 비밀번호 발급에 실패했을 경우"),
            @ApiResponse(responseCode = "404", description = "해당 이메일로 멤버가 존재하지 않을 경우")
    })
    @PostMapping("/login/reset-password")
    public ResponseEntity<String> resetPassword(@Validated @RequestBody RequestWrapperDto<ResetPasswordRequestDto> wrapperDto) {
        ResetPasswordRequestDto requestDto = wrapperDto.getData();
        loginService.resetPassword(requestDto);
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }

    @Operation(summary = "회원가입할 때 이메일 인증번호 요청 API", description = "회원가입할 때 인증번호를 이메일로 보내는 api \n ## 인증번호는 30분간 유효 \n ## 30초 이내로 같은 이메일로 요청하거나 회원가입되어 있는 중복 이메일이 존재할 경우 400 에러")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 발송에 성공했을 경우"),
            @ApiResponse(responseCode = "400", description = " - 30초 이내로 같은 이메일로 재요청했을 경우 \n - 이미 회원가입 된 이메일이 존재할 경우")
    })
    @PostMapping("/register/send-certification")
    public ResponseEntity<String> sendCertificationNumber(@Validated @RequestBody RequestWrapperDto<EmailCertificationRequest> requestDto) throws MessagingException, NoSuchAlgorithmException {
        EmailCertificationRequest request = requestDto.getData();

        log.info(">> 사용자의 이메일 인증 요청");
        mailSendService.sendEmailForCertification(request.getEmail());
        return ResponseEntity.ok(ResponseMessages.SUCCESS.getMessage());
    }
}
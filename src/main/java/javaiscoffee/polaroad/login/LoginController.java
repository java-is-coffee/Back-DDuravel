package javaiscoffee.polaroad.login;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.polaroad.response.ResponseStatus;
import javaiscoffee.polaroad.response.Status;
import javaiscoffee.polaroad.security.TokenDto;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 나중에 정식 배포하기 전에
 * 컨트롤러 파라미터에 검증해야하는 DTO에 @Valid 추가하기
 */

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "로그인 관련 API", description = "로그인에 관련된 API들 모음")
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "로그인 API", description = "로그인할 때 사용하는 API")
    @Parameter(name = "email", description = "사용자 이메일")
    @Parameter(name = "password", description = "사용자 비밀번호")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestWrapperDto<LoginDto> requestDto, HttpServletResponse response) {
        LoginDto loginDto = requestDto.getData();
        log.info("로그인 요청");
        loginService.login(loginDto,response);
        ResponseEntity.notFound();
        //로그인 실패했을 경우 실패 Response 반환
        return ResponseEntity.ok("");
    }

    @Hidden
    @PostMapping("test")
    public ResponseEntity<?> test() {
        log.info("토큰 테스트");
        return ResponseEntity.ok("토큰 인증 성공");
    }

    @Operation(summary = "회원가입 API", description = "회원가입할 때 사용하는 API")
    @Parameter(name = "email", description = "사용자 이메일")
    @Parameter(name = "password", description = "사용자 비밀번호")
    @Parameter(name = "name", description = "사용자 이름")
    @Parameter(name = "nickname", description = "사용자 닉네임")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestWrapperDto<RegisterDto> requestDto) {
        RegisterDto registerDto = requestDto.getData();
        log.info("registerDto = {}", registerDto);
        Member registerdMember = loginService.register(registerDto);
        if(registerdMember ==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.REGISTER_FAILED));
        }
        return ResponseEntity.ok(null);
    }
}
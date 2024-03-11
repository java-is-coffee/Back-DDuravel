package javaiscoffee.polaroad.login;

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
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestWrapperDto<LoginDto> requestDto, HttpServletResponse response) {
        LoginDto loginDto = requestDto.getData();
        log.info("로그인 요청");
        loginService.login(loginDto,response);
        //로그인 실패했을 경우 실패 Response 반환
        return ResponseEntity.ok("");
    }

    @PostMapping("test")
    public ResponseEntity<?> test() {
        log.info("토큰 테스트");
        return ResponseEntity.ok("토큰 인증 성공");
    }

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
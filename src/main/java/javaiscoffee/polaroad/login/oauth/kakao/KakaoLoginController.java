package javaiscoffee.polaroad.login.oauth.kakao;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.response.ResponseMessages;
import javaiscoffee.polaroad.security.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping()
public class KakaoLoginController {
    @Value("${kakao_api_key}")
    private String kakaoApiKey;
    @Value("${kakao_redirect_uri}")
    private String redirectUri;
    @Value("${kakao_result_uri}")
    private String resultUri;
    private final KakaoService kakaoService;
    private final LoginService loginService;

    @Autowired
    public KakaoLoginController(KakaoService kakaoService, LoginService loginService) {
        this.kakaoService = kakaoService;
        this.loginService = loginService;
    }

    @GetMapping("/api/oauth2/login/kakao")
    public void getAccessToken(HttpServletResponse response) {
        String uri = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoApiKey+"&redirect_uri="+redirectUri;
        try {
            response.sendRedirect(uri);
        } catch (IOException e) {
            log.error("카카오 로그인 redirect 오류");
            throw new NotFoundException(ResponseMessages.BAD_REQUEST.getMessage());
        }
    }
    @GetMapping("/api/oauth2/authorization/kakao")
    public void callback(@RequestParam(name = "code") String code,HttpServletResponse response) {
        log.info("카카오 로그인 코드 = {}",code);
        String accessToken = kakaoService.getAccessTokenFromKakao(kakaoApiKey, code);
        HashMap<String, Object> uerInfo = kakaoService.getUerInfo(accessToken);
        TokenDto tokenDto = loginService.oauthLogin(uerInfo);
        try {
            String redirectUrl = resultUri +
                    "?access_token=" + URLEncoder.encode(tokenDto.getAccessToken(), "UTF-8") +
                    "&refresh_token=" + URLEncoder.encode(tokenDto.getRefreshToken(), "UTF-8");
            response.sendRedirect(redirectUrl); // 리다이렉트
        } catch (IOException e) {
            throw new BadRequestException(ResponseMessages.ERROR.getMessage());
        }
    }
}

package javaiscoffee.polaroad.login.oauth.google;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping()
public class GoogleLoginController {
    @Value("${google_redirect_uri}")
    private String redirectUri;
    @Value("${google_result_uri}")
    private String resultUri;
    @Value("${google_client_id}")
    private String googleClientId;
    @Value("${google_client_secret}")
    private String googleClientSecret;
    private final GoogleService googleService;
    private final LoginService loginService;

    @Autowired
    public GoogleLoginController(GoogleService googleService, LoginService loginService) {
        this.googleService = googleService;
        this.loginService = loginService;
    }

    @GetMapping("/api/oauth2/login/google")
    public void getAccessToken(HttpServletResponse response) {
        try {
            String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth";
            String clientId = googleClientId;
            String encodedRedirectUri = URLEncoder.encode(redirectUri, "UTF-8");
            String responseType = "code";
            String scope = "openid email profile"; // 요구하는 권한에 따라 조정

            String uri = String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
                    googleAuthUrl, clientId, encodedRedirectUri, responseType, scope);

            response.sendRedirect(uri);
        } catch (IOException e) {
            log.error("구글 로그인 redirect 오류");
            throw new NotFoundException(ResponseMessages.BAD_REQUEST.getMessage());
        }
    }

    @GetMapping("/api/oauth2/authorization/google")
    public void callback(@RequestParam(name = "code") String code, HttpServletResponse response) {
        log.info("구글 로그인 코드 = {}",code);
        String accessToken = googleService.getAccessTokenFromGoogle(googleClientId, googleClientSecret, redirectUri, code);
        HashMap<String, Object> userInfo = googleService.getUerInfo(accessToken);
        TokenDto tokenDto = loginService.oauthLogin(userInfo);
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

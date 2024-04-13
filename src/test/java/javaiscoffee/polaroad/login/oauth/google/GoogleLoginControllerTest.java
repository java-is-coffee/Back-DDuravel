package javaiscoffee.polaroad.login.oauth.google;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.net.URLEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GoogleLoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Value("${google_redirect_uri}")
    private String redirectUri;
    @Value("${google_client_id}")
    private String googleClientId;

    @Test
    public void testGetAccessToken() throws Exception {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/auth";
        String clientId = googleClientId;
        String encodedRedirectUri = URLEncoder.encode(redirectUri, "UTF-8");
        String responseType = "code";
        String scope = "openid email profile"; // 요구하는 권한에 따라 조정

        String uri = String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
                googleAuthUrl, clientId, encodedRedirectUri, responseType, scope);

        MockHttpServletRequestBuilder builder = get("/api/oauth2/login/google");

        mockMvc.perform(builder)
                .andExpect(redirectedUrl(uri))
                .andReturn();
    }

//    @Test
//    public void testCallback() throws Exception {
//        String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        MockHttpServletRequestBuilder builder = get("/api/oauth2/authorization/google")
//                .param("code", code);
//
//        mockMvc.perform(builder)
//                .andExpect(status().isBadRequest())
//                .andReturn();
//    }
}
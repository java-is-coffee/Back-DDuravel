package javaiscoffee.polaroad.login.oauth.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(GoogleLoginController.class)
class GoogleLoginControllerTest {
    @Autowired
    private GoogleLoginController googleLoginController;

}